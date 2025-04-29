package kr.co.triphos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

// 외부 API 요청용 Configuration
@Configuration
public class WebClientConfiguration {

    // GitTea용 WebClient
    @Bean(name = "gitTeaWebClient")
    public WebClient gitTeaWebClient() {
        return WebClient.builder()
                .baseUrl("http://dev.triphos.co.kr:34000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
