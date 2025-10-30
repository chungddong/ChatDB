package com.chatdb.controller;

import com.chatdb.dto.FriendRequestDto;
import com.chatdb.dto.FriendshipResponseDto;
import com.chatdb.entity.Friendship;
import com.chatdb.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 친구 관리 컨트롤러
 * 친구 요청, 수락, 거절, 친구 목록 조회 등의 API 제공
 */
@RestController
@RequestMapping("/api/friends")
@Tag(name = "Friendship", description = "친구 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * 친구 요청 보내기
     * @param authentication 인증 정보 (JWT에서 자동으로 추출된 사용자 ID)
     * @param requestDto 친구 요청 정보 (친구 사용자 이메일)
     * @return 친구 요청 결과
     */
    @PostMapping("/request")
    @Operation(summary = "친구 요청 보내기", description = "특정 사용자의 이메일로 친구 요청을 보냅니다.")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            Authentication authentication,
            @RequestBody FriendRequestDto requestDto) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            Friendship friendship = friendshipService.sendFriendRequest(userId, requestDto.getFriendEmail());

            response.put("success", true);
            response.put("message", "친구 요청을 보냈습니다.");
            response.put("requestId", friendship.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 받은 친구 요청 목록 조회
     * @param authentication 인증 정보
     * @return 받은 친구 요청 목록
     */
    @GetMapping("/requests/received")
    @Operation(summary = "받은 친구 요청 목록 조회", description = "내가 받은 친구 요청 목록을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getReceivedFriendRequests(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            List<FriendshipResponseDto> requests = friendshipService.getReceivedFriendRequests(userId);

            response.put("success", true);
            response.put("message", "받은 친구 요청 목록을 조회했습니다.");
            response.put("requests", requests);
            response.put("count", requests.size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 보낸 친구 요청 목록 조회
     * @param authentication 인증 정보
     * @return 보낸 친구 요청 목록
     */
    @GetMapping("/requests/sent")
    @Operation(summary = "보낸 친구 요청 목록 조회", description = "내가 보낸 친구 요청 목록을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getSentFriendRequests(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            List<FriendshipResponseDto> requests = friendshipService.getSentFriendRequests(userId);

            response.put("success", true);
            response.put("message", "보낸 친구 요청 목록을 조회했습니다.");
            response.put("requests", requests);
            response.put("count", requests.size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 친구 요청 수락
     * @param requestId 친구 요청 ID
     * @param authentication 인증 정보
     * @return 수락 결과
     */
    @PostMapping("/requests/{requestId}/accept")
    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(
            @PathVariable Long requestId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            Friendship friendship = friendshipService.acceptFriendRequest(requestId, userId);

            response.put("success", true);
            response.put("message", "친구 요청을 수락했습니다.");
            response.put("friendshipId", friendship.getId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 수락 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 친구 요청 거절
     * @param requestId 친구 요청 ID
     * @param authentication 인증 정보
     * @return 거절 결과
     */
    @PostMapping("/requests/{requestId}/reject")
    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(
            @PathVariable Long requestId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            friendshipService.rejectFriendRequest(requestId, userId);

            response.put("success", true);
            response.put("message", "친구 요청을 거절했습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 거절 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 친구 목록 조회
     * @param authentication 인증 정보
     * @return 친구 목록
     */
    @GetMapping("/list")
    @Operation(summary = "친구 목록 조회", description = "내 친구 목록을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getFriendList(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            List<FriendshipResponseDto> friends = friendshipService.getFriendList(userId);

            response.put("success", true);
            response.put("message", "친구 목록을 조회했습니다.");
            response.put("friends", friends);
            response.put("count", friends.size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 친구 요청 취소
     * @param requestId 친구 요청 ID
     * @param authentication 인증 정보
     * @return 취소 결과
     */
    @DeleteMapping("/requests/{requestId}/cancel")
    @Operation(summary = "친구 요청 취소", description = "보낸 친구 요청을 취소합니다.")
    public ResponseEntity<Map<String, Object>> cancelFriendRequest(
            @PathVariable Long requestId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            friendshipService.cancelFriendRequest(requestId, userId);

            response.put("success", true);
            response.put("message", "친구 요청을 취소했습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 요청 취소 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 친구 삭제
     * @param friendshipId 친구 관계 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{friendshipId}")
    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @PathVariable Long friendshipId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            friendshipService.deleteFriend(friendshipId, userId);

            response.put("success", true);
            response.put("message", "친구를 삭제했습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "친구 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
