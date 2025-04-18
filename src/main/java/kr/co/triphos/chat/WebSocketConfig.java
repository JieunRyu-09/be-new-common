package kr.co.triphos.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final WebSocketAuthChannelInterceptor stompInterceptor;
	private final CustomHandshakeInterceptor customHandshakeInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 클라이언트가 메시지를 받을 수 있는 경로 설정
		// topic = 채팅 메세지 채널
		config.enableSimpleBroker("/topic", "/queue");

		// 클라이언트에서 메시지를 보낼 때 사용하는 prefix
		config.setApplicationDestinationPrefixes("/app");

		// 사용자정보
		// sendToUser 시 사용
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// WebSocket 엔드포인트 설정 (프론트엔드에서 연결할 주소)
		registry
				.addEndpoint("/ws")
				.addInterceptors(customHandshakeInterceptor) // ✅ 인터셉터 등록
				.setHandshakeHandler(new CustomHandshakeHandler()) // 🔸 여기!
				.setAllowedOrigins("*");
	}

	@Override
	public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
		registration.interceptors(stompInterceptor); // ✅ STOMP 인터셉터 등록
	}



}
