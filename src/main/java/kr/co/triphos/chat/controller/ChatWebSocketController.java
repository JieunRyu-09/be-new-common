package kr.co.triphos.chat.controller;

import kr.co.triphos.chat.dto.ChatMessageDTO;
import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.service.AuthenticationFacadeService;
import kr.co.triphos.member.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.projection.Accessor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatWebSocketController {
	private final ChatWebSocketService chatWebSocketService;


	@MessageMapping("/chat/{roomIdx}")
	public ChatMessageDTO sendMessage(@DestinationVariable int roomIdx, ChatMessageDTO chatMessageDTO, Principal principal) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			CustomUserDetails customUserDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
			String memberId = customUserDetails.getMemberId();
			chatWebSocketService.receiveMessage(memberId, roomIdx, chatMessageDTO);
		}
		catch (Exception ex) {
			log.info("CUSTOMUSER :: " + (CustomUserDetails) ((Authentication) principal).getPrincipal());
			log.error(ex.getMessage());
		}
		System.out.println("📩 받은 메시지: " + chatMessageDTO.getContent());
		return chatMessageDTO;
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO) {
		chatMessageDTO.setContent(chatMessageDTO.getMemberNm() + " 님이 입장하셨습니다.");
		return chatMessageDTO;
	}

}
