package com.chatdb.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 설정
 * 모든 API 엔드포인트에 API 키 보안 요구사항 자동 적용
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomizer() {
        return openApi -> {
            // 모든 경로의 모든 Operation에 API 키 보안 요구사항 추가
            openApi.getPaths().values().forEach(pathItem -> {
                addSecurityToOperation(pathItem.getGet());
                addSecurityToOperation(pathItem.getPost());
                addSecurityToOperation(pathItem.getPut());
                addSecurityToOperation(pathItem.getDelete());
                addSecurityToOperation(pathItem.getPatch());
            });
        };
    }

    private void addSecurityToOperation(Operation operation) {
        if (operation != null) {
            // API 키 보안 요구사항 추가
            SecurityRequirement apiKeyRequirement = new SecurityRequirement().addList("apiKey");
            operation.addSecurityItem(apiKeyRequirement);
        }
    }
}
