package kr.co.triphos.organization.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

// Webhook이 올 수 있는 플랫폼
@Getter
@RequiredArgsConstructor
public enum WebhookPlatformType {

    // 깃티
    GIT_TEA("GITTEA"),

    // 깃허브
    GIT_HUB("GITHUB"),

    // 깃랩
    GIT_LAB("GITLAB"),

    // 곡스
    GOGS("GOGS");

    private final String platformType;

    // PlatformType 코드 값으로 PlatformType Enum 추출
    public static Optional<WebhookPlatformType> fromPlatformType(String platformType) {
        return Arrays.stream(WebhookPlatformType.values())
                .filter(it -> it.getPlatformType().equals(platformType))
                .findFirst();
    }
}
