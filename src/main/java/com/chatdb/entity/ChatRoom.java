package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 채팅방 엔티티
 * chatrooms 테이블과 매핑
 */
@Entity
@Table(name = "chatrooms")
public class ChatRoom {
    
    /** 채팅방 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 채팅방 이름 */
    @Column(name = "chatroom_name", nullable = false, length = 100)
    private String chatroomName;
    
    /** 생성 일시 (자동 생성) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /** 수정 일시 (자동 갱신) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 엔티티 생성 시 자동으로 생성/수정 일시 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 엔티티 수정 시 자동으로 수정 일시 갱신
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // === 기본 생성자 ===
    public ChatRoom() {
    }
    
    // === 생성자 ===
    public ChatRoom(String chatroomName) {
        this.chatroomName = chatroomName;
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
}
