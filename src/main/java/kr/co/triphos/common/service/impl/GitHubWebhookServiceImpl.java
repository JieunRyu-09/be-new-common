package kr.co.triphos.common.service.impl;

import kr.co.triphos.common.dto.GitTeaWebhookDTO;
import kr.co.triphos.common.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubWebhookServiceImpl implements WebhookService {
    @Override
    public void analyzeEvent(GitTeaWebhookDTO gitTeaWebhookDTO) {

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
