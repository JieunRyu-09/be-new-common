package kr.co.triphos.chat.service;

import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.dto.ChatRoomMemberDTO;
import kr.co.triphos.chat.entity.ChatRoom;
import kr.co.triphos.chat.entity.ChatRoomMember;
import kr.co.triphos.chat.repository.ChatRoomMemberRepository;
import kr.co.triphos.chat.repository.ChatRoomMsgRepository;
import kr.co.triphos.chat.repository.ChatRoomRepository;
import kr.co.triphos.common.entity.MenuInfo;
import kr.co.triphos.common.repository.MenuInfoRepository;
import kr.co.triphos.config.JwtUtil;
import kr.co.triphos.member.entity.Member;
import kr.co.triphos.member.entity.MenuMemberAuth;
import kr.co.triphos.member.repository.MemberRepository;
import kr.co.triphos.member.repository.MenuMemberAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomMsgRepository chatRoomMsgRepository;

    @Transactional
    public boolean createChatRoom(ChatRoomDTO chatRoomDTO, String memberId) throws Exception {
        LocalDateTime nowDate = LocalDateTime.now();

        // 채팅방 생성
        int memberCnt = chatRoomDTO.getInviteMemberIdList().size() + 1;
        ChatRoom chatRoom = ChatRoom.createChatRoom()
                .memberId(memberId)
                .title(chatRoomDTO.getTitle())
                .memberCnt(memberCnt)
                .build();
        chatRoomRepository.save(chatRoom);
        chatRoomRepository.flush();

        // 채팅방에 사람 추가
        List<ChatRoomMember> chatRoomMemberList = new ArrayList<>();
        int roomIdx = chatRoom.getRoomIdx();
        // 생성자 추가
        ChatRoomMember chatRoomCreator = ChatRoomMember.createChatRoomMember()
                .roomIdx(roomIdx)
                .memberId(memberId)
                .inviteId(memberId)
                .build();
        chatRoomMemberList.add(chatRoomCreator);
        // 초대받은 사람들 추가
        chatRoomDTO.getInviteMemberIdList().forEach(chatRoomMemberId -> {
            ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember()
                    .roomIdx(roomIdx)
                    .memberId(chatRoomMemberId)
                    .inviteId(memberId)
                    .build();
            chatRoomMemberList.add(chatRoomMember);
        });
        chatRoomMemberRepository.saveAll(chatRoomMemberList);
        return true;
    }

    @Transactional
    public boolean updateChatRoom(ChatRoomDTO chatRoomDTO, String memberId) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(chatRoomDTO.getRoomIdx())
                .orElseThrow(() -> new RuntimeException("채팅방 정보를 다시 확인하여주십시오."));
        chatRoom.setTitle(chatRoomDTO.getTitle());
        chatRoom.setUpdId(memberId);
        chatRoom.setUpdDt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        return true;
    }

    @Transactional
    public boolean inviteMember(ChatRoomMemberDTO chatRoomMemberDTO, String inviteId) {
        List<ChatRoomMember> chatRoomMemberList = new ArrayList<>();
        LocalDateTime nowDate = LocalDateTime.now();
        int roomIdx = chatRoomMemberDTO.getRoomIdx();
        int newMemberCnt = chatRoomMemberDTO.getInviteMemberIdList().size();

        // 채팅방 인원수 증가
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdx(roomIdx)
                .orElseThrow(() -> new RuntimeException("채팅방 정보를 다시 확인하여주십시오."));
        int memberCnt = chatRoom.getMemberCnt();
        chatRoom.setMemberCnt(memberCnt + newMemberCnt);
        chatRoomRepository.save(chatRoom);

        // 채팅방에 인원 추가
        chatRoomMemberDTO.getInviteMemberIdList().forEach(memberId -> {
            ChatRoomMember chatRoomMember = ChatRoomMember.createChatRoomMember()
                    .roomIdx(roomIdx)
                    .memberId(memberId)
                    .inviteId(inviteId)
                    .build();
            chatRoomMemberList.add(chatRoomMember);
        });
        chatRoomMemberRepository.saveAll(chatRoomMemberList);
        return true;
    }

}
