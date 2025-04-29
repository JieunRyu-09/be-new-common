package kr.co.triphos.common.exception;

import kr.co.triphos.config.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

// Custom Exception들의 부모 클렉스
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomException extends RuntimeException {
    // 상수
    private static final String EXCEPTION_INFO_BRACKET = "{ %s | %s }";
    private static final String CODE_MESSAGE = " Code: %d, Message: %s ";
    private static final String PROPERTY_VALUE = "Property: %s, Value: %s ";
    private static final String VALUE_DELIMITER = "/";

    // 에러 코드
    private final int code;
    // 에러 메세지
    private final String message;
    // 입력값
    private final Map<String, String> inputValuesByProperty;

    protected CustomException(final ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    protected CustomException(final ErrorCode errorCode, Throwable cause) {
        this(errorCode, Collections.emptyMap(), cause);
    }

    protected CustomException(ErrorCode errorCode, Map<String, String> inputValuesByProperty) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.inputValuesByProperty = inputValuesByProperty;
    }

    protected CustomException(ErrorCode errorCode, Map<String, String> inputValuesByProperty, Throwable cause) {
        super(cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.inputValuesByProperty = inputValuesByProperty;
    }

    // 팩토리 메서드
    public static CustomException of(ErrorCode errorCode, Map<String, String> inputValuesByProperty, Throwable cause) {
        return new CustomException(errorCode, inputValuesByProperty, cause);
    }

    // 팩토리 메서드
    public static CustomException of(ErrorCode errorCode, Map<String, String> inputValuesByProperty) {
        return new CustomException(errorCode, inputValuesByProperty);
    }

    // 팩토리 메서드
    public static CustomException from(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }

    // 에러 로그
    public String getErrorInfoLog() {
        final String codeMessage = String.format(CODE_MESSAGE, code, message);
        final String errorPropertyValue = getErrorPropertyValue();

        return String.format(EXCEPTION_INFO_BRACKET, codeMessage, errorPropertyValue);
    }

    // 에러 로그 - 입력값 출력
    private String getErrorPropertyValue() {
        return inputValuesByProperty.entrySet()
                .stream()
                .map(entry -> String.format(PROPERTY_VALUE, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(VALUE_DELIMITER));
    }

    // Key, Value로 순차로 들어오는 값
    protected static Map<String, String> toMap(String... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("args length must be even: key-value pairs expected.");
        }

        Map<String, String> map = new java.util.HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }
}

