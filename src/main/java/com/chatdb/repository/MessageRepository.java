package com.chatdb.repository;

import com.chatdb.entity.ChatRoom;
import com.chatdb.entity.Message;
import com.chatdb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 리포지토리
 * Message 엔티티에 대한 데이터베이스 작업 처리
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 채팅방의 메시지 목록 조회 (페이징)
     * @param chatRoom 채팅방
     * @param pageable 페이징 정보
     * @return 메시지 페이지
     */
    Page<Message> findByChatRoomOrderBySendAtDesc(ChatRoom chatRoom, Pageable pageable);
    
    /**
     * 채팅방의 메시지 목록 조회 (전체)
     * @param chatRoom 채팅방
     * @return 메시지 목록
     */
    List<Message> findByChatRoomOrderBySendAtAsc(ChatRoom chatRoom);
    
    /**
     * 특정 시간 이후의 메시지 개수 조회 (자신이 보낸 메시지 제외)
     * @param chatRoom 채팅방
     * @param userId 사용자 ID
     * @param lastReadAt 마지막 읽은 시간
     * @return 안 읽은 메시지 개수
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom = :chatRoom " +
           "AND m.sender.id != :userId " +
           "AND m.sendAt > :lastReadAt")
    long countUnreadMessages(@Param("chatRoom") ChatRoom chatRoom,
                             @Param("userId") Long userId,
                             @Param("lastReadAt") LocalDateTime lastReadAt);
    
    /**
     * 채팅방의 총 메시지 개수
     * @param chatRoom 채팅방
     * @return 메시지 개수
     */
    long countByChatRoom(ChatRoom chatRoom);
    
    /**
     * 채팅방의 최신 메시지 조회
     * @param chatRoom 채팅방
     * @return 최신 메시지
     */
    Message findFirstByChatRoomOrderBySendAtDesc(ChatRoom chatRoom);
    
    /**
     * 발신자로 메시지 목록 조회
     * @param sender 발신자
     * @return 메시지 목록
     */
    List<Message> findBySender(User sender);
}
