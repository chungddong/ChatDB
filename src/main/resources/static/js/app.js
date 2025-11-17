// API 기본 설정
const BASE_URL = 'http://localhost:7070';
const API_KEY = 'your-secure-api-key-change-this-in-production';

// 전역 변수
let currentUser = null;
let currentChatRoom = null;
let stompClient = null;
let currentEditingMessageId = null;

// 기본 헤더
const baseHeaders = {
    'Content-Type': 'application/json',
    'X-API-Key': API_KEY
};

// 인증 헤더 생성
const authHeaders = (token) => ({
    'Content-Type': 'application/json',
    'X-API-Key': API_KEY,
    'Authorization': `Bearer ${token}`
});

// 토큰 가져오기
function getToken() {
    return localStorage.getItem('jwt_token');
}

// 현재 사용자 정보 가져오기
function getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

// ==================== 알림 메시지 ====================
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// ==================== 화면 전환 ====================
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });
    document.getElementById(screenId).classList.add('active');
}

// ==================== 로그인/회원가입 ====================
function showLoginForm() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
}

function showRegisterForm() {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    document.getElementById('registerForm').classList.add('active');
    document.getElementById('loginForm').classList.remove('active');
}

// 회원가입
async function register(email, username, password, profileImage = null) {
    try {
        const response = await fetch(`${BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: baseHeaders,
            body: JSON.stringify({ email, username, password, profileImage })
        });

        const data = await response.json();

        if (data.success) {
            showToast('회원가입이 완료되었습니다. 로그인해주세요.');
            showLoginForm();
            document.getElementById('registerForm').reset();
        } else {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('서버와 연결할 수 없습니다.', 'error');
        console.error('Register error:', error);
    }
}

// 로그인
async function login(email, password) {
    try {
        const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: baseHeaders,
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (data.success && data.token) {
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            currentUser = data.user;

            showToast('로그인 성공!');
            initChatScreen();
            showScreen('chatScreen');
        } else {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('서버와 연결할 수 없습니다.', 'error');
        console.error('Login error:', error);
    }
}

// 로그아웃
function logout() {
    if (stompClient) {
        stompClient.disconnect();
    }
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
    currentUser = null;
    currentChatRoom = null;
    showScreen('loginScreen');
    showToast('로그아웃되었습니다.');
}

// ==================== 채팅 화면 초기화 ====================
function initChatScreen() {
    currentUser = getCurrentUser();

    // 사용자 정보 표시
    document.getElementById('userName').textContent = currentUser.username;
    document.getElementById('userEmail').textContent = currentUser.email;
    if (currentUser.profileImage) {
        document.getElementById('userProfileImg').src = currentUser.profileImage;
    }

    // WebSocket 연결
    connectWebSocket();

    // 데이터 로드
    loadChatRooms();
    loadFriends();
}

// ==================== WebSocket 연결 ====================
function connectWebSocket() {
    const socket = new SockJS(`${BASE_URL}/ws`);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('WebSocket Connected: ' + frame);

        // 전체 사용자 알림 구독 (선택사항)
        stompClient.subscribe('/topic/notifications', function(notification) {
            console.log('Notification:', notification.body);
        });
    }, function(error) {
        console.error('WebSocket Error:', error);
        showToast('실시간 연결에 실패했습니다.', 'error');
    });
}

// 채팅방 구독
function subscribeToChatRoom(chatroomId) {
    if (stompClient && stompClient.connected) {
        stompClient.subscribe(`/topic/chatroom/${chatroomId}`, function(message) {
            const chatMessage = JSON.parse(message.body);
            displayMessage(chatMessage);

            // 메시지 읽음 처리
            if (chatMessage.senderId !== currentUser.id) {
                markAsRead(chatroomId);
            }
        });
    }
}

// 메시지 전송 (WebSocket)
function sendMessageViaWebSocket(chatroomId, content) {
    if (stompClient && stompClient.connected) {
        const message = {
            content: content,
            type: 'TEXT'
        };

        stompClient.send(`/app/chat/${chatroomId}`, {}, JSON.stringify(message));
    }
}

// ==================== 친구 관리 ====================
async function sendFriendRequest(friendEmail) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/request`, {
            method: 'POST',
            headers: authHeaders(token),
            body: JSON.stringify({ friendEmail })
        });

        const data = await response.json();

        if (data.success) {
            showToast('친구 요청을 보냈습니다.');
            closeModal('addFriendModal');
            document.getElementById('addFriendForm').reset();
        } else {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('친구 요청 중 오류가 발생했습니다.', 'error');
        console.error('Send friend request error:', error);
    }
}

async function loadFriends() {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/list`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            displayFriends(data.friends);
        }

        return data;
    } catch (error) {
        console.error('Load friends error:', error);
    }
}

function displayFriends(friends) {
    const friendItems = document.getElementById('friendItems');

    if (friends.length === 0) {
        friendItems.innerHTML = `
            <div class="empty-state">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                    <path d="M16 3.13a4 4 0 010 7.75"/>
                </svg>
                <p>친구가 없습니다</p>
            </div>
        `;
        return;
    }

    friendItems.innerHTML = friends.map(friend => `
        <div class="list-item">
            <img src="${friend.profileImage || 'https://via.placeholder.com/40'}"
                 alt="${friend.username}"
                 class="profile-img">
            <div class="item-info">
                <div class="item-name">${friend.username}</div>
                <div class="item-detail">${friend.email}</div>
            </div>
            <div class="item-actions">
                <button class="btn btn-danger btn-small" onclick="deleteFriend(${friend.id})">삭제</button>
            </div>
        </div>
    `).join('');
}

async function deleteFriend(friendshipId) {
    if (!confirm('정말 친구를 삭제하시겠습니까?')) return;

    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/${friendshipId}`, {
            method: 'DELETE',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('친구를 삭제했습니다.');
            loadFriends();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('친구 삭제 중 오류가 발생했습니다.', 'error');
        console.error('Delete friend error:', error);
    }
}

// 친구 요청 관련
async function loadReceivedRequests() {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/requests/received`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            displayReceivedRequests(data.requests);
        }

        return data;
    } catch (error) {
        console.error('Load received requests error:', error);
    }
}

async function loadSentRequests() {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/requests/sent`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            displaySentRequests(data.requests);
        }

        return data;
    } catch (error) {
        console.error('Load sent requests error:', error);
    }
}

