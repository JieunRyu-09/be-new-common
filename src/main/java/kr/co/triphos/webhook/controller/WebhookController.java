package kr.co.triphos.webhook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.triphos.common.dto.GitTeaWebhookDTO;
import kr.co.triphos.common.dto.ResponseDTO;
import kr.co.triphos.common.service.WebhookService;
import kr.co.triphos.common.service.factory.WebhookServiceFactory;
import kr.co.triphos.manage.enums.WebhookPlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/webhook")
public class WebhookController {

    private final WebhookServiceFactory webhookServiceFactory;

    @PostMapping("/web-hook")
    @Tag(name = "Git에서 발생하는 Event 수신")
    @Operation(
            summary = "Git에서 발생하는 Event 수신",
            description = "Git에서 발생하는 Event 수신하여 선택된 채팅방으로 인계",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(hidden = true)
                    )
            )
    )
    public ResponseEntity<?> webhookTest(
            @RequestBody GitTeaWebhookDTO gitTeaWebhookDTO,
            @Parameter(description = "웹훅 플랫폼 유형 (예: GITHUB, GITLAB, GITEA, GOGS)", example = "GITHUB") @RequestParam String platformType,
            @Parameter(description = "이벤트를 전송할 채티방 고유 ID", example = "123") @RequestParam Long chatRoomIdx
    ) {
        WebhookPlatformType webhookPlatformType = WebhookPlatformType.fromPlatformType(platformType)
                .orElseThrow(UnsupportedOperationException::new);
        WebhookService service = webhookServiceFactory.getService(webhookPlatformType);

        service.analyzeEvent(gitTeaWebhookDTO);

        log.info("WEB HOOK 도착함");
        log.info(gitTeaWebhookDTO);
        return ResponseEntity.ok().body(ResponseDTO.create("성공적으로 처리했습니다."));
    }
}
