package kr.co.triphos.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.security.auth.message.AuthException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
	@Value("${chat.send-msg}")
	private String sendMsgUrl;
	@Value("${chat.unread-chat-room}")
	private String unreadChatRoomUrl;
	@Value("${chat.error-msg}")
	private String errorMsgUrl;

	private final AuthService authService;
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
		String destination = accessor.getDestination();

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new MessageHandlingException(message, "헤더정보가 없습니다.");
		}

		// 토큰 확인
		String token = authHeader.substring(7);
		if (token.isEmpty()) {
			throw new MessageHandlingException(message, "토큰정보가 없습니다.");
		}


		// 정보확인
		Authentication auth = null;
		String memberId = null;
		try {
			authService.checkToken(token);
			Authentication authentication = authService.getAuthenticationByToken(token);
			accessor.setUser(authentication);
			memberId = authService.getMemberIdByToken(token);
		}
		catch (Exception ex) {
			throw new MessageHandlingException(message, "로그인이 만료되었습니다.");
		}


		if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
			try {
				checkSubscribeAuth(destination, memberId);
			}
			catch (AuthException ex) {
				chatWebSocketService.sendErrorToUser(memberId, 403, ex.getMessage());
			}
			catch (RuntimeException ex) {
				chatWebSocketService.sendErrorToUser(memberId, 404, ex.getMessage());
			}
			catch (Exception ex) {
				log.error(ex);
				chatWebSocketService.sendErrorToUser(memberId, 500, "서버에 문제가 발생하였습니다.");
			}
		}



		return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
	}

	private void checkSubscribeAuth(String destination, String memberId) throws Exception{
		String regex = "^" + Pattern.quote(sendMsgUrl) + "(\\d+)?$";

		if (destination.isEmpty()) {
			throw new RuntimeException("구독경로가 없습니다.");
		}
		else if (destination.matches(regex)) {
			String[] parts = destination.split("/");
			String roomIdx = parts[parts.length - 1];
			// 구독하려는 방의 멤버인지 확인
			boolean isMember = chatWebSocketService.checkMemberRoom(memberId, roomIdx);
			if (!isMember) {
				throw new AuthException("초대받지 않은 채팅방입니다.");
			}
		}
		else if (destination.matches(unreadChatRoomUrl) || destination.matches(errorMsgUrl)){

		}
		else {
			throw new RuntimeException("잘못된 구독경로입니다.");
		}
	}
}
