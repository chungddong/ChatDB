package com.chatdb.repository;

import com.chatdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 리포지토리
 * User 엔티티에 대한 데이터베이스 작업 처리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명으로 사용자 조회
     * @param username 사용자명
     * @return 사용자 Optional
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일 존재 여부 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 사용자명 존재 여부 확인
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
}