function displayReceivedRequests(requests) {
    const receivedItems = document.getElementById('receivedRequestItems');

    if (requests.length === 0) {
        receivedItems.innerHTML = `
            <div class="empty-state">
                <p>받은 친구 요청이 없습니다</p>
            </div>
        `;
        return;
    }

    receivedItems.innerHTML = requests.map(request => `
        <div class="list-item">
            <img src="${request.profileImage || 'https://via.placeholder.com/40'}"
                 alt="${request.username}"
                 class="profile-img">
            <div class="item-info">
                <div class="item-name">${request.username}</div>
                <div class="item-detail">${request.email}</div>
            </div>
            <div class="item-actions">
                <button class="btn btn-success btn-small" onclick="acceptFriendRequest(${request.id})">수락</button>
                <button class="btn btn-danger btn-small" onclick="rejectFriendRequest(${request.id})">거절</button>
            </div>
        </div>
    `).join('');
}

function displaySentRequests(requests) {
    const sentItems = document.getElementById('sentRequestItems');

    if (requests.length === 0) {
        sentItems.innerHTML = `
            <div class="empty-state">
                <p>보낸 친구 요청이 없습니다</p>
            </div>
        `;
        return;
    }

    sentItems.innerHTML = requests.map(request => `
        <div class="list-item">
            <img src="${request.profileImage || 'https://via.placeholder.com/40'}"
                 alt="${request.username}"
                 class="profile-img">
            <div class="item-info">
                <div class="item-name">${request.username}</div>
                <div class="item-detail">${request.email}</div>
                <div class="item-detail">상태: ${request.status}</div>
            </div>
            <div class="item-actions">
                <button class="btn btn-danger btn-small" onclick="cancelFriendRequest(${request.id})">취소</button>
            </div>
        </div>
    `).join('');
}

