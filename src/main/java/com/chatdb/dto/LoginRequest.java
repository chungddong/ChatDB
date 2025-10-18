package com.chatdb.dto;

/**
 * 로그인 요청 DTO
 * 이메일과 비밀번호만 포함
 */
public class LoginRequest {
    
    /** 이메일 */
    private String email;
    
    /** 비밀번호 */
    private String password;
    
    // ===== Getter/Setter =====
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
