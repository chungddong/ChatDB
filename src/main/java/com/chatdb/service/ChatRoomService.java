package com.chatdb.service;

import com.chatdb.dto.ChatRoomResponse;
import com.chatdb.dto.CreateChatRoomRequest;
import com.chatdb.dto.ParticipantResponse;
import com.chatdb.entity.ChatRoom;
import com.chatdb.entity.Participant;
import com.chatdb.entity.User;
import com.chatdb.repository.ChatRoomRepository;
import com.chatdb.repository.ParticipantRepository;
import com.chatdb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 채팅방 서비스
 */
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                          ParticipantRepository participantRepository,
                          UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public ChatRoomResponse createChatRoom(CreateChatRoomRequest request) {
        // 1. 채팅방 생성
        ChatRoom chatRoom = new ChatRoom(request.getChatroomName());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 2. 참가자 추가
        List<Long> participantIds = request.getParticipantIds();
        if (participantIds != null && !participantIds.isEmpty()) {
            for (Long userId : participantIds) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    Participant participant = new Participant(savedChatRoom, userOptional.get());
                    participantRepository.save(participant);
                }
            }
        }

        // 3. 참가자 수 조회
        Integer participantCount = participantRepository.countByChatRoom(savedChatRoom);

        return ChatRoomResponse.from(savedChatRoom, participantCount);
    }

    /**
     * 모든 채팅방 조회
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getAllChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(chatRoom -> {
                    Integer participantCount = participantRepository.countByChatRoom(chatRoom);
                    return ChatRoomResponse.from(chatRoom, participantCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 참여한 채팅방 조회
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return List.of();
        }

        User user = userOptional.get();
        // 사용자가 참여한 모든 참가자 레코드 조회 (Fetch Join 사용)
        List<Participant> participants = participantRepository.findByUserWithChatRoom(user);

        // 참가자 레코드에서 채팅방 추출 및 변환
        return participants.stream()
                .map(participant -> {
                    ChatRoom chatRoom = participant.getChatRoom();
                    Integer participantCount = participantRepository.countByChatRoom(chatRoom);
                    return ChatRoomResponse.from(chatRoom, participantCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 채팅방 조회
     */
    @Transactional(readOnly = true)
    public Optional<ChatRoomResponse> getChatRoomById(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .map(chatRoom -> {
                    Integer participantCount = participantRepository.countByChatRoom(chatRoom);
                    return ChatRoomResponse.from(chatRoom, participantCount);
                });
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    public boolean deleteChatRoom(Long chatroomId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatroomId);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            // 참가자 먼저 삭제
            participantRepository.deleteByChatRoom(chatRoom);
            // 채팅방 삭제
            chatRoomRepository.delete(chatRoom);
            return true;
        }
        return false;
    }

    /**
     * 특정 채팅방의 참가자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipantsByChatRoom(Long chatroomId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatroomId);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            List<Participant> participants = participantRepository.findByChatRoom(chatRoom);
            return participants.stream()
                    .map(ParticipantResponse::from)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * 채팅방 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long chatroomId) {
        return chatRoomRepository.existsById(chatroomId);
    }
}
