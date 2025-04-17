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
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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
	// TODO scardy 기능동작 이후 리팩터링 작업 진행 예정
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// jwt Token 존재여부 검사
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		StompCommand command = accessor.getCommand();

		// 구독해제는 헤더에 토큰을 안보냄.
		// sessionId로 작업
		if (StompCommand.UNSUBSCRIBE.equals(command)) {
			String sessionId = accessor.getSessionId();
			String memberId = redisService.getData("chat:sesseionId:" + sessionId);
			int roomIdx = Integer.parseInt(redisService.getData(memberId + ":chatRoom"));

			redisService.delData(memberId + ":chatRoom");
			redisService.delData("chat:" + memberId+ ":" + roomIdx + "msg_idx");
			redisService.delData("chat:sesseionId:" + sessionId);
			return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
		}

		String token = authHeader.substring(7);
		Authentication auth = authService.getAuthenticationByToken(token);
		String memberId = authService.getMemberIdByToken(token);

		// 구독할 경우 redis에 엔드포인드 | memberId로 정보 저장
		// 구독중(읽고있는중)인 채팅방의 경우 안읽은 메세지 수 증가 안하기 위함
		if (StompCommand.SUBSCRIBE.equals(command)) {
			String destination = accessor.getDestination();
			if (destination == null || destination.isEmpty()) {
				return null;
			}
			if (destination.matches("^/topic/chat/\\d+$")) {
				try {
					String[] parts = destination.split("/");
					String roomIdx = parts[parts.length - 1];
					String sessionId = accessor.getSessionId();
					redisService.delData(memberId + ":chatRoom");
					redisService.saveData(memberId + ":chatRoom", roomIdx);
					redisService.saveData("chat:sesseionId:" + sessionId, memberId);
					chatWebSocketService.setUnreadCount(memberId, Integer.parseInt(roomIdx));
				}
				catch (Exception ex) {
					log.error(ex);
				}
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