async function acceptFriendRequest(requestId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/accept`, {
            method: 'POST',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('친구 요청을 수락했습니다.');
            loadReceivedRequests();
            loadFriends();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('친구 요청 수락 중 오류가 발생했습니다.', 'error');
        console.error('Accept friend request error:', error);
    }
}

async function rejectFriendRequest(requestId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/reject`, {
            method: 'POST',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('친구 요청을 거절했습니다.');
            loadReceivedRequests();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('친구 요청 거절 중 오류가 발생했습니다.', 'error');
        console.error('Reject friend request error:', error);
    }
}

async function cancelFriendRequest(requestId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/cancel`, {
            method: 'DELETE',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('친구 요청을 취소했습니다.');
            loadSentRequests();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('친구 요청 취소 중 오류가 발생했습니다.', 'error');
        console.error('Cancel friend request error:', error);
    }
}

// ==================== 채팅방 관리 ====================
async function loadChatRooms() {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/chatrooms`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            displayChatRooms(data.chatRooms);
        }

        return data;
    } catch (error) {
        console.error('Load chat rooms error:', error);
    }
}

function displayChatRooms(chatRooms) {
    const chatRoomItems = document.getElementById('chatRoomItems');

    if (chatRooms.length === 0) {
        chatRoomItems.innerHTML = `
            <div class="empty-state">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
                </svg>
                <p>채팅방이 없습니다</p>
            </div>
        `;
        return;
    }

    chatRoomItems.innerHTML = chatRooms.map(room => `
        <div class="list-item ${currentChatRoom && currentChatRoom.id === room.id ? 'active' : ''}"
             onclick="selectChatRoom(${room.id})">
            <div class="item-info">
                <div class="item-name">${room.chatroomName}</div>
                <div class="item-detail">${room.participantCount}명</div>
            </div>
        </div>
    `).join('');
}

async function selectChatRoom(chatroomId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            currentChatRoom = data.chatRoom;

            // UI 업데이트
            document.getElementById('noChatSelected').style.display = 'none';
            document.getElementById('chatContent').style.display = 'flex';
            document.getElementById('chatRoomName').textContent = currentChatRoom.chatroomName;
            document.getElementById('chatRoomParticipants').textContent = `${currentChatRoom.participantCount}명`;

            // 채팅방 목록 활성화 표시
            document.querySelectorAll('#chatRoomItems .list-item').forEach(item => {
                item.classList.remove('active');
            });
            event.target.closest('.list-item').classList.add('active');

            // 메시지 로드
            loadMessages(chatroomId);

            // WebSocket 구독
            subscribeToChatRoom(chatroomId);

            // 읽음 처리
            markAsRead(chatroomId);
        }
    } catch (error) {
        console.error('Select chat room error:', error);
    }
}

async function createChatRoom(chatroomName, participantIds) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/chatrooms`, {
            method: 'POST',
            headers: authHeaders(token),
            body: JSON.stringify({ chatroomName, participantIds })
        });

        const data = await response.json();

        if (data.success) {
            showToast('채팅방이 생성되었습니다.');
            closeModal('createChatRoomModal');
            document.getElementById('createChatRoomForm').reset();
            loadChatRooms();
        } else {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('채팅방 생성 중 오류가 발생했습니다.', 'error');
        console.error('Create chat room error:', error);
    }
}

async function deleteChatRoom(chatroomId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
            method: 'DELETE',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('채팅방을 나갔습니다.');
            currentChatRoom = null;
            document.getElementById('noChatSelected').style.display = 'flex';
            document.getElementById('chatContent').style.display = 'none';
            loadChatRooms();
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('채팅방 삭제 중 오류가 발생했습니다.', 'error');
        console.error('Delete chat room error:', error);
    }
}

function confirmDeleteChatRoom() {
    if (currentChatRoom && confirm('정말 채팅방을 나가시겠습니까?')) {
        deleteChatRoom(currentChatRoom.id);
    }
}

async function loadParticipants(chatroomId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}/participants`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            displayParticipants(data.participants);
        }

        return data;
    } catch (error) {
        console.error('Load participants error:', error);
    }
}

function displayParticipants(participants) {
    const participantsList = document.getElementById('participantsList');

    participantsList.innerHTML = participants.map(p => `
        <div class="participant-item">
            <img src="${p.profileImage || 'https://via.placeholder.com/40'}" alt="${p.username}">
            <div class="participant-info">
                <div class="participant-name">${p.username}</div>
                <div class="participant-email">${p.email}</div>
            </div>
        </div>
    `).join('');
}

