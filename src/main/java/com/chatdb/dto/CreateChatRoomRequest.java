package com.chatdb.dto;

import java.util.List;

/**
 * 채팅방 생성 요청 DTO
 */
public class CreateChatRoomRequest {

    /** 채팅방 이름 */
    private String chatroomName;

    /** 참가자 사용자 ID 목록 (생성자 포함) */
    private List<Long> participantIds;

    // === 기본 생성자 ===
    public CreateChatRoomRequest() {
    }

    // === 생성자 ===
    public CreateChatRoomRequest(String chatroomName, List<Long> participantIds) {
        this.chatroomName = chatroomName;
        this.participantIds = participantIds;
    }

    // === Getter/Setter ===
    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }
}
