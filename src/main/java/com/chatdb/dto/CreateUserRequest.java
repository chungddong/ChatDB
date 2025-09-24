package com.chatdb.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 생성 요청")
public class CreateUserRequest {
    
    @Schema(description = "사용자 ID", example = "john123", required = true)
    private String userId;
    
    @Schema(description = "비밀번호", example = "mypassword123", required = true)
    private String password;
    
    // Constructors
    public CreateUserRequest() {}
    
    public CreateUserRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}