package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * users 테이블과 매핑
 */
@Entity
@Table(name = "users")
public class User {
    
    /** 사용자 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 이메일 (필수, 고유값) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /** 사용자명 (필수) */
    @Column(nullable = false, length = 50)
    private String username;
    
    /** 비밀번호 (필수) */
    @Column(nullable = false)
    private String password;
    
    /** 프로필 이미지 URL (선택) */
    @Column(name = "profile_image", length = 500)
    private String profileImage;
    
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
    
    // ===== 생성자 =====
    
    /** 기본 생성자 */
    public User() {
    }
    
    /**
     * 사용자 생성 생성자
     * @param email 이메일
     * @param username 사용자명
     * @param password 비밀번호
     */
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    
    // ===== Getter/Setter =====
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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
