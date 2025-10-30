package com.chatdb.dto;

import com.chatdb.entity.ChatRoom;
import java.time.LocalDateTime;

/**
 * 채팅방 응답 DTO
 */
public class ChatRoomResponse {

    private Long id;
    private String chatroomName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer participantCount;

    // === 기본 생성자 ===
    public ChatRoomResponse() {
    }

    // === 생성자 ===
    public ChatRoomResponse(Long id, String chatroomName, LocalDateTime createdAt,
                           LocalDateTime updatedAt, Integer participantCount) {
        this.id = id;
        this.chatroomName = chatroomName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.participantCount = participantCount;
    }

    // === 정적 팩토리 메서드 ===
    public static ChatRoomResponse from(ChatRoom chatRoom, Integer participantCount) {
        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getChatroomName(),
            chatRoom.getCreatedAt(),
            chatRoom.getUpdatedAt(),
            participantCount
        );
    }

    // === Getter/Setter ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
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

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
}