// ==================== 메시지 관리 ====================
async function loadMessages(chatroomId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/messages/chatrooms/${chatroomId}/all`, {
            method: 'GET',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            const messageArea = document.getElementById('messageArea');
            messageArea.innerHTML = '';
            data.messages.forEach(msg => displayMessage(msg));

            // 스크롤을 맨 아래로
            messageArea.scrollTop = messageArea.scrollHeight;
        }

        return data;
    } catch (error) {
        console.error('Load messages error:', error);
    }
}

function displayMessage(message) {
    const messageArea = document.getElementById('messageArea');
    const isSent = message.senderId === currentUser.id;

    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${isSent ? 'sent' : 'received'}`;
    messageDiv.dataset.messageId = message.id;

    messageDiv.innerHTML = `
        <img src="${message.senderProfileImage || 'https://via.placeholder.com/36'}"
             alt="${message.senderName}"
             class="message-avatar">
        <div class="message-content">
            <div class="message-header">
                <span class="message-sender">${message.senderName}</span>
                <span class="message-time">${formatTime(message.createdAt)}</span>
            </div>
            <div class="message-bubble">
                ${message.content}
                ${message.edited ? '<div class="message-edited">(수정됨)</div>' : ''}
            </div>
            ${isSent ? `
                <div class="message-actions">
                    <button class="message-action-btn" onclick="editMessage(${message.id}, '${message.content.replace(/'/g, "\\'")}')">수정</button>
                    <button class="message-action-btn" onclick="deleteMessage(${message.id})">삭제</button>
                </div>
            ` : ''}
        </div>
    `;

    messageArea.appendChild(messageDiv);
    messageArea.scrollTop = messageArea.scrollHeight;
}

async function sendMessage(chatroomId, content) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/messages/chatrooms/${chatroomId}`, {
            method: 'POST',
            headers: authHeaders(token),
            body: JSON.stringify({ content, type: 'TEXT' })
        });

        const data = await response.json();

        if (!data.success) {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('메시지 전송 중 오류가 발생했습니다.', 'error');
        console.error('Send message error:', error);
    }
}

async function updateMessage(messageId, content) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/messages/${messageId}`, {
            method: 'PUT',
            headers: authHeaders(token),
            body: JSON.stringify({ content })
        });

        const data = await response.json();

        if (data.success) {
            showToast('메시지를 수정했습니다.');
            loadMessages(currentChatRoom.id);
        } else {
            showToast(data.message, 'error');
        }

        return data;
    } catch (error) {
        showToast('메시지 수정 중 오류가 발생했습니다.', 'error');
        console.error('Update message error:', error);
    }
}

