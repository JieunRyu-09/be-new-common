package kr.co.triphos.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SwaggerConfigTest {

    private static Logger logger = null;

    @BeforeEach
    public void setLogger() {
        logger = LogManager.getLogger();
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Test
    @DisplayName("Swagger UI 페이지 노출 테스트")
    void swaggerUiLoads() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OpenAPI JSON 정상 호출 확인")
    void apiDocsLoads() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        logger.info(jsonPath("$.info.title").toString());
        logger.info(jsonPath("$.info.version").toString());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.info.title").value("Triphos 개발표준 API문서"))
                .andExpect(jsonPath("$.info.version").value("v1.0.0"));
    }

    @Test
    @DisplayName("exclude path 미노출 확인")
    void excludePathNotAvailable() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/common/**']").doesNotExist())
                .andExpect(jsonPath("$.paths['/command/**']").doesNotExist());
    }

    @Test
    @DisplayName("JWT 정상 적용 확인")
    void swaggerSecuritySchemeTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.JWT").exists())
                .andExpect(jsonPath("$.components.securitySchemes.JWT.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.JWT.scheme").value("bearer"));
    }

}
