package kr.co.triphos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sound.sampled.Line;

@Configuration
@OpenAPIDefinition(
		security = @SecurityRequirement(name = "bearerAuth") // API 전체에 Security 적용
)

public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				// 스웨거 제목정보
				.info(new Info().title("Triphos 개발표준 API문서").version("V1..0.0").description(""))
				// 스웨거 jwt토큰 설정
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("bearerAuth",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")))
				// 스웨거 문서 태그 순서결정
				.addTagsItem(new Tag().name("사용자 관리"));
	}

}
