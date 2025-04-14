package kr.co.triphos.chat;

import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Log4j2
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

	private final AuthService authService;  // 인증 서비스

	@Override
	public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		URI uri = request.getURI();
		String query = uri.getQuery(); // query 파라미터에서 token 값 추출

		// query param에 token이 포함되어 있는지 확인
		if (query != null && query.startsWith("token=")) {
			String token = URLDecoder.decode(query.substring("token=".length()), String.valueOf(StandardCharsets.UTF_8));
			try {
				Authentication auth = authService.getAuthenticationByToken(token);
				SecurityContextHolder.getContext().setAuthentication(auth);
				attributes.put("auth", auth);
				return true;
			}
			catch (Exception ex) {
				log.error(ex.getMessage());
				return false;
			}
		}

		// 인증되지 않은 경우 false를 반환하여 핸드셰이크를 차단
		return false;
	}

	@Override
	public void afterHandshake(org.springframework.http.server.ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}
}
