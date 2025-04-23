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
import org.springframework.security.core.AuthenticationException;
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
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		// 헤더정보 확인
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new WebSocketAuthException("헤더정보가 없습니다.");
		}

		// 토큰 확인
		String token = authHeader.substring(7);
		if (token.isEmpty()) {
			throw new WebSocketAuthException("토큰정보가 없습니다.");
		}

		// 정보확인
		Authentication auth = null;
		String memberId = null;
		try {
			authService.checkToken(token);
			auth = authService.getAuthenticationByToken(token);
			memberId = authService.getMemberIdByToken(token);
		}
		catch (Exception ex) {
			throw new WebSocketAuthException(ex.getMessage());
		}


		Authentication authentication = authService.getAuthenticationByToken(token);
		accessor.setUser(authentication);

		return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
	}

	public class WebSocketAuthException extends RuntimeException {
		public WebSocketAuthException(String message) {
			super(message);
		}
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
