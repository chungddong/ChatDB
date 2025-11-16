package com.chatdb.service;

import com.chatdb.dto.ChatMessageRequest;
import com.chatdb.dto.ChatMessageResponse;
import com.chatdb.entity.ChatRoom;
import com.chatdb.entity.Message;
import com.chatdb.entity.Participant;
import com.chatdb.entity.User;
import com.chatdb.repository.ChatRoomRepository;
import com.chatdb.repository.MessageRepository;
import com.chatdb.repository.ParticipantRepository;
import com.chatdb.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메시지 서비스
 * 채팅 메시지 관련 비즈니스 로직 처리
 */
@Service
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    
    public MessageService(MessageRepository messageRepository,
                         ChatRoomRepository chatRoomRepository,
                         UserRepository userRepository,
                         ParticipantRepository participantRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
    }
    
    /**
     * 메시지 전송
     * @param chatroomId 채팅방 ID
     * @param userId 발신자 ID
     * @param request 메시지 요청
     * @return 저장된 메시지
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long chatroomId, Long userId, ChatMessageRequest request) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 참가자 확인
        if (!participantRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new IllegalArgumentException("채팅방에 참가하지 않은 사용자입니다.");
        }
        
        // 메시지 생성 및 저장
        Message message = new Message(chatRoom, user, request.getContent(), request.getMessageType());
        Message savedMessage = messageRepository.save(message);
        
        return new ChatMessageResponse(savedMessage);
    }
    
    /**
     * 채팅방의 메시지 목록 조회 (페이징)
     * @param chatroomId 채팅방 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(Long chatroomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendAt"));
        Page<Message> messages = messageRepository.findByChatRoomOrderBySendAtDesc(chatRoom, pageable);
        
        return messages.map(ChatMessageResponse::new);
    }
    
    /**
     * 채팅방의 모든 메시지 조회
     * @param chatroomId 채팅방 ID
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getAllMessages(Long chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        
        List<Message> messages = messageRepository.findByChatRoomOrderBySendAtAsc(chatRoom);
        
        return messages.stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 안 읽은 메시지 개수 조회
     * @param chatroomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 안 읽은 메시지 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long chatroomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        Participant participant = participantRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참가하지 않은 사용자입니다."));
        
        LocalDateTime lastReadAt = participant.getLastReadAt();
        if (lastReadAt == null) {
            lastReadAt = participant.getJoinedAt();
        }
        
        return messageRepository.countUnreadMessages(chatRoom, userId, lastReadAt);
    }
    
    /**
     * 메시지 읽음 처리 (채팅방 입장 시)
     * @param chatroomId 채팅방 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void markAsRead(Long chatroomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        Participant participant = participantRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참가하지 않은 사용자입니다."));
        
        participant.updateLastReadAt();
        participantRepository.save(participant);
    }
    
    /**
     * 메시지 수정
     * @param messageId 메시지 ID
     * @param userId 사용자 ID (권한 확인용)
     * @param newContent 새로운 내용
     * @return 수정된 메시지
     */
    @Transactional
    public ChatMessageResponse updateMessage(Long messageId, Long userId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        
        // 본인이 작성한 메시지인지 확인
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 메시지만 수정할 수 있습니다.");
        }
        
        message.setMessageContent(newContent);
        Message updatedMessage = messageRepository.save(message);
        
        return new ChatMessageResponse(updatedMessage);
    }
    
    /**
     * 메시지 삭제
     * @param messageId 메시지 ID
     * @param userId 사용자 ID (권한 확인용)
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        
        // 본인이 작성한 메시지인지 확인
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 메시지만 삭제할 수 있습니다.");
        }
        
        messageRepository.delete(message);
    }
    
    /**
     * 특정 메시지 조회
     * @param messageId 메시지 ID
     * @return 메시지 응답
     */
    @Transactional(readOnly = true)
    public ChatMessageResponse getMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        
        return new ChatMessageResponse(message);
    }
}
