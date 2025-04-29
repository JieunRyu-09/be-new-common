package kr.co.triphos.common.exception;

import kr.co.triphos.config.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 웹훅 플랫폼 타입을 찾지 못한 경우
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WebhookPlatformTypeNotSupportedException extends CustomException {

    public static final ErrorCode ERROR_CODE = ErrorCode.WEBHOOK_PLATFORM_NOT_SUPPORTED;

    public WebhookPlatformTypeNotSupportedException() {
        super(ERROR_CODE);
    }

    public WebhookPlatformTypeNotSupportedException(String... args) {
        super(ERROR_CODE, toMap(args));
    }

    public WebhookPlatformTypeNotSupportedException(Throwable cause) {
        super(ERROR_CODE, cause);
    }
}
