package kr.co.triphos.chat.listener;

import kr.co.triphos.chat.service.ChatService;
import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Log4j2
@RequiredArgsConstructor
public class StompEventListener {
	@Value("${chat.send-msg}")
	private String sendMsgUrl;
	@Value("${chat.unread-chat-room}")
	private String unreadChatRoomUrl;
	@Value("${chat.watching}")
	private String watchingUrl;

	private final RedisService redisService;
	private final AuthService authService;
	private final ChatWebSocketService chatWebSocketService;

	@EventListener
	public void handleConnect(SessionConnectEvent event) throws Exception{
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		assert authHeader != null;

		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);
		String sessionId = accessor.getSessionId();

		String redisId = chatWebSocketService.getConnectRedisId(memberId);
		redisService.delData(redisId);
		/** 사용자 정보로 세션ID조회 */
		redisService.saveData(redisId, sessionId);

		Date tokenTime = authService.getTokenExpireTime(token);
		long now = System.currentTimeMillis();                  // 현재 시간 (밀리초 단위)
		long expireTime = tokenTime.getTime();                  // 만료 시간 (밀리초 단위)
		long diffMillis = expireTime - now;                     // 남은 시간 (밀리초)


		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			chatWebSocketService.sendErrorToUser(memberId, 402, "로그인이 만료되었습니다.");
		}, diffMillis, TimeUnit.MILLISECONDS);
		/**
		 * 세션ID로 사용자 정보 조회
		 * 강제종료 등 handleSessionDisconnect 발생 시
		 * 세션ID로 사용자 정보 조회 후
		 * 해당 사용자와 관련된 redis데이터 제거 진행
		 * */
		redisService.saveData(sessionId, memberId);
	}

	@EventListener
	public void handleSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		assert authHeader != null;

		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);
		String sessionId = accessor.getSessionId();

		String regex = "^" + Pattern.quote(sendMsgUrl) + "(\\d+)?$";
		if (destination.matches(regex)) {
			try {
				String[] parts = destination.split("/");
				String roomIdx = parts[parts.length - 1];
				String redisId = chatWebSocketService.getWatchingRoomRedisId(memberId);
				redisService.saveData(redisId, roomIdx);
				chatWebSocketService.setUnreadCount(memberId, Integer.parseInt(roomIdx));
			}
			catch (Exception ex) {
				log.error(ex);
			}
		}
		else if (destination.matches(unreadChatRoomUrl)){
			String redisId = chatWebSocketService.getUnreadChatRoomRedisId(memberId);
			redisService.saveData(redisId, unreadChatRoomUrl);
		}
	}

	@EventListener
	public void handleUnsubscribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		String destination = accessor.getFirstNativeHeader("x-destination");

		// preSend에서 검사
		assert authHeader != null;
		if (destination == null || destination.isEmpty()) {
			throw new MessageHandlingException(event.getMessage(), "구독해제 경로가 없습니다.");
		}

		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);

		String regex = "^" + Pattern.quote(sendMsgUrl) + "(\\d+)?$";
		if (destination.matches(regex)) {
			String[] parts = destination.split("/");
			String roomIdx = parts[parts.length - 1];

			String roomRedisId = chatWebSocketService.getWatchingRoomRedisId(memberId);
			String msgRedisId = chatWebSocketService.getWatchingRoomMsgRedisId(memberId, roomIdx);
			redisService.delData(roomRedisId);
			redisService.delData(msgRedisId);
		}
		else if (destination.matches(unreadChatRoomUrl)){
			String redisId = chatWebSocketService.getUnreadChatRoomRedisId(memberId);
			redisService.delData(redisId);
		}
	}

	@EventListener
	public void handleSessionDisconnect(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		String memberId = redisService.getData(sessionId);

		String sessionRedisId = chatWebSocketService.getConnectRedisId(memberId);
		String roomRedisId = chatWebSocketService.getWatchingRoomRedisId(memberId);
		String msgRedisId = chatWebSocketService.getWatchingRoomMsgRedisId(memberId, roomRedisId);

		String roomIdx = redisService.getData(roomRedisId);
		redisService.delData(sessionRedisId);
		redisService.delData(roomRedisId);
		redisService.delData(msgRedisId);
	}
}
