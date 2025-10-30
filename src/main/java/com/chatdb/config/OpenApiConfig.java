package com.chatdb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // API 키 보안 요구사항 (모든 API에 적용)
        SecurityRequirement apiKeyRequirement = new SecurityRequirement().addList("apiKey");

        return new OpenAPI()
                .components(new Components()
                        // JWT 인증 스키마
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 제외)"))
                        // API 키 인증 스키마
                        .addSecuritySchemes("apiKey",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key")
                                        .description("API 키를 입력하세요 (application.properties에 설정된 api.key 값)")))
                // 전역 보안 요구사항 적용
                .addSecurityItem(apiKeyRequirement)
                .info(new Info()
                        .title("ChatDB API")
                        .version("v1.0")
                        .description("Chat Database Application REST API Documentation\n\n" +
                                "## 인증 방법\n" +
                                "### 1. API 키 인증 (필수)\n" +
                                "- 모든 API 요청에 `X-API-Key` 헤더가 필요합니다.\n" +
                                "- 우측 상단의 'Authorize' 버튼을 클릭하여 API 키를 입력하세요.\n" +
                                "- API 키는 `application.properties`의 `api.key` 값입니다.\n\n" +
                                "### 2. JWT 토큰 인증 (보호된 엔드포인트)\n" +
                                "1. API 키를 먼저 설정합니다.\n" +
                                "2. `/api/auth/login` 엔드포인트로 로그인하여 JWT 토큰을 받습니다.\n" +
                                "3. 'Authorize' 버튼에서 받은 토큰을 입력합니다.\n" +
                                "4. 이제 인증이 필요한 API를 사용할 수 있습니다.")
                        .contact(new Contact()
                                .name("ChatDB Team")
                                .email("admin@chatdb.com")
                                .url("https://github.com/chungddong/changongDB"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}