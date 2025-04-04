package kr.co.triphos.chatting;

import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
	private final AuthService authService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String authHeader = accessor.getFirstNativeHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			String memberId = authService.getMemberIdByToken(token);
			log.info("{}");
			System.out.println("✅${}: " + token);

			// JWT 검증 로직 추가 (SecurityContext에 저장 가능)
		} else {
			System.out.println("❌ Authorization 헤더 없음");
		}
		return message;
	}
}
