package kr.co.triphos.chatting;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
								   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

			// ❌ GET 방식 WebSocket Upgrade 요청에서는 Authorization 헤더가 없음
			String authHeader = httpServletRequest.getHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				attributes.put("token", authHeader.substring(7)); // "Bearer " 제거 후 저장
			}
		}
		return true;
	}


	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
							   WebSocketHandler wsHandler, Exception exception) {
		// 필요하면 후처리 가능
	}
}
