package com.chatdb.controller;

import com.chatdb.dto.ChatMessageRequest;
import com.chatdb.dto.ChatMessageResponse;
import com.chatdb.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메시지 REST API 컨트롤러
 * 채팅 메시지 CRUD 및 조회 API
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "채팅 메시지 API")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {
    
    private final MessageService messageService;
    
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    /**
     * 채팅방의 메시지 목록 조회 (페이징)
     * @param chatroomId 채팅방 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 50)
     * @return 메시지 목록
     */
    @GetMapping("/chatrooms/{chatroomId}")
    @Operation(summary = "메시지 목록 조회", description = "채팅방의 메시지 목록을 페이징하여 조회합니다.")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable Long chatroomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<ChatMessageResponse> messages = messageService.getMessages(chatroomId, page, size);
            
            response.put("success", true);
            response.put("message", "메시지 목록을 조회했습니다.");
            response.put("messages", messages.getContent());
            response.put("currentPage", messages.getNumber());
            response.put("totalPages", messages.getTotalPages());
            response.put("totalMessages", messages.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 채팅방의 모든 메시지 조회
     * @param chatroomId 채팅방 ID
     * @return 전체 메시지 목록
     */
    @GetMapping("/chatrooms/{chatroomId}/all")
    @Operation(summary = "모든 메시지 조회", description = "채팅방의 모든 메시지를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getAllMessages(@PathVariable Long chatroomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ChatMessageResponse> messages = messageService.getAllMessages(chatroomId);
            
            response.put("success", true);
            response.put("message", "모든 메시지를 조회했습니다.");
            response.put("messages", messages);
            response.put("count", messages.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 안 읽은 메시지 개수 조회
     * @param chatroomId 채팅방 ID
     * @param authentication 인증 정보
     * @return 안 읽은 메시지 개수
     */
    @GetMapping("/chatrooms/{chatroomId}/unread-count")
    @Operation(summary = "안 읽은 메시지 개수", description = "채팅방의 안 읽은 메시지 개수를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCount(
            @PathVariable Long chatroomId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        try {
            long unreadCount = messageService.getUnreadMessageCount(chatroomId, userId);
            
            response.put("success", true);
            response.put("message", "안 읽은 메시지 개수를 조회했습니다.");
            response.put("chatroomId", chatroomId);
            response.put("unreadCount", unreadCount);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "안 읽은 메시지 개수 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 메시지 읽음 처리
     * @param chatroomId 채팅방 ID
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PostMapping("/chatrooms/{chatroomId}/read")
    @Operation(summary = "메시지 읽음 처리", description = "채팅방의 메시지를 읽음 처리합니다.")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long chatroomId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        try {
            messageService.markAsRead(chatroomId, userId);
            
            response.put("success", true);
            response.put("message", "메시지를 읽음 처리했습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "읽음 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 메시지 전송 (REST API)
     * @param chatroomId 채팅방 ID
     * @param request 메시지 요청
     * @param authentication 인증 정보
     * @return 전송된 메시지
     */
    @PostMapping("/chatrooms/{chatroomId}")
    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다. (REST API)")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable Long chatroomId,
            @RequestBody ChatMessageRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        try {
            ChatMessageResponse message = messageService.sendMessage(chatroomId, userId, request);
            
            response.put("success", true);
            response.put("message", "메시지를 전송했습니다.");
            response.put("data", message);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 전송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 메시지 수정
     * @param messageId 메시지 ID
     * @param request 수정 내용
     * @param authentication 인증 정보
     * @return 수정된 메시지
     */
    @PutMapping("/{messageId}")
    @Operation(summary = "메시지 수정", description = "자신이 작성한 메시지를 수정합니다.")
    public ResponseEntity<Map<String, Object>> updateMessage(
            @PathVariable Long messageId,
            @RequestBody ChatMessageRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        try {
            ChatMessageResponse message = messageService.updateMessage(messageId, userId, request.getContent());
            
            response.put("success", true);
            response.put("message", "메시지를 수정했습니다.");
            response.put("data", message);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 메시지 삭제
     * @param messageId 메시지 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "메시지 삭제", description = "자신이 작성한 메시지를 삭제합니다.")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Long messageId,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        try {
            messageService.deleteMessage(messageId, userId);
            
            response.put("success", true);
            response.put("message", "메시지를 삭제했습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 메시지 조회
     * @param messageId 메시지 ID
     * @return 메시지 정보
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "메시지 조회", description = "특정 메시지를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getMessage(@PathVariable Long messageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ChatMessageResponse message = messageService.getMessage(messageId);
            
            response.put("success", true);
            response.put("message", "메시지를 조회했습니다.");
            response.put("data", message);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
