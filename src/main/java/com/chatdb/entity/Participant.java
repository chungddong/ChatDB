package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 채팅방 참가자 엔티티
 * participants 테이블과 매핑
 */
@Entity
@Table(name = "participants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"chatroom_id", "user_id"}))
public class Participant {
    
    /** 참가자 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 채팅방 ID (외래키) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;
    
    /** 사용자 ID (외래키) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /** 참가 일시 */
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    /** 마지막 읽은 시간 */
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
    
    /**
     * 엔티티 생성 시 자동으로 참가 일시 설정
     */
    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
        this.lastReadAt = LocalDateTime.now();
    }
    
    // === 기본 생성자 ===
    public Participant() {
    }
    
    // === 생성자 ===
    public Participant(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
    }
    
    // === Getter/Setter ===
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ChatRoom getChatRoom() {
        return chatRoom;
    }
    
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    // === 비즈니스 메서드 ===
    
    /**
     * 메시지 읽음 처리
     */
    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
