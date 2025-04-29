package kr.co.triphos.common.enums;

import kr.co.triphos.common.dto.WebhookDTO;
import kr.co.triphos.manage.enums.WebhookPlatformType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum GitActivityType {

    // 풀리퀘스트
    PULL_REQUEST("PR"),

    // 커밋
    PUSH("PU"),

    // 이슈
    ISSUE("IS"),

    // 머지
    MERGE("ME"),

    // 릴리즈
    RELEASE("RE"),

    // 포크
    FORK("FORK"),

    // 코멘트
    COMMENT("CM"),

    // 브랜치나 태그 삭제
    DELETE("DE");

    private final String code;

    // code로 GitActivity 찾기
    public static Optional<GitActivityType> from(String code) {
        return Arrays.stream(GitActivityType.values())
                .filter(it -> it.getCode().equals(code))
                .findFirst();
    }

    // PlatformType과 WebhookDTO로 GitActivityType 찾기
    public static Optional<GitActivityType> from(WebhookPlatformType type, WebhookDTO dto) {
        if (isPullRequest(type, dto)) {
            return Optional.of(PULL_REQUEST);
        } else if(isPush(type, dto)) {
            return Optional.of(PUSH);
        } else if(isIssue(type, dto)) {
            return Optional.of(ISSUE);
        } else if(isMerge(type, dto)) {
            return Optional.of(MERGE);
        } else if(isRelease(type, dto)) {
            return Optional.of(RELEASE);
        } else if(isFork(type, dto)) {
            return Optional.of(FORK);
        } else if(isComment(type, dto)) {
            return Optional.of(COMMENT);
        } else if(isDelete(type, dto)) {
            return Optional.of(DELETE);
        }
        return Optional.empty();
    }

    // 풀리퀘스트 여부
    private static boolean isPullRequest(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 푸시 여부
    private static boolean isPush(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 이슈 여부
    private static boolean isIssue(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 머지 여부
    private static boolean isMerge(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 릴리즈 여부
    private static boolean isRelease(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 포크 여부
    private static boolean isFork(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 코멘트 여부
    private static boolean isComment(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }

    // 삭제 여부
    private static boolean isDelete(WebhookPlatformType type, WebhookDTO dto) {
        switch (type) {
            case GIT_TEA:
                break;
            case GIT_HUB:
                break;
            case GIT_LAB:
                break;
            case GOGS:
                break;
        }
        return false;
    }
}
