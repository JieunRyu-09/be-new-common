package kr.co.triphos.common.exception;

import kr.co.triphos.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@Order(10)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    // 예상치 못한 에러 발생한 경우
    @ExceptionHandler(UnExpectedException.class)
    public ResponseEntity<ResponseDTO> handleUnExpectedException(UnExpectedException e) {
        log.error("예상치 못한 서버 장애가 발생했습니다.", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.create(UnExpectedException.ERROR_CODE.getMessage()));
    }
}
