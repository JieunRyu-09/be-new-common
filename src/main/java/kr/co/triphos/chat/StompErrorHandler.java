package kr.co.triphos.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import kr.co.triphos.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

@Log4j2
@RequiredArgsConstructor
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

	@Lazy
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private final ObjectMapper objectMapper;

	@Override
	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

		if (ex instanceof MessageDeliveryException) {
			Throwable cause = ex.getCause();
			if (cause instanceof AccessDeniedException) {
				return sendErrorMessage(new ResponseDTO(false, "서버현황이 불안정합니다. 잠시 후 다시 시도하여주십시오."), 403);
			}
			else if (cause instanceof AuthenticationCredentialsNotFoundException) {
				sendErrorToClient(getSessionId(clientMessage), "토큰이 없습니다.", 403);
				return sendErrorMessage(new ResponseDTO(false, "토큰정보가 없습니다."), 403);
			}
			else if (cause instanceof ExpiredJwtException) {
				return sendErrorMessage(new ResponseDTO(false, "토큰이 만료되었습니다."), 403);
			}
			else if (cause instanceof RuntimeException) {
				return sendErrorMessage(new ResponseDTO(false, ex.getMessage()), 403);
			}
			else if (cause instanceof Exception) {
				return sendErrorMessage(new ResponseDTO(false, "서버에 이상이 발생하였습니다. 잠시후 시도하여주십시오."), 403);
			}


		}
		return super.handleClientMessageProcessingError(clientMessage, ex);
	}

	private String getSessionId(Message<byte[]> message) {
		return (String) message.getHeaders().get("simpSessionId");
	}

	private Message<byte[]> sendErrorMessage(ResponseDTO errorResponse, int errorCode) {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
		headers.setMessage(errorResponse.getMsg());
		headers.setLeaveMutable(true);
		headers.setHeader("errorCode", errorCode);

		try {
			String json = objectMapper.writeValueAsString(errorResponse);
			return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8),
					headers.getMessageHeaders());
		} catch (JsonProcessingException e) {
			log.error("Failed to convert ErrorResponse to JSON", e);
			return MessageBuilder.createMessage(errorResponse.getMsg().getBytes(StandardCharsets.UTF_8),
					headers.getMessageHeaders());
		}
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
			messagingTemplate.send("/error-virtual", message); // destination은 의미 없음, 직접 세션에 푸시됨
		} catch (Exception e) {
			log.error("에러 전송 실패", e);
		}
	}
}