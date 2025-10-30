package com.chatdb.dto;

import com.chatdb.entity.Participant;
import java.time.LocalDateTime;

/**
 * 참가자 응답 DTO
 */
public class ParticipantResponse {

    private Long participantId;
    private Long userId;
    private String username;
    private String email;
    private String profileImage;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;

    // === 기본 생성자 ===
    public ParticipantResponse() {
    }

    // === 생성자 ===
    public ParticipantResponse(Long participantId, Long userId, String username,
                              String email, String profileImage, LocalDateTime joinedAt,
                              LocalDateTime lastReadAt) {
        this.participantId = participantId;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.joinedAt = joinedAt;
        this.lastReadAt = lastReadAt;
    }

    // === 정적 팩토리 메서드 ===
    public static ParticipantResponse from(Participant participant) {
        return new ParticipantResponse(
            participant.getId(),
            participant.getUser().getId(),
            participant.getUser().getUsername(),
            participant.getUser().getEmail(),
            participant.getUser().getProfileImage(),
            participant.getJoinedAt(),
            participant.getLastReadAt()
        );
    }

    // === Getter/Setter ===
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
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

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
