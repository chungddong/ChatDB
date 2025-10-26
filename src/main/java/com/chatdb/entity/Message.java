package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 메시지 엔티티
 * messages 테이블과 매핑
 */
@Entity
@Table(name = "messages")
public class Message {
    /** 메시지 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 채팅방 ID (외래키) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    /** 발신자 ID (외래키) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /** 메시지 내용 */
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    /** 메시지 타입 (TEXT, IMAGE, FILE 등) */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    private MessageType messageType;

    /** 발송 시간 */
    @Column(name = "send_at", nullable = false)
    private LocalDateTime sendAt;

    /**
     * 메시지 타입 enum
     */
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE
    }

    /**
     * 엔티티 생성 시 자동으로 발송 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        this.sendAt = LocalDateTime.now();
    }

    // === 기본 생성자 ===
    public Message() {
    }

    // === 생성자 ===
    public Message(ChatRoom chatRoom, User sender, String messageContent, MessageType messageType) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.messageContent = messageContent;
        this.messageType = messageType;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }
}
