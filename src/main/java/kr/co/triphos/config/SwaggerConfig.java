package kr.co.triphos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.acl.Group;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Lazy
@OpenAPIDefinition (
	info = @io.swagger.v3.oas.annotations.info.Info(title = "Triphos 개발표준 API문서", version = "v1.0.0")
)
@Log4j2
@RequiredArgsConstructor
public class SwaggerConfig {

	private final ResourceLoader resourceLoader;

	@Value("${springdoc.doc-locations}")
	private String swaggerDocPath;

//	@Bean
//	public GroupedOpenApi all() {
//		return GroupedOpenApi.builder()
//				.group("전체")
//				.pathsToMatch("/**")
//				.addOpenApiCustomiser(getAuthCustom())
//				.build();
//	}
//
//	@Bean
//	public GroupedOpenApi memberGroup() {
//		String[] excludePaths = {"/auth/**"};
//
//		return GroupedOpenApi.builder()
//				.group("회원")
//				.pathsToMatch("/member/**")
//				.addOpenApiCustomiser(getAuthCustom())
//				.build();
//	}
//
//	@Bean
//	public GroupedOpenApi authGroup() {
//		Components components = new Components();
//		components.setExamples(getAPIExamples());
//
//		return GroupedOpenApi.builder()
//				.group("인증")
//				.pathsToMatch("/auth/**")
//				.build();
//	}
//
//	private OpenApiCustomiser getAuthCustom() {
//		String jwt = "JWT";
//		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//		Components components = new Components()
//				.addSecuritySchemes(jwt, new SecurityScheme()
//						.name(jwt)
//						.type(SecurityScheme.Type.HTTP)
//						.scheme("bearer")
//						.bearerFormat("JWT"));
//		components.setExamples(getAPIExamples());
//
//		return openApi -> openApi
//				.addSecurityItem(securityRequirement)
//				.components(components);
//	}

	@Bean
	public OpenAPI openAPI() {
		String jwt = "JWT";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
		Components components = new Components()
				.addSecuritySchemes(jwt, new SecurityScheme()
						.name(jwt)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));
		components.setExamples(getAPIExamples());

		return new OpenAPI()
				.components(components)
				.addSecurityItem(securityRequirement);
	}

	private Map<String, Example> getAPIExamples() {
		Map<String, Example> examples = new HashMap<>();

		try {
			Resource resource = resourceLoader.getResource(swaggerDocPath);

			// 파일 경로 검증 및 파일 읽기 처리
			File swaggerDir = resource.getFile();
			if (swaggerDir.exists() && swaggerDir.isDirectory()) {
				File[] swaggerExampleJsonFiles = swaggerDir.listFiles(
						file -> file.isFile() && file.getName().endsWith(".json")
				);

				if (swaggerExampleJsonFiles != null) {
					for (File file : swaggerExampleJsonFiles) {
						try (InputStreamReader reader = new InputStreamReader(
								resourceLoader.getResource("file:" + file.getPath()).getInputStream(),
								StandardCharsets.UTF_8)) {

							// JSON 파싱 & Example 객체 변환
							JSONObject json = (JSONObject) new JSONParser().parse(reader);

							json.forEach((key, value) -> {
								Example example = new Example();
								example.setValue(value);
								example.setDescription(key.toString());
								examples.put(key.toString(), example);
							});
						}
						catch (Exception fileEx) {
							log.error("파일 읽기 실패: {} - {}", file.getName(), fileEx.getMessage());
						}
					}
				}
			}
			else {
				log.error("Swagger 예제 JSON 경로를 찾을 수 없습니다: {}", swaggerDocPath);
			}
		}
		catch (Exception ex) {
			log.error("Swagger 예제 로드 실패: {}", ex.getMessage());
		}

		return examples;
	}

}
