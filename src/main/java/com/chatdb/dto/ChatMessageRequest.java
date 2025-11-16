package com.chatdb.dto;

import com.chatdb.entity.Message;

/**
 * 채팅 메시지 전송 요청 DTO
 */
public class ChatMessageRequest {
    
    /** 메시지 내용 */
    private String content;
    
    /** 메시지 타입 (기본값: TEXT) */
    private Message.MessageType messageType;
    
    // === 생성자 ===
    
    public ChatMessageRequest() {
        this.messageType = Message.MessageType.TEXT;
    }
    
    public ChatMessageRequest(String content) {
        this.content = content;
        this.messageType = Message.MessageType.TEXT;
    }
    
    // === Getter/Setter ===
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Message.MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }
}
