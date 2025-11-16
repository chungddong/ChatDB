package com.chatdb.dto;

import com.chatdb.entity.Message;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 */
public class ChatMessageResponse {
    
    /** 메시지 ID */
    private Long messageId;
    
    /** 채팅방 ID */
    private Long chatroomId;
    
    /** 발신자 ID */
    private Long senderId;
    
    /** 발신자 이름 */
    private String senderName;
    
    /** 발신자 프로필 이미지 */
    private String senderProfileImage;
    
    /** 메시지 내용 */
    private String content;
    
    /** 메시지 타입 */
    private Message.MessageType messageType;
    
    /** 발송 시간 */
    private LocalDateTime sendAt;
    
    // === 생성자 ===
    
    public ChatMessageResponse() {
    }
    
    public ChatMessageResponse(Message message) {
        this.messageId = message.getId();
        this.chatroomId = message.getChatRoom().getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getUsername();
        this.senderProfileImage = message.getSender().getProfileImage();
        this.content = message.getMessageContent();
        this.messageType = message.getMessageType();
        this.sendAt = message.getSendAt();
    }
    
    // === Getter/Setter ===
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Long getChatroomId() {
        return chatroomId;
    }
    
    public void setChatroomId(Long chatroomId) {
        this.chatroomId = chatroomId;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderProfileImage() {
        return senderProfileImage;
    }
    
    public void setSenderProfileImage(String senderProfileImage) {
        this.senderProfileImage = senderProfileImage;
    }
    
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
    
    public LocalDateTime getSendAt() {
        return sendAt;
    }
    
    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }
}
