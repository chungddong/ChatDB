package com.chatdb.dto;

/**
 * 친구 요청 DTO
 * 친구 요청을 보낼 때 사용
 */
public class FriendRequestDto {

    /** 친구 요청을 받을 사용자 이메일 */
    private String friendEmail;

    // ===== 생성자 =====

    public FriendRequestDto() {
    }

    public FriendRequestDto(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    // ===== Getter/Setter =====

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }
}
