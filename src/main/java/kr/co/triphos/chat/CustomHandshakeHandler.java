package kr.co.triphos.chat;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
	@Override
	protected Principal determineUser(ServerHttpRequest request,
									  WebSocketHandler wsHandler,
									  Map<String, Object> attributes) {
		Authentication auth = (Authentication) attributes.get("auth");
		if (auth != null && auth.isAuthenticated()) {
			return new StompPrincipal(auth.getName()); // 이 name이 memberId 역할
		}
		return null;
	}
}
