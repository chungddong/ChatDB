package com.chatdb.repository;

import com.chatdb.entity.ChatRoom;
import com.chatdb.entity.Participant;
import com.chatdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 참가자 레포지토리
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    /**
     * 특정 채팅방의 모든 참가자 조회
     */
    List<Participant> findByChatRoom(ChatRoom chatRoom);

    /**
     * 특정 채팅방의 참가자 수 조회
     */
    Integer countByChatRoom(ChatRoom chatRoom);

    /**
     * 특정 채팅방에 특정 사용자가 참가 중인지 확인
     */
    Optional<Participant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * 특정 채팅방에 특정 사용자가 참가 중인지 여부
     */
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * 특정 사용자가 참가 중인 모든 채팅방 조회 (Fetch Join 사용)
     */
    @Query("SELECT p FROM Participant p JOIN FETCH p.chatRoom WHERE p.user = :user")
    List<Participant> findByUserWithChatRoom(@Param("user") User user);

    /**
     * 특정 사용자가 참가 중인 모든 채팅방 조회
     */
    List<Participant> findByUser(User user);

    /**
     * 특정 채팅방의 모든 참가자 삭제
     */
    void deleteByChatRoom(ChatRoom chatRoom);
}
