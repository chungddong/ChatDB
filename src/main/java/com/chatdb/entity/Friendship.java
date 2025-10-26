package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 친구 관계 엔티티
 * friendships 테이블과 매핑
 */
@Entity
@Table(name = "friendships", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_user_id"}))
public class Friendship {
    
    /** 친구 관계 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 친구 요청을 보낸 사용자 ID */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /** 친구 요청을 받는 사용자 ID */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id", nullable = false)
    private User friendUser;
    
    /** 친구 요청 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendshipStatus status;
    
    /** 생성 일시 (요청 시간) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /** 수정 일시 (수락/거절 시간) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 친구 관계 상태 enum
     */
    public enum FriendshipStatus {
        /** 대기중 */
        PENDING,
        /** 수락됨 */
        ACCEPTED,
        /** 거절됨 */
        REJECTED,
        /** 차단됨 */
        BLOCKED
    }
    
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
    public Friendship() {
    }
    
    // === 생성자 ===
    public Friendship(User user, User friendUser, FriendshipStatus status) {
        this.user = user;
        this.friendUser = friendUser;
        this.status = status;
    }
    
    // === Getter/Setter ===
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getFriendUser() {
        return friendUser;
    }
    
    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
    
    public FriendshipStatus getStatus() {
        return status;
    }
    
    public void setStatus(FriendshipStatus status) {
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
    
    // === 비즈니스 메서드 ===
    
    /**
     * 친구 요청 수락
     */
    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }
    
    /**
     * 친구 요청 거절
     */
    public void reject() {
        this.status = FriendshipStatus.REJECTED;
    }
    
    /**
     * 친구 차단
     */
    public void block() {
        this.status = FriendshipStatus.BLOCKED;
    }
    
    /**
     * 대기중인 요청인지 확인
     */
    public boolean isPending() {
        return this.status == FriendshipStatus.PENDING;
    }
    
    /**
     * 수락된 친구 관계인지 확인
     */
    public boolean isAccepted() {
        return this.status == FriendshipStatus.ACCEPTED;
    }
}
