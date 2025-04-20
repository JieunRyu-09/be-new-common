package kr.co.triphos.chat;

import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.http.server.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Log4j2
public class CustomHandshakeInterceptor implements HandshakeInterceptor  {

	private final AuthService authService;  // 인증 서비스

	// scardy TODO 필요없으면 삭제하기
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		HttpSession session  =  servletRequest.getSession(false);
		return  true;
	}

	@Override
	public void afterHandshake(org.springframework.http.server.ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}
}
