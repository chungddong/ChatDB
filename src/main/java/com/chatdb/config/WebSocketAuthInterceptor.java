package com.chatdb.config;

import com.chatdb.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket 인증 인터셉터
 * STOMP 연결 시 JWT 토큰을 검증하고 사용자 ID를 세션에 저장
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 시 JWT 토큰 검증
            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);

                try {
                    // JWT 토큰에서 사용자 ID 추출
                    Long userId = jwtUtil.extractUserId(token);

                    // 세션 속성에 사용자 ID 저장
                    accessor.getSessionAttributes().put("userId", userId);

                } catch (Exception e) {
                    // 토큰 검증 실패 시 연결 거부는 하지 않고 로그만 남김
                    // (필요시 예외를 던져서 연결을 거부할 수 있음)
                    System.err.println("JWT 토큰 검증 실패: " + e.getMessage());
                }
            }
        }

        return message;
    }
}
