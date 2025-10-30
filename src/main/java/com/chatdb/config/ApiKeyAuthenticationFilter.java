package com.chatdb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * API 키 인증 필터
 * 모든 API 요청에 대해 API 키를 검증합니다.
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.key.enabled:true}")
    private boolean apiKeyEnabled;

    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Swagger UI 관련 경로는 API 키 검증 제외
        if (path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // API 키 검증이 비활성화된 경우 (개발 환경)
        if (!apiKeyEnabled) {
            logger.info("API Key validation is DISABLED (api.key.enabled=false)");
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("API Key validation is ENABLED (api.key.enabled=true)");
        String requestApiKey = request.getHeader(API_KEY_HEADER);

        // API 키가 없거나 일치하지 않는 경우
        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            logger.warn("Invalid or missing API key. Request path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Invalid or missing API key\", \"message\": \"X-API-Key 헤더에 올바른 API 키를 포함해야 합니다.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
