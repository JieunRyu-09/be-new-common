package kr.co.triphos.common.service.factory;

import kr.co.triphos.common.service.WebhookService;
import kr.co.triphos.common.service.impl.GitHubWebhookServiceImpl;
import kr.co.triphos.common.service.impl.GitLabWebhookServiceImpl;
import kr.co.triphos.common.service.impl.GitTeaWebhookServiceImpl;
import kr.co.triphos.common.service.impl.GogsWebhookServiceImpl;
import kr.co.triphos.manage.enums.WebhookPlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// WebhookPlatformType enum과 매칭하는 Factory 클래스
@Component
@RequiredArgsConstructor
public class WebhookServiceFactory {

    private final Map<WebhookPlatformType, WebhookService> serviceMap;

    @Autowired
    public WebhookServiceFactory(List<WebhookService> services) {
        this.serviceMap = new HashMap<>();

        services.forEach(service -> {
            if (service instanceof GitTeaWebhookServiceImpl) {
                serviceMap.put(WebhookPlatformType.GIT_TEA, service);
            } else if (service instanceof GitLabWebhookServiceImpl) {
                serviceMap.put(WebhookPlatformType.GIT_LAB, service);
            } else if (service instanceof GogsWebhookServiceImpl) {
                serviceMap.put(WebhookPlatformType.GOGS, service);
            } else if (service instanceof GitHubWebhookServiceImpl) {
                serviceMap.put(WebhookPlatformType.GIT_HUB, service);
            }
        });
    }

    // PlatformType에 맞는 WebhookService 반환
    public WebhookService getService(WebhookPlatformType platformType) {
        return Optional.ofNullable(serviceMap.get(platformType))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 웹훅 플랫폼타입입니다."));
    }
}
