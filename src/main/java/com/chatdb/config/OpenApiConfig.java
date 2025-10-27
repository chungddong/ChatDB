package com.chatdb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 제외)")))
                .info(new Info()
                        .title("ChatDB API")
                        .version("v1.0")
                        .description("Chat Database Application REST API Documentation\n\n" +
                                "## 인증 방법\n" +
                                "1. `/api/auth/login` 엔드포인트로 로그인하여 JWT 토큰을 받습니다.\n" +
                                "2. 우측 상단의 'Authorize' 버튼을 클릭합니다.\n" +
                                "3. 받은 토큰을 입력하고 'Authorize' 버튼을 클릭합니다.\n" +
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