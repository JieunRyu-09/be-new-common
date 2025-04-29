package kr.co.triphos.manage.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

// Webhook이 올 수 있는 플랫폼
@Getter
@RequiredArgsConstructor
public enum WebhookPlatformType {

    // 깃티
    GIT_TEA("GT"),

    // 곡스
    GOGS("G"),

    // 깃허브
    GIT_HUB("GH"),

    // 깃랩
    GIT_LAB("GL");

    private final String platformType;

    // PlatformType 코드 값으로 PlatformType Enum 추출
    public static Optional<WebhookPlatformType> fromPlatformType(String platformType) {
        return Arrays.stream(WebhookPlatformType.values())
                .filter(it -> it.getPlatformType().equals(platformType))
                .findFirst();
    }
}
