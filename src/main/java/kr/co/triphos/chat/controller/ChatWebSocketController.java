package kr.co.triphos.chat.controller;

import kr.co.triphos.chat.dto.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

	@MessageMapping("/chat") // 클라이언트가 "/app/chat"으로 메시지 전송하면 실행됨
	@SendTo("/topic/messages") // 모든 구독자에게 메시지를 브로드캐스트
	public ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO) {
		System.out.println("📩 받은 메시지: " + chatMessageDTO.getContent());
		return chatMessageDTO; // 메시지를 그대로 클라이언트에게 전송
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO) {
		chatMessageDTO.setContent(chatMessageDTO.getSender() + " 님이 입장하셨습니다.");
		return chatMessageDTO;
	}

}
