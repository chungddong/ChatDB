package com.chatdb.dto;

/**
 * 친구 요청 DTO
 * 친구 요청을 보낼 때 사용
 */
public class FriendRequestDto {

    /** 친구 요청을 받을 사용자 ID */
    private Long friendUserId;

    // ===== 생성자 =====

    public FriendRequestDto() {
    }

    public FriendRequestDto(Long friendUserId) {
        this.friendUserId = friendUserId;
    }

    // ===== Getter/Setter =====

    public Long getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Long friendUserId) {
        this.friendUserId = friendUserId;
    }
}
