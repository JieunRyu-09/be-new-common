package kr.co.triphos.common.service.impl;

import kr.co.triphos.chat.dto.ChatRoomDTO;
import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.common.dto.GitTeaWebhookDTO;
import kr.co.triphos.common.service.WebhookService;
import kr.co.triphos.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

// GitTea용 Webhook 관련 로직 처리
@Log4j2
@Service
@RequiredArgsConstructor
public class GitTeaWebhookServiceImpl implements WebhookService {

    private final WebClient gitTeaWebClient;

    private final MemberRepository memberRepository;

    private final ChatService chatService;

    // ChatRoomDTO 생성
    private ChatRoomDTO createChatRoomDTO(String title) {
        return ChatRoomDTO.builder()
                .title(title)
                .chatRoomType("GROUP")
                .memberCnt(2)
                .build();
    }

    @Override
    public void analyzeEvent(GitTeaWebhookDTO gitTeaWebhookDTO) {
        log.info(gitTeaWebhookDTO);
    }

    @Override
    public void createChatRooms(List<String> memberIds) {

    }

    @Override
    public void createChatRoom(String memberId) {

    }

    @Override
    public void sendMessageToChatRoom(int roomIdx, String message) {

    }
}
