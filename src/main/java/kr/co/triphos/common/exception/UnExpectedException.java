package kr.co.triphos.common.exception;

import kr.co.triphos.config.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static kr.co.triphos.config.ErrorCode.INTERNAL_SERVER_ERROR;

// 서버에서 예상치 못한 에러가 발생한 경우
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnExpectedException extends CustomException {

    public static final ErrorCode ERROR_CODE = INTERNAL_SERVER_ERROR;

    public UnExpectedException() {
        super(ERROR_CODE);
    }

    public UnExpectedException(String... args) {
        super(ERROR_CODE, toMap(args));
    }

    public UnExpectedException(Throwable cause) {
        super(ERROR_CODE, cause);
    }
}

