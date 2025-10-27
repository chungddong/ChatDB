package com.chatdb.dto;

import com.chatdb.entity.Friendship;
import java.time.LocalDateTime;

/**
 * 친구 관계 응답 DTO
 * 친구 요청 및 친구 목록 조회 시 사용
 */
public class FriendshipResponseDto {

    /** 친구 관계 ID */
    private Long id;

    /** 요청을 보낸 사용자 ID */
    private Long userId;

    /** 요청을 보낸 사용자 이름 */
    private String username;

    /** 요청을 보낸 사용자 이메일 */
    private String email;

    /** 요청을 보낸 사용자 프로필 이미지 */
    private String profileImage;

    /** 친구 요청 상태 */
    private String status;

    /** 생성 일시 */
    private LocalDateTime createdAt;

    /** 수정 일시 */
    private LocalDateTime updatedAt;

    // ===== 생성자 =====

    public FriendshipResponseDto() {
    }

    /**
     * Friendship 엔티티로부터 DTO 생성 (받은 요청용)
     * @param friendship 친구 관계 엔티티
     * @return FriendshipResponseDto
     */
    public static FriendshipResponseDto fromReceivedRequest(Friendship friendship) {
        FriendshipResponseDto dto = new FriendshipResponseDto();
        dto.setId(friendship.getId());
        dto.setUserId(friendship.getUser().getId());
        dto.setUsername(friendship.getUser().getUsername());
        dto.setEmail(friendship.getUser().getEmail());
        dto.setProfileImage(friendship.getUser().getProfileImage());
        dto.setStatus(friendship.getStatus().name());
        dto.setCreatedAt(friendship.getCreatedAt());
        dto.setUpdatedAt(friendship.getUpdatedAt());
        return dto;
    }

    /**
     * Friendship 엔티티로부터 DTO 생성 (보낸 요청용)
     * @param friendship 친구 관계 엔티티
     * @return FriendshipResponseDto
     */
    public static FriendshipResponseDto fromSentRequest(Friendship friendship) {
        FriendshipResponseDto dto = new FriendshipResponseDto();
        dto.setId(friendship.getId());
        dto.setUserId(friendship.getFriendUser().getId());
        dto.setUsername(friendship.getFriendUser().getUsername());
        dto.setEmail(friendship.getFriendUser().getEmail());
        dto.setProfileImage(friendship.getFriendUser().getProfileImage());
        dto.setStatus(friendship.getStatus().name());
        dto.setCreatedAt(friendship.getCreatedAt());
        dto.setUpdatedAt(friendship.getUpdatedAt());
        return dto;
    }

    /**
     * Friendship 엔티티로부터 친구 목록용 DTO 생성
     * @param friendship 친구 관계 엔티티
     * @param currentUserId 현재 사용자 ID (상대방 정보를 가져오기 위함)
     * @return FriendshipResponseDto
     */
    public static FriendshipResponseDto fromFriendship(Friendship friendship, Long currentUserId) {
        FriendshipResponseDto dto = new FriendshipResponseDto();
        dto.setId(friendship.getId());

        // 현재 사용자가 아닌 상대방의 정보를 설정
        if (friendship.getUser().getId().equals(currentUserId)) {
            dto.setUserId(friendship.getFriendUser().getId());
            dto.setUsername(friendship.getFriendUser().getUsername());
            dto.setEmail(friendship.getFriendUser().getEmail());
            dto.setProfileImage(friendship.getFriendUser().getProfileImage());
        } else {
            dto.setUserId(friendship.getUser().getId());
            dto.setUsername(friendship.getUser().getUsername());
            dto.setEmail(friendship.getUser().getEmail());
            dto.setProfileImage(friendship.getUser().getProfileImage());
        }

        dto.setStatus(friendship.getStatus().name());
        dto.setCreatedAt(friendship.getCreatedAt());
        dto.setUpdatedAt(friendship.getUpdatedAt());
        return dto;
    }

    // ===== Getter/Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
