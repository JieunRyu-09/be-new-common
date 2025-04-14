package kr.co.triphos.chat.service;

import kr.co.triphos.chat.dao.ChatDAO;
import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.chat.entity.ChatRoom;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.entity.ChatRoomMsg;
import kr.co.triphos.chat.repository.ChatRoomMemberRepository;
import kr.co.triphos.chat.repository.ChatRoomMsgRepository;
import kr.co.triphos.chat.repository.ChatRoomRepository;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatWebSocketService {
    private final ChatDAO chatDAO;

    private final SimpMessagingTemplate messagingTemplate;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomMsgRepository chatRoomMsgRepository;

    private final RedisService redisService;

    private SimpUserRegistry simpUserRegistry;


    @Transactional
    public boolean receiveMessage(String memberId, int roomIdx, ChatMessageDTO chatMessageDTO) throws Exception{
        String content = chatMessageDTO.getContent();
        LocalDateTime nowDate = LocalDateTime.now();

        // 메세지 전송자 설정
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("잘못된 사용자입니다."));
        chatMessageDTO.setMemberId(member.getMemberId());
        chatMessageDTO.setMemberNm(member.getMemberNm());

        ChatRoomMsg chatRoomMsg = ChatRoomMsg.createTextChatRoomMsg()
                .roomIdx(roomIdx)
                .memberId(memberId)
                .content(content)
                .build();
        // 메세지 저장
        chatRoomMsgRepository.save(chatRoomMsg);

        // 채팅방 정보 업데이트 (메세지 정보 업데이트)
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(roomIdx)
                .orElseThrow(() -> new RuntimeException("잘못된 채팅방 정보입니다."));
        chatRoom.setLastChatMemberId(memberId);
        chatRoom.setLastChatDt(nowDate);
        chatRoom.setLastChatMsg(content);
        chatRoomRepository.save(chatRoom);

        ChatRoomInfoDTO chatRoomInfoDTO = ChatRoomInfoDTO.createChatRoomInfo()
                .roomIdx(roomIdx)
                .title(chatRoom.getTitle())
                .memberCnt(chatRoom.getMemberCnt())
                .lastChatMsg(chatRoom.getLastChatMsg())
                .unreadCount(0)
                .build();

        // 채팅방 맴버별 안읽은 메세지 수 증가 및 채팅목록 send
        updateChatRoomMemberUnreadCount(roomIdx, chatRoomInfoDTO);

        // 채팅방의 멤버들에게 메세지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomIdx, chatMessageDTO);
        //messagingTemplate.convertAndSend("/topic/chat-room-list/" + roomIdx, chatMessageDTO);
        return true;
    }

    public void updateChatRoomMemberUnreadCount(int roomIdx, ChatRoomInfoDTO chatRoomInfoDTO) throws Exception {
        // 해당 채팅방에서 나가지 않은 사람만 조회
        List<ChatRoomMember> chatRoomMemberList = chatRoomMemberRepository.findByPkRoomIdxAndDelYn(roomIdx, "N");
        // 추후 안읽은 메세지수 업데이트할 사람 목록
        List<ChatRoomMember> updateChatRoomMemberList = new ArrayList<>();

        chatRoomMemberList.forEach(chatRoomMember -> {
            // redis에서 사용자의 구독중인 roomIdx 조회
            String memberId = chatRoomMember.getPk().getMemberId();
            String redisValue = redisService.getData(memberId + "chatRoom");
            int participateRoomIdx = (redisValue != null) ? Integer.parseInt(redisValue) : -1; // 기본값 -1 사용

            // 사용자가 구독중인 roomIdx랑 현재 메세지가 전송된 방의 roomIdx랑 일치하지 않는다면
            // 안읽은 메세지 수 증가
            if (participateRoomIdx != roomIdx) {
                int unreadCount = chatRoomMember.getUnreadCount() + 1;
                chatRoomMember.setUnreadCount(unreadCount);
                updateChatRoomMemberList.add(chatRoomMember);
                // 채팅방 채널에 안읽은 메세지 수 설정
                chatRoomInfoDTO.setUnreadCount(unreadCount);
            }


            // 채팅방 채널에 발송
            messagingTemplate.convertAndSendToUser(memberId,"/queue/unread", chatRoomInfoDTO);
        });

        // 업데이트 해야될 사람이 있으면 업데이트
        if (!updateChatRoomMemberList.isEmpty()) {
            chatRoomMemberRepository.saveAll(updateChatRoomMemberList);
        }
    }

}
