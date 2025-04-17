package kr.co.triphos.chat.service;

import kr.co.triphos.chat.dao.ChatDAO;
import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.dto.ChatRoomInfoDTO;
import kr.co.triphos.chat.entity.ChatRoom;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.entity.ChatRoomMsg;
import kr.co.triphos.chat.entity.pk.ChatRoomMemberPK;
import kr.co.triphos.chat.repository.ChatRoomMemberRepository;
import kr.co.triphos.chat.repository.ChatRoomMsgRepository;
import kr.co.triphos.chat.repository.ChatRoomRepository;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    private final RedisService redisService;

    @Transactional
    public void receiveMessage(String memberId, int roomIdx, ChatMessageDTO chatMessageDTO) throws Exception{
        String content = chatMessageDTO.getContent();
        LocalDateTime nowDate = LocalDateTime.now();

        // 메세지 전송자 설정
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("잘못된 사용자입니다."));
        chatMessageDTO.setMemberId(member.getMemberId());
        chatMessageDTO.setMemberNm(member.getMemberNm());
        chatMessageDTO.setSendTime(nowDate);
        chatMessageDTO.setBundleYn("N");

        ChatRoomMsg chatRoomMsg = ChatRoomMsg.createTextChatRoomMsg()
                .roomIdx(roomIdx)
                .memberId(memberId)
                .content(content)
                .messageType(chatMessageDTO.getType().toString())
                .build();
        // 메세지 저장
        chatRoomMsgRepository.save(chatRoomMsg);
        chatRoomMsgRepository.flush();
        chatMessageDTO.setMsgIdx(chatRoomMsg.getMsgIdx());

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
                .chatRoomType(chatRoom.getChatRoomType())
                .memberCnt(chatRoom.getMemberCnt())
                .lastChatMsg(chatRoom.getLastChatMsg())
                .unreadMessageCount(0)
                .lastChatDt(nowDate)
                .build();

        // 채팅방 맴버별 안읽은 메세지 수 증가 및 채팅목록 send
        updateChatRoomMemberUnreadCount(roomIdx, chatRoomInfoDTO);

        // 채팅방의 멤버들에게 메세지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + roomIdx, chatMessageDTO);
    }

    @Transactional
    public Map<String, Object> receiveFilesMessage(String memberId, int roomIdx, String messageType, String bundleYn) throws Exception{
        Map<String, Object> returnData = new HashMap<>();
        LocalDateTime nowDate = LocalDateTime.now();

        String content = null;

        if (bundleYn.equals("Y")) {
            if (messageType.equals("IMG")) content = "묶음이미지를 보냈습니다";
            else content = "묶음파일을 보냈습니다.";
        }
        else {
            if (messageType.equals("IMG")) content = "이미지를 보냈습니다";
            else content = "파일을 보냈습니다.";
        }


        ChatRoomMsg chatRoomMsg = ChatRoomMsg.createFilesChatRoomMsg()
                .roomIdx(roomIdx)
                .memberId(memberId)
                .content(content)
                .messageType(messageType)
                .bundleYn(bundleYn)
                .build();
        // 메세지 저장
        chatRoomMsgRepository.save(chatRoomMsg);
        chatRoomMsgRepository.flush();
        int msgIdx = chatRoomMsg.getMsgIdx();

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
                .lastChatDt(nowDate)
                .unreadMessageCount(0)
                .build();
        returnData.put("chatRoomInfoDTO", chatRoomInfoDTO);
        returnData.put("msgIdx", msgIdx);
        returnData.put("content", content);
        returnData.put("messageType", messageType);
        return returnData;
    }

    @Transactional
    public void updateChatRoomMemberUnreadCount(int roomIdx, ChatRoomInfoDTO chatRoomInfoDTO) throws Exception {
        // 해당 채팅방에서 나가지 않은 사람만 조회
        List<ChatRoomMember> chatRoomMemberList = chatRoomMemberRepository.findByPkRoomIdxAndDelYn(roomIdx, "N");

        List<String >participantIds = new ArrayList<>();
        chatRoomMemberList.forEach(chatRoomMember -> {
            participantIds.add(chatRoomMember.getPk().getMemberId());
        });
        // 추후 안읽은 메세지수 업데이트할 사람 목록
        List<ChatRoomMember> updateChatRoomMemberList = new ArrayList<>();

        chatRoomMemberList.forEach(chatRoomMember -> {
            // redis에서 사용자의 구독중인 roomIdx 조회
            String memberId = chatRoomMember.getPk().getMemberId();
            String redisValue = redisService.getData(memberId + ":chatRoom");
            int participateRoomIdx = (redisValue != null) ? Integer.parseInt(redisValue) : -1; // 기본값 -1 사용

            // 사용자가 구독중인 roomIdx랑 현재 메세지가 전송된 방의 roomIdx랑 일치하지 않는다면
            // 안읽은 메세지 수 증가
            if (participateRoomIdx != roomIdx) {
                int unreadCount = chatRoomMember.getUnreadCount() + 1;
                chatRoomMember.setUnreadCount(unreadCount);
                updateChatRoomMemberList.add(chatRoomMember);
                // 채팅방 채널에 안읽은 메세지 수 설정
                chatRoomInfoDTO.setUnreadMessageCount(unreadCount);
            } else {
                chatRoomInfoDTO.setUnreadMessageCount(0);
            }
            sendToUser(memberId, "/queue/unread", chatRoomInfoDTO);
        });

        // 업데이트 해야될 사람이 있으면 업데이트
        if (!updateChatRoomMemberList.isEmpty()) {
            chatRoomMemberRepository.saveAll(updateChatRoomMemberList);
        }
    }

    @Transactional
    public void setUnreadCount(String memberId, int roomIdx) throws Exception {
        ChatRoomMemberPK pk = new ChatRoomMemberPK(roomIdx, memberId);
        // 채팅방 정보와 사용자의 채팅방 정보 조회
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(roomIdx).orElseThrow(() -> new RuntimeException("잘못된 채팅방 정보입니다."));
        List<String >participantIds = new ArrayList<>();
        chatRoomMemberRepository.findByPkRoomIdxAndDelYn(roomIdx, "N").forEach(chatRoomMember -> {
            participantIds.add(chatRoomMember.getPk().getMemberId());
        });
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByPk(pk);

        // 사용자의 채팅방 정보의 안읽은 메세지 수 0으로 저장
        chatRoomMember.setUnreadCount(0);
        chatRoomMemberRepository.save(chatRoomMember);

        // 사용자의 채팅방 정보 최신화 broadcast
        ChatRoomInfoDTO chatRoomInfoDTO = ChatRoomInfoDTO.createChatRoomInfo()
                .roomIdx(roomIdx)
                .title(chatRoom.getTitle())
                .memberCnt(chatRoom.getMemberCnt())
                .lastChatMsg(chatRoom.getLastChatMsg())
                .lastChatDt(chatRoom.getLastChatDt())
                .unreadMessageCount(0)
                .build();
        sendToUser(memberId, "/queue/unread", chatRoomInfoDTO);
    }

    public void sendToChannel(String destination, Object object) {
        messagingTemplate.convertAndSend(destination, object);
    }

    public void sendToUser(String memberId, String destination, Object object) {
        messagingTemplate.convertAndSendToUser(memberId, destination, object);
    }

}
