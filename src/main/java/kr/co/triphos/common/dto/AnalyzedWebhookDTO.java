package kr.co.triphos.common.dto;

import kr.co.triphos.common.enums.GitActivityType;
import lombok.Builder;
import lombok.Data;

// 분석한 웹 DTO
@Data
@Builder
public class AnalyzedWebhookDTO {

    // 타입
    private GitActivityType type;

    // 이벤트 생성자
    private String creator;

    // 타겟명(저장소명)
    private String targetName;

    // 타겟 URL
    private String targetUrl;

    // 내용 반환
    public String toContent() {
        switch (type) {
            case PULL_REQUEST:
                return String.format("%s님이 [%s] 저장소에 Pull Request를 생성했습니다.\n(%s)", creator, targetName, targetUrl);
            case COMMENT:
                return String.format("%s님이 [%s]에 코멘트를 작성했습니다.\n(%s)", creator, targetName, targetUrl);
            case ISSUE:
                return String.format("%s님이 [%s] 저장소에 이슈를 등록했습니다.\n(%s)", creator, targetName, targetUrl);
            case MERGE:
                return String.format("%s님이 [%s] Pull Request를 머지했습니다.\n(%s)", creator, targetName, targetUrl);
            case RELEASE:
                return String.format("%s님이 [%s] 저장소에 새로운 릴리즈를 발행했습니다.\n(%s)", creator, targetName, targetUrl);
            case FORK:
                return String.format("%s님이 [%s] 저장소를 포크했습니다.\n(%s)", creator, targetName, targetUrl);
            case COMMIT:
                return String.format("%s님이 [%s] 저장소에 커밋을 푸시했습니다.\n(%s)", creator, targetName, targetUrl);
            case DELETE:
                return String.format("%s님이 [%s] 브랜치나 태그를 삭제했습니다.\n(%s)", creator, targetName, targetUrl);
            default:
                return String.format("%s님의 [%s] 저장소에서 알 수 없는 활동이 발생했습니다.\n(%s)", creator, targetName, targetUrl);
        }
    }
}
