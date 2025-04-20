package kr.co.triphos.chat;

import kr.co.triphos.chat.service.ChatWebSocketService;
import kr.co.triphos.common.service.RedisService;
import kr.co.triphos.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Log4j2
@RequiredArgsConstructor
public class StompEventListener {
	private final RedisService redisService;
	private final AuthService authService;
	private final ChatWebSocketService chatWebSocketService;

	/*@EventListener
	public void handleConnect(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());



		String token = accessor.getFirstNativeHeader("Authorization");

		log.info("-------------------CONNECT--------------");
		log.info("ACCESSOR :: " + accessor);
		log.info("TOKEN :: " + token);

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			log.info("TOKEN :: " + token);
			log.info("----------------------------------------------");
			try {
				Authentication authentication = authService.getAuthenticationByToken(token);
				SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext 설정
				accessor.setUser(authentication); // 이후 Principal 사용 가능

				System.out.println("✅ CONNECT 인증 성공: " + authentication.getName());
			} catch (Exception e) {
				System.out.println("❌ CONNECT 인증 실패: " + e.getMessage());
				throw new IllegalArgumentException("CONNECT 실패: 인증 불가");
			}
		} else {
			System.out.println("❌ CONNECT 실패: 토큰 없음");
			throw new IllegalArgumentException("CONNECT 실패: 토큰 없음");
		}
	}*/

	@EventListener
	public void handleSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());


		String authHeader = accessor.getFirstNativeHeader("Authorization");
		String destination = accessor.getDestination();
		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);


		if (destination.matches("^/topic/chat/\\d+$")) {
			try {
				String[] parts = destination.split("/");
				String roomIdx = parts[parts.length - 1];
				log.info("ROOM_IDX :: " + roomIdx);

				redisService.delData(memberId + ":chatRoom");
				redisService.saveData(memberId + ":chatRoom", roomIdx);
				chatWebSocketService.setUnreadCount(memberId, Integer.parseInt(roomIdx));
			}
			catch (Exception ex) {
				log.error(ex);
			}
		}

		log.info("-------------SUBSCRIBE------------------------");
		log.info("ACCESSOR :: " + accessor);
		log.info("AUTH_HEADER :: " + authHeader);
		log.info("DESTINATION :: " + destination);
		log.info("TOKEN ::" + token);
		log.info("MEMBER_ID ::" + memberId);
		log.info("----------------------------------------------");
	}

	@EventListener
	public void handleUnsubscribe(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String authHeader = accessor.getFirstNativeHeader("Authorization");
		String destination = accessor.getFirstNativeHeader("x-destination");
		String token = authHeader.substring(7);
		String memberId = authService.getMemberIdByToken(token);

		String[] parts = destination.split("/");
		String roomIdx = parts[parts.length - 1];

		redisService.delData(memberId + ":chatRoom");
		redisService.delData("chat:" + memberId+ ":roomIdx:" + roomIdx + ":msg_idx");

		log.info("-------------DISSUBSCRIBE------------------------");
		log.info("AUTH_HEADER :: " + authHeader);
		log.info("DESTINATION :: " + destination);
		log.info("TOKEN ::" + token);
		log.info("ROOM_IDX :: " + roomIdx);
		log.info("MEMBER_ID ::" + memberId);
		log.info("-----------------------------------------------");
	}
}
