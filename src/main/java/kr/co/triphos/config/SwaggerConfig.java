package kr.co.triphos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sound.sampled.Line;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
				.version("v1.0.0")
				.title("Triphos 개발표준 API문서")
				.description("");

		return new OpenAPI()
				.info(info);
	}

}
