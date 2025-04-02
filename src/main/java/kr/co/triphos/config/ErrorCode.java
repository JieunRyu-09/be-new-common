package kr.co.triphos.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	BAD_REQUEST(400, "잘못된 요청입니다."),
	UNAUTHORIZED(401, "인증이 필요합니다."),
	PAYMENT_REQUIRED(402, "결제가 필요합니다."),
	FORBIDDEN(403, "접근이 금지되었습니다."),
	NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

	private final int code;
	private final String message;
}

