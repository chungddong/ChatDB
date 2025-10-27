package com.chatdb.repository;

import com.chatdb.entity.Friendship;
import com.chatdb.entity.Friendship.FriendshipStatus;
import com.chatdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 친구 관계 리포지토리
 * Friendship 엔티티에 대한 데이터베이스 작업 처리
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * 두 사용자 간의 친구 관계 조회
     * @param user 사용자1
     * @param friendUser 사용자2
     * @return 친구 관계 Optional
     */
    Optional<Friendship> findByUserAndFriendUser(User user, User friendUser);

    /**
     * 특정 사용자가 받은 친구 요청 목록 조회 (대기중인 요청만)
     * @param friendUser 친구 요청을 받은 사용자
     * @param status 요청 상태
     * @return 친구 요청 목록
     */
    List<Friendship> findByFriendUserAndStatus(User friendUser, FriendshipStatus status);

    /**
     * 특정 사용자가 보낸 친구 요청 목록 조회
     * @param user 친구 요청을 보낸 사용자
     * @param status 요청 상태
     * @return 친구 요청 목록
     */
    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);

    /**
     * 특정 사용자의 친구 목록 조회 (양방향 검색)
     * @param userId 사용자 ID
     * @return 친구 목록
     */
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user.id = :userId OR f.friendUser.id = :userId) " +
           "AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("userId") Long userId);

    /**
     * 두 사용자 간의 친구 관계 존재 여부 확인 (양방향)
     * @param userId1 사용자1 ID
     * @param userId2 사용자2 ID
     * @return 존재 여부
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f WHERE " +
           "((f.user.id = :userId1 AND f.friendUser.id = :userId2) OR " +
           "(f.user.id = :userId2 AND f.friendUser.id = :userId1))")
    boolean existsFriendship(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
