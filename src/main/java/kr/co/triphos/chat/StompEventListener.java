package kr.co.triphos.chat;

import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.naming.AuthenticationException;
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
		redisService.saveData(redisId, sessionId);

		log.info("-------------CONNECT------------------------");
		log.info("SESSION_ID :: " + sessionId);
		log.info("AUTH_HEADER :: " + authHeader);
		log.info("TOKEN ::" + token);
		log.info("MEMBER_ID ::" + memberId);
		log.info("-----------------------------------------------");
		/*
			/user/queue/unread 이 경로도 원래는 구독을 해야했나

		 */
	}

	@EventListener
	public void handleSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		assert authHeader != null;
		if (destination == null) {
			throw new RuntimeException("구독경로가 없습니다.");
		}

		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);
		String sessionId = accessor.getSessionId();

		log.info("-------------SUBSCRIBE------------------------");
		log.info("SESSION_ID :: " + sessionId);
		log.info("AUTH_HEADER :: " + authHeader);
		log.info("DESTINATION :: " + destination);

		String regex = "^" + Pattern.quote(sendMsgUrl) + "(/\\d+)?$";
		if (destination.matches(regex)) {
			try {
				String[] parts = destination.split("/");
				String roomIdx = parts[parts.length - 1];
				String redisId = chatWebSocketService.getWatchingRoomRedisId(memberId);
				redisService.saveData(redisId, roomIdx);
				chatWebSocketService.setUnreadCount(memberId, Integer.parseInt(roomIdx));
				log.info("ROOM_IDX :: " + roomIdx);
			}
			catch (Exception ex) {
				log.error(ex);
			}
		}
		else if (destination.matches(unreadChatRoomUrl)){
			String redisId = chatWebSocketService.getUnreadChatRoomRedisId(memberId);
			redisService.saveData(redisId, unreadChatRoomUrl);
		}

		log.info("TOKEN ::" + token);
		log.info("MEMBER_ID ::" + memberId);
		log.info("-----------------------------------------------");
	}

	@EventListener
	public void handleUnsubscribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		String destination = accessor.getFirstNativeHeader("x-destination");
		assert authHeader != null;
		if (destination == null || destination.isEmpty()) {
			throw new RuntimeException("구독해제 경로가 없습니다.");
		}

		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);

		log.info("-------------DISSUBSCRIBE------------------------");
		log.info("AUTH_HEADER :: " + authHeader);
		log.info("DESTINATION :: " + destination);
		String regex = "^" + Pattern.quote(sendMsgUrl) + "(/\\d+)?$";
		if (destination.matches(regex)) {
			String[] parts = destination.split("/");
			String roomIdx = parts[parts.length - 1];

			String roomRedisId = chatWebSocketService.getWatchingRoomRedisId(memberId);
			String msgRedisId = chatWebSocketService.getWatchingRoomMsgRedisId(memberId, roomIdx);

			redisService.delData(roomRedisId);
			redisService.delData(msgRedisId);
			log.info("ROOM_IDX :: " + roomIdx);
		}
		else if (destination.matches(unreadChatRoomUrl)){
			String redisId = chatWebSocketService.getUnreadChatRoomRedisId(memberId);
			redisService.delData(redisId);
		}

		log.info("TOKEN ::" + token);
		log.info("MEMBER_ID ::" + memberId);
		log.info("-----------------------------------------------");
	}

	@EventListener
	public void handleSessionDisconnect(SessionDisconnectEvent event) {
		String sessionId = event.getSessionId(); // ✅ 여기서 sessionId 확인 가능
		System.out.println("WebSocket 세션 종료됨: " + sessionId);

		// Redis에서 sessionId -> userId 매핑 제거
		/*String userId = redisTemplate.opsForValue().get("ws:session:" + sessionId);

		if (userId != null) {
			redisTemplate.delete("ws:session:" + sessionId);
			redisTemplate.delete("ws:user:" + userId);
			System.out.println("Redis 세션 정리 완료: " + userId);
		}*/
	}
}
