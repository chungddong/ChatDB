package com.chatdb.controller;

import com.chatdb.dto.ChatMessageRequest;
import com.chatdb.dto.ChatMessageResponse;
import com.chatdb.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 메시지 컨트롤러
 * 실시간 채팅 메시지 전송/수신 처리
 */
@Controller
public class WebSocketMessageController {
    
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketMessageController(MessageService messageService,
                                     SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * 채팅 메시지 전송
     * 클라이언트 → 서버: /app/chat/{chatroomId}/send
     * 서버 → 클라이언트: /topic/chatroom/{chatroomId}
     * 
     * @param chatroomId 채팅방 ID
     * @param request 메시지 요청
     * @param headerAccessor WebSocket 세션 정보
     * @return 전송된 메시지
     */
    @MessageMapping("/chat/{chatroomId}/send")
    @SendTo("/topic/chatroom/{chatroomId}")
    public ChatMessageResponse sendMessage(
            @DestinationVariable Long chatroomId,
            ChatMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            // WebSocket 세션에서 사용자 ID 추출
            // (실제로는 JWT 토큰에서 추출하거나, 연결 시 저장한 정보 사용)
            Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
            
            if (userId == null) {
                throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
            }
            
            // 메시지 저장 및 전송
            ChatMessageResponse response = messageService.sendMessage(chatroomId, userId, request);
            
            return response;
            
        } catch (Exception e) {
            // 에러 처리 (실제로는 에러 메시지를 별도로 처리)
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 읽음 상태 알림
     * 클라이언트 → 서버: /app/chat/{chatroomId}/read
     * 서버 → 클라이언트: /topic/chatroom/{chatroomId}/read
     * 
     * @param chatroomId 채팅방 ID
     * @param headerAccessor WebSocket 세션 정보
     */
    @MessageMapping("/chat/{chatroomId}/read")
    public void markAsRead(
            @DestinationVariable Long chatroomId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
            
            if (userId == null) {
                throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
            }
            
            // 읽음 처리
            messageService.markAsRead(chatroomId, userId);
            
            // 읽음 알림을 채팅방의 모든 참가자에게 전송
            messagingTemplate.convertAndSend(
                "/topic/chatroom/" + chatroomId + "/read",
                new ReadNotification(userId, chatroomId)
            );
            
        } catch (Exception e) {
            throw new RuntimeException("읽음 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 읽음 알림 DTO
     */
    public static class ReadNotification {
        private Long userId;
        private Long chatroomId;
        
        public ReadNotification(Long userId, Long chatroomId) {
            this.userId = userId;
            this.chatroomId = chatroomId;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Long getChatroomId() {
            return chatroomId;
        }
        
        public void setChatroomId(Long chatroomId) {
            this.chatroomId = chatroomId;
        }
    }
}