async function deleteMessage(messageId) {
    if (!confirm('메시지를 삭제하시겠습니까?')) return;

    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/api/messages/${messageId}`, {
            method: 'DELETE',
            headers: authHeaders(token)
        });

        const data = await response.json();

        if (data.success) {
            showToast('메시지를 삭제했습니다.');
            loadMessages(currentChatRoom.id);
        } else {
            showToast(data.message, 'error');
        }
    } catch (error) {
        showToast('메시지 삭제 중 오류가 발생했습니다.', 'error');
        console.error('Delete message error:', error);
    }
}

function editMessage(messageId, currentContent) {
    currentEditingMessageId = messageId;
    document.getElementById('editMessageContent').value = currentContent;
    openModal('editMessageModal');
}

async function markAsRead(chatroomId) {
    try {
        const token = getToken();
        await fetch(`${BASE_URL}/api/messages/chatrooms/${chatroomId}/read`, {
            method: 'POST',
            headers: authHeaders(token)
        });
    } catch (error) {
        console.error('Mark as read error:', error);
    }
}

// ==================== UI 헬퍼 함수 ====================
function showChatRooms() {
    document.querySelectorAll('.sidebar-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    document.querySelectorAll('.sidebar-content').forEach(content => content.classList.remove('active'));
    document.getElementById('chatRoomList').classList.add('active');

    loadChatRooms();
}

function showFriends() {
    document.querySelectorAll('.sidebar-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    document.querySelectorAll('.sidebar-content').forEach(content => content.classList.remove('active'));
    document.getElementById('friendList').classList.add('active');

    loadFriends();
}

function showFriendRequests() {
    document.querySelectorAll('.sidebar-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    document.querySelectorAll('.sidebar-content').forEach(content => content.classList.remove('active'));
    document.getElementById('friendRequestList').classList.add('active');

    loadReceivedRequests();
    loadSentRequests();
}

function showReceivedRequests() {
    document.querySelectorAll('.request-tab').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    document.getElementById('receivedRequestItems').classList.add('active');
    document.getElementById('sentRequestItems').classList.remove('active');

    loadReceivedRequests();
}

function showSentRequests() {
    document.querySelectorAll('.request-tab').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    document.getElementById('receivedRequestItems').classList.remove('active');
    document.getElementById('sentRequestItems').classList.add('active');

    loadSentRequests();
}

function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

function showAddFriendModal() {
    openModal('addFriendModal');
}

async function showCreateChatRoomModal() {
    openModal('createChatRoomModal');

    // 친구 목록 로드
    const token = getToken();
    const response = await fetch(`${BASE_URL}/api/friends/list`, {
        method: 'GET',
        headers: authHeaders(token)
    });

    const data = await response.json();

    if (data.success) {
        const checkboxList = document.getElementById('friendCheckboxList');
        checkboxList.innerHTML = data.friends.map(friend => `
            <div class="checkbox-item">
                <input type="checkbox" id="friend-${friend.userId}" value="${friend.userId}">
                <label for="friend-${friend.userId}">
                    ${friend.username} (${friend.email})
                </label>
            </div>
        `).join('');
    }
}

async function showParticipantsModal() {
    if (currentChatRoom) {
        await loadParticipants(currentChatRoom.id);
        openModal('participantsModal');
    }
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;

    if (diff < 60000) { // 1분 미만
        return '방금 전';
    } else if (diff < 3600000) { // 1시간 미만
        return `${Math.floor(diff / 60000)}분 전`;
    } else if (diff < 86400000) { // 24시간 미만
        return `${Math.floor(diff / 3600000)}시간 전`;
    } else {
        return date.toLocaleDateString('ko-KR');
    }
}

// ==================== 이벤트 리스너 ====================
document.addEventListener('DOMContentLoaded', function() {
    // 로그인 폼
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        await login(email, password);
    });

    // 회원가입 폼
    document.getElementById('registerForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const email = document.getElementById('registerEmail').value;
        const username = document.getElementById('registerUsername').value;
        const password = document.getElementById('registerPassword').value;
        const profileImage = document.getElementById('registerProfileImage').value || null;
        await register(email, username, password, profileImage);
    });

    // 친구 추가 폼
    document.getElementById('addFriendForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const friendEmail = document.getElementById('friendEmail').value;
        await sendFriendRequest(friendEmail);
    });

    // 채팅방 생성 폼
    document.getElementById('createChatRoomForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const chatroomName = document.getElementById('newChatRoomName').value;
        const checkboxes = document.querySelectorAll('#friendCheckboxList input[type="checkbox"]:checked');
        const participantIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

        // 현재 사용자도 포함
        participantIds.push(currentUser.id);

        if (participantIds.length < 2) {
            showToast('최소 1명 이상의 친구를 선택해주세요.', 'error');
            return;
        }

        await createChatRoom(chatroomName, participantIds);
    });

    // 메시지 전송 폼
    document.getElementById('messageForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const input = document.getElementById('messageInput');
        const content = input.value.trim();

        if (!content || !currentChatRoom) return;

        // WebSocket을 통해 전송
        sendMessageViaWebSocket(currentChatRoom.id, content);

        input.value = '';
    });

    // 메시지 수정 폼
    document.getElementById('editMessageForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const content = document.getElementById('editMessageContent').value;
        await updateMessage(currentEditingMessageId, content);
        closeModal('editMessageModal');
    });

    // 모달 외부 클릭 시 닫기
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeModal(modal.id);
            }
        });
    });

    // 자동 로그인 체크
    const token = getToken();
    if (token) {
        currentUser = getCurrentUser();
        if (currentUser) {
            initChatScreen();
            showScreen('chatScreen');
        }
    }
});
