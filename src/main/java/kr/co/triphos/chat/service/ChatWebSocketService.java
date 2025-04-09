package kr.co.triphos.chat.service;

import kr.co.triphos.chat.dao.ChatDAO;
import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.chat.dto.ChatRoomMemberDTO;
import kr.co.triphos.chat.entity.ChatRoom;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.entity.ChatRoomMsg;
import kr.co.triphos.chat.repository.ChatRoomMemberRepository;
import kr.co.triphos.chat.repository.ChatRoomMsgRepository;
import kr.co.triphos.chat.repository.ChatRoomRepository;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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


    @Transactional
    public boolean receiveMessage(String memberId, int roomIdx, ChatMessageDTO chatMessageDTO) throws Exception{
        String content = chatMessageDTO.getContent();
        LocalDateTime nowDate = LocalDateTime.now();

        // 메세지 전송자 설정
        String memberNm = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("잘못된 사용자입니다.")).getMemberNm();
        chatMessageDTO.setSender(memberNm);

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

        // 채팅방 맴버별 안읽은 메세지 수 증가

        // 채팅방의 멤버들에게 메세지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomIdx, chatMessageDTO);
        //messagingTemplate.convertAndSend("/topic/chat-room-list/" + roomIdx, chatMessageDTO);
        return true;
    }

}
