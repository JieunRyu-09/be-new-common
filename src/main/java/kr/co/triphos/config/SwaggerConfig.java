package kr.co.triphos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@OpenAPIDefinition (
	info = @io.swagger.v3.oas.annotations.info.Info(title = "Triphos 개발표준 API문서", version = "v1.0.0")
)
@Log4j2
public class SwaggerConfig {

	private static final String BASE_PACKAGE = "kr.co.triphos";
	private static final String JWT_SCHEME_NAME = "JWT";
	private static final String[] EXCLUDE_PATHS = {"/common/**", "/command/**"};

	@Value("${springdoc.doc-locations}")
	private String swaggerDocPath;

	@Bean
	public GroupedOpenApi authGroup() {return createGroup("001", "1. 인증", true, "/v1/auth/**");}

	@Bean
	public GroupedOpenApi memberGroup() {return createGroup("002", "2. 회원", true, new String[]{"/v1/members/**"});}

	@Bean
	public GroupedOpenApi excelGroup() {return createGroup("003","3. 엑셀", true, new String[]{"/v1/excels/**"});}

	@Bean
	public GroupedOpenApi fileGroup() {return createGroup("004","4. 파일", true, new String[]{"/v1/files/**"});}

	@Bean
	public GroupedOpenApi menuGroup() {return createGroup("005","5. 메뉴", true, new String[]{"/v1/menus/**"});}

	@Bean
	public GroupedOpenApi chatGroup() {return createGroup("006","6. 채팅", true, new String[]{"/v1/chat/**"});}

	@Bean
	public GroupedOpenApi chatOrganization() {return createGroup("007","7. 조직도", true, new String[]{"/v1/organizations/**"});}


	@Bean
	public GroupedOpenApi all() {return createGroup("099","99. 전체", true, "/**");}

	/**
	 * GroupedOpenApi 설정
	 * @return GroupedOpenApi
	 */
	private GroupedOpenApi createGroup(String group, String groupName, boolean enableSecurity, String... pathPattern) {
		GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
				.group(group)
				.pathsToMatch(pathPattern)
				.pathsToExclude(EXCLUDE_PATHS)
				.packagesToScan(BASE_PACKAGE)
				.displayName(groupName)
				.addOpenApiCustomiser(enableSecurity ? schemaAutoRegisterWithSecurity() : schemaAutoRegister());

		return builder.build();
	}

	/**
	 * 스키마 자동 등록
	 * @return OpenApiCustomiser
	 */
	private OpenApiCustomiser schemaAutoRegister() {
		return openApi -> {
			Components components = openApi.getComponents();
			components.setExamples(loadApiExamples());
			registerSchemas(components);
			openApi.setComponents(components);
		};
	}

	/**
	 * 스키마 자동 등록 + JWT 설정
	 * @return OpenApiCustomiser
	 */
	private OpenApiCustomiser schemaAutoRegisterWithSecurity() {
		return openApi -> {
			Components components = openApi.getComponents();
			components.setExamples(loadApiExamples());
			registerSchemas(components);

			// JWT Security 설정 추가
			components.addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
					.name(JWT_SCHEME_NAME)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT"));

			openApi.addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME_NAME));
			openApi.setComponents(components);
		};
	}

	/**
	 * 패키지 스캔, DTO 자동 등록
	 * @param components
	 */
	private void registerSchemas(Components components) {
		Reflections reflections = new Reflections(BASE_PACKAGE, Scanners.TypesAnnotated);
		Set<Class<?>> dtoClasses = reflections.getTypesAnnotatedWith(io.swagger.v3.oas.annotations.media.Schema.class);

		for (Class<?> dtoClass : dtoClasses) {
			String dtoName = dtoClass.getSimpleName();
			Schema<Object> schema = new Schema<>();
			schema.setDescription(dtoName + " DTO");
			components.addSchemas(dtoName, schema);
		}
	}

	/**
	 * Swagger 예제 JSON 파일 로드
	 * @return Map<String, Example>
	 */
	private Map<String, Example> loadApiExamples() {
		Map<String, Example> examples = new HashMap<>();

		try {
			String pattern = swaggerDocPath + "*.json";
			Resource[] resources = new PathMatchingResourcePatternResolver().getResources(pattern);

			for (Resource resource : resources) {
				try (InputStream inputStream = resource.getInputStream();
					 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

					String fileName = resource.getFilename().replace(".json", "");
					JSONObject json = (JSONObject) new JSONParser().parse(reader);

					json.forEach((key, value) -> {
						Example example = new Example();
						example.setValue(value);
						example.setDescription(key.toString());
						examples.put(fileName + "." + key, example);
					});

				}
				catch (Exception fileEx) {
					log.error("Swagger 예제 파일 읽기 실패: {} - {}", resource.getFilename(), fileEx.getMessage());
				}
			}

		}
		catch (Exception ex) {
			log.error("Swagger 예제 파일 경로 로드 실패: {}", ex.getMessage());
		}

		return examples;
	}

}
