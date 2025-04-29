package kr.co.triphos.common.exception;

import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.member.controller.AuthController;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@Order(1)
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthExceptionHandler {

    // 지원하지 않는 웹훅 요청을 한 경우
    @ExceptionHandler(WebhookPlatformTypeNotSupportedException.class)
    public ResponseEntity<ResponseDTO> handleWebhookPlatformTypeNotSupportedException(WebhookPlatformTypeNotSupportedException e) {
        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.create(e.getMessage()));
    }
}
