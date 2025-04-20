package kr.co.triphos.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
	private final AuthService authService;
	private final ObjectMapper objectMapper;
	private final RedisService redisService;
	private ChatWebSocketService chatWebSocketService;

	@Autowired
	public void setChatWebSocketService(@Lazy ChatWebSocketService chatWebSocketService) {
		this.chatWebSocketService = chatWebSocketService;
	}

	@Override
	// TODO scardy 보안로직 추가 필요
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// jwt Token 존재여부 검사
		String authHeader = accessor.getFirstNativeHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return message;
		}

		String token = authHeader.substring(7);
		Authentication auth = authService.getAuthenticationByToken(token);
		String memberId = authService.getMemberIdByToken(token);



		Authentication authentication = authService.getAuthenticationByToken(token);

		// Principal 설정 (이후 Controller에서 사용 가능)
		accessor.setUser(authentication);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			try {
				accessor.setUser(authentication);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
				if (sessionAttributes != null) {
					sessionAttributes.put("auth", authentication);
				}
				accessor.setUser(new StompPrincipal(memberId));
			} catch (Exception e) {
				log.error("❌ STOMP 인증 실패 :: {}", e.getMessage());
				throw new IllegalArgumentException("CONNECT 실패: 인증 불가");
			}
		}

		return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
	}

	private void sendErrorToClient(String sessionId, String msg, int errorCode) {
		StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
		accessor.setSessionId(sessionId);
		accessor.setLeaveMutable(true);
		accessor.setHeader("errorCode", errorCode);

		ResponseDTO dto = new ResponseDTO(false, msg);
		try {
			String json = objectMapper.writeValueAsString(dto);
			Message<byte[]> message = MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
			//messagingTemplate.send("/error-virtual", message); // destination은 의미 없음, 직접 세션에 푸시됨
		} catch (Exception e) {
			log.error("에러 전송 실패", e);
		}
	}
}
