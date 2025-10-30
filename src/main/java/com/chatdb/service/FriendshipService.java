package com.chatdb.service;

import com.chatdb.dto.FriendshipResponseDto;
import com.chatdb.entity.Friendship;
import com.chatdb.entity.Friendship.FriendshipStatus;
import com.chatdb.entity.User;
import com.chatdb.repository.FriendshipRepository;
import com.chatdb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 친구 관계 서비스
 * 친구 요청, 수락, 거절, 친구 목록 조회 등의 비즈니스 로직 처리
 */
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * 친구 요청 보내기 (이메일로)
     * @param userId 요청을 보내는 사용자 ID
     * @param friendEmail 요청을 받을 사용자 이메일
     * @return 생성된 친구 관계
     */
    @Transactional
    public Friendship sendFriendRequest(Long userId, String friendEmail) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이메일로 친구 사용자 조회
        User friendUser = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));

        // 자기 자신에게 요청하는 경우
        if (userId.equals(friendUser.getId())) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 친구 관계가 존재하는지 확인 (양방향)
        if (friendshipRepository.existsFriendship(userId, friendUser.getId())) {
            throw new IllegalArgumentException("이미 친구 요청이 존재하거나 친구 관계입니다.");
        }

        // 친구 요청 생성
        Friendship friendship = new Friendship(user, friendUser, FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    /**
     * 받은 친구 요청 목록 조회
     * @param userId 사용자 ID
     * @return 받은 친구 요청 목록
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDto> getReceivedFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Friendship> requests = friendshipRepository.findByFriendUserAndStatus(user, FriendshipStatus.PENDING);
        return requests.stream()
                .map(FriendshipResponseDto::fromReceivedRequest)
                .collect(Collectors.toList());
    }

    /**
     * 보낸 친구 요청 목록 조회
     * @param userId 사용자 ID
     * @return 보낸 친구 요청 목록
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDto> getSentFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Friendship> requests = friendshipRepository.findByUserAndStatus(user, FriendshipStatus.PENDING);
        return requests.stream()
                .map(FriendshipResponseDto::fromSentRequest)
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 수락
     * @param requestId 친구 요청 ID
     * @param userId 요청을 받은 사용자 ID
     * @return 수락된 친구 관계
     */
    @Transactional
    public Friendship acceptFriendRequest(Long requestId, Long userId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 요청을 받은 사용자가 맞는지 확인
        if (!friendship.getFriendUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인에게 온 친구 요청만 수락할 수 있습니다.");
        }

        // 이미 처리된 요청인지 확인
        if (!friendship.isPending()) {
            throw new IllegalArgumentException("이미 처리된 친구 요청입니다.");
        }

        // 친구 요청 수락
        friendship.accept();
        return friendshipRepository.save(friendship);
    }

    /**
     * 친구 요청 거절
     * @param requestId 친구 요청 ID
     * @param userId 요청을 받은 사용자 ID
     * @return 거절된 친구 관계
     */
    @Transactional
    public Friendship rejectFriendRequest(Long requestId, Long userId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 요청을 받은 사용자가 맞는지 확인
        if (!friendship.getFriendUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인에게 온 친구 요청만 거절할 수 있습니다.");
        }

        // 이미 처리된 요청인지 확인
        if (!friendship.isPending()) {
            throw new IllegalArgumentException("이미 처리된 친구 요청입니다.");
        }

        // 친구 요청 거절
        friendship.reject();
        return friendshipRepository.save(friendship);
    }

    /**
     * 친구 목록 조회
     * @param userId 사용자 ID
     * @return 친구 목록
     */
    @Transactional(readOnly = true)
    public List<FriendshipResponseDto> getFriendList(Long userId) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        List<Friendship> friendships = friendshipRepository.findAcceptedFriendships(userId);
        return friendships.stream()
                .map(friendship -> FriendshipResponseDto.fromFriendship(friendship, userId))
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 취소 (보낸 요청 취소)
     * @param requestId 친구 요청 ID
     * @param userId 요청을 보낸 사용자 ID
     */
    @Transactional
    public void cancelFriendRequest(Long requestId, Long userId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 요청을 보낸 사용자가 맞는지 확인
        if (!friendship.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 보낸 친구 요청만 취소할 수 있습니다.");
        }

        // 대기중인 요청만 취소 가능
        if (!friendship.isPending()) {
            throw new IllegalArgumentException("대기중인 친구 요청만 취소할 수 있습니다.");
        }

        friendshipRepository.delete(friendship);
    }

    /**
     * 친구 삭제
     * @param friendshipId 친구 관계 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteFriend(Long friendshipId, Long userId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        // 본인과 관련된 친구 관계인지 확인
        if (!friendship.getUser().getId().equals(userId) && !friendship.getFriendUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 친구 관계만 삭제할 수 있습니다.");
        }

        // 수락된 친구 관계만 삭제 가능
        if (!friendship.isAccepted()) {
            throw new IllegalArgumentException("수락된 친구 관계만 삭제할 수 있습니다.");
        }

        friendshipRepository.delete(friendship);
    }
}
