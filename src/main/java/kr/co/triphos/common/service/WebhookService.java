package kr.co.triphos.common.service;

import kr.co.triphos.common.dto.GitTeaWebhookDTO;

import java.util.List;

public interface WebhookService {

    // 이벤트 분석
    void analyzeEvent(GitTeaWebhookDTO gitTeaWebhookDTO);

    // 복수 채팅방 생성
    void createChatRooms(List<String> memberIds);

    // 채팅방 생성
    void createChatRoom(String memberId);

    // 채팅방에 메세지 발송
    void sendMessageToChatRoom(int roomIdx, String message);
}
