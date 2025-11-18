package com.chatdb.controller;

import com.chatdb.dto.ChatRoomResponse;
import com.chatdb.dto.CreateChatRoomRequest;
import com.chatdb.dto.ParticipantResponse;
import com.chatdb.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 채팅방 컨트롤러
 * 채팅방 생성, 조회, 삭제 및 참가자 관리
 */
@RestController
@RequestMapping("/api/chatrooms")
@Tag(name = "ChatRoom", description = "채팅방 API")
@SecurityRequirement(name = "bearerAuth")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    /**
     * 채팅방 생성
     * @param request 채팅방 이름 및 참가자 목록
     * @return 생성된 채팅방 정보
     */
    @PostMapping
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성하고 참가자를 추가합니다.")
    public ResponseEntity<Map<String, Object>> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 입력값 검증
            if (request.getChatroomName() == null || request.getChatroomName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "채팅방 이름을 입력해주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
                response.put("success", false);
                response.put("message", "최소 1명 이상의 참가자를 선택해주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 채팅방 생성
            ChatRoomResponse chatRoom = chatRoomService.createChatRoom(request);

            response.put("success", true);
            response.put("message", "채팅방이 생성되었습니다.");
            response.put("chatRoom", chatRoom);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "채팅방 생성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 내가 참여한 채팅방 조회
     * @return 내가 참여한 채팅방 목록
     */
    @GetMapping
    @Operation(summary = "내가 참여한 채팅방 조회", description = "현재 사용자가 참여한 채팅방 목록을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getAllChatRooms(
            org.springframework.security.core.Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) authentication.getPrincipal();
            List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsByUserId(userId);

            response.put("success", true);
            response.put("message", "채팅방 목록을 조회했습니다.");
            response.put("chatRooms", chatRooms);
            response.put("count", chatRooms.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "채팅방 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 채팅방 조회
     * @param chatroomId 채팅방 ID
     * @return 채팅방 정보
     */
    @GetMapping("/{chatroomId}")
    @Operation(summary = "특정 채팅방 조회", description = "채팅방 ID로 특정 채팅방 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getChatRoomById(@PathVariable Long chatroomId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<ChatRoomResponse> chatRoomOptional = chatRoomService.getChatRoomById(chatroomId);

            if (chatRoomOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "존재하지 않는 채팅방입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("message", "채팅방 정보를 조회했습니다.");
            response.put("chatRoom", chatRoomOptional.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "채팅방 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 채팅방 삭제
     * @param chatroomId 채팅방 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{chatroomId}")
    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다. 참가자도 함께 삭제됩니다.")
    public ResponseEntity<Map<String, Object>> deleteChatRoom(@PathVariable Long chatroomId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = chatRoomService.deleteChatRoom(chatroomId);

            if (!deleted) {
                response.put("success", false);
                response.put("message", "존재하지 않는 채팅방입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("message", "채팅방이 삭제되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "채팅방 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 채팅방 참가자 조회
     * @param chatroomId 채팅방 ID
     * @return 참가자 목록
     */
    @GetMapping("/{chatroomId}/participants")
    @Operation(summary = "채팅방 참가자 조회", description = "특정 채팅방의 참가자 목록을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getParticipants(@PathVariable Long chatroomId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 채팅방 존재 여부 확인
            if (!chatRoomService.existsById(chatroomId)) {
                response.put("success", false);
                response.put("message", "존재하지 않는 채팅방입니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<ParticipantResponse> participants = chatRoomService.getParticipantsByChatRoom(chatroomId);

            response.put("success", true);
            response.put("message", "참가자 목록을 조회했습니다.");
            response.put("participants", participants);
            response.put("count", participants.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "참가자 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
