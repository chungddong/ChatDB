# ChatDB API 연동 가이드 (Electron App)

이 문서는 Electron 앱에서 ChatDB API를 사용하는 방법을 설명합니다.

## 📋 목차
- [기본 설정](#기본-설정)
- [인증 (Auth)](#인증-auth)
- [친구 관리 (Friendship)](#친구-관리-friendship)
- [채팅방 관리 (ChatRoom)](#채팅방-관리-chatroom)

---

## 기본 설정

### API 기본 정보
- **Base URL**: `http://localhost:7070`
- **API Key**: `your-secure-api-key-change-this-in-production`
- **Content-Type**: `application/json`

### 공통 헤더 설정
모든 API 요청에는 다음 헤더가 필요합니다:

```javascript
const BASE_URL = 'http://localhost:7070';
const API_KEY = 'your-secure-api-key-change-this-in-production';

// 기본 헤더 (API 키만)
const baseHeaders = {
  'Content-Type': 'application/json',
  'X-API-Key': API_KEY
};

// 인증이 필요한 API용 헤더
const authHeaders = (token) => ({
  'Content-Type': 'application/json',
  'X-API-Key': API_KEY,
  'Authorization': `Bearer ${token}`
});
```

---

## 인증 (Auth)

### 1. 회원가입

**요청**
```javascript
async function register(email, username, password, profileImage = null) {
  const response = await fetch(`${BASE_URL}/api/auth/register`, {
    method: 'POST',
    headers: baseHeaders,
    body: JSON.stringify({
      email: email,
      username: username,
      password: password,
      profileImage: profileImage
    })
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await register('user@example.com', 'username123', 'password123');
```

**응답 예시 (성공)**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "username123",
    "profileImage": null,
    "createdAt": "2025-10-30T12:00:00"
  }
}
```

**응답 예시 (실패)**
```json
{
  "success": false,
  "message": "이미 존재하는 이메일입니다."
}
```

---

### 2. 로그인

**요청**
```javascript
async function login(email, password) {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: baseHeaders,
    body: JSON.stringify({
      email: email,
      password: password
    })
  });

  const data = await response.json();

  // 로그인 성공 시 토큰 저장
  if (data.success && data.token) {
    localStorage.setItem('jwt_token', data.token);
    localStorage.setItem('user', JSON.stringify(data.user));
  }

  return data;
}
```

**요청 예시**
```javascript
const result = await login('user@example.com', 'password123');
```

**응답 예시 (성공)**
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "username123",
    "profileImage": null,
    "createdAt": "2025-10-30T12:00:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 친구 관리 (Friendship)

모든 친구 관리 API는 JWT 토큰이 필요합니다.

### 1. 친구 요청 보내기

**요청**
```javascript
async function sendFriendRequest(friendEmail) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/request`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({
      friendEmail: friendEmail
    })
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await sendFriendRequest('friend@example.com');
```

**응답 예시 (성공)**
```json
{
  "success": true,
  "message": "친구 요청을 보냈습니다.",
  "requestId": 1
}
```

**응답 예시 (실패)**
```json
{
  "success": false,
  "message": "해당 이메일의 사용자를 찾을 수 없습니다."
}
```

---

### 2. 받은 친구 요청 목록 조회

**요청**
```javascript
async function getReceivedFriendRequests() {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/requests/received`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "받은 친구 요청 목록을 조회했습니다.",
  "requests": [
    {
      "friendshipId": 1,
      "userId": 2,
      "username": "friend1",
      "email": "friend1@example.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "2025-10-30T12:00:00"
    }
  ],
  "count": 1
}
```

---

### 3. 보낸 친구 요청 목록 조회

**요청**
```javascript
async function getSentFriendRequests() {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/requests/sent`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "보낸 친구 요청 목록을 조회했습니다.",
  "requests": [
    {
      "friendshipId": 2,
      "userId": 3,
      "username": "friend2",
      "email": "friend2@example.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "2025-10-30T13:00:00"
    }
  ],
  "count": 1
}
```

---

### 4. 친구 요청 수락

**요청**
```javascript
async function acceptFriendRequest(requestId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/accept`, {
    method: 'POST',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await acceptFriendRequest(1);
```

**응답 예시**
```json
{
  "success": true,
  "message": "친구 요청을 수락했습니다.",
  "friendshipId": 1
}
```

---

### 5. 친구 요청 거절

**요청**
```javascript
async function rejectFriendRequest(requestId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/reject`, {
    method: 'POST',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "친구 요청을 거절했습니다."
}
```

---

### 6. 친구 요청 취소

**요청**
```javascript
async function cancelFriendRequest(requestId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/cancel`, {
    method: 'DELETE',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "친구 요청을 취소했습니다."
}
```

---

### 7. 친구 목록 조회

**요청**
```javascript
async function getFriendList() {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/list`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "친구 목록을 조회했습니다.",
  "friends": [
    {
      "friendshipId": 1,
      "userId": 2,
      "username": "friend1",
      "email": "friend1@example.com",
      "profileImage": null,
      "status": "ACCEPTED",
      "createdAt": "2025-10-30T12:00:00"
    },
    {
      "friendshipId": 3,
      "userId": 4,
      "username": "friend2",
      "email": "friend2@example.com",
      "profileImage": "https://example.com/profile.jpg",
      "status": "ACCEPTED",
      "createdAt": "2025-10-30T14:00:00"
    }
  ],
  "count": 2
}
```

---

### 8. 친구 삭제

**요청**
```javascript
async function deleteFriend(friendshipId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/friends/${friendshipId}`, {
    method: 'DELETE',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "친구를 삭제했습니다."
}
```

---

## 채팅방 관리 (ChatRoom)

모든 채팅방 관리 API는 JWT 토큰이 필요합니다.

### 1. 채팅방 생성

**요청**
```javascript
async function createChatRoom(chatroomName, participantIds) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/chatrooms`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({
      chatroomName: chatroomName,
      participantIds: participantIds
    })
  });

  return await response.json();
}
```

**요청 예시**
```javascript
// 현재 사용자와 친구 2명으로 채팅방 생성
const currentUserId = 1;
const result = await createChatRoom('즐거운 채팅방', [currentUserId, 2, 3]);
```

**응답 예시**
```json
{
  "success": true,
  "message": "채팅방이 생성되었습니다.",
  "chatRoom": {
    "id": 1,
    "chatroomName": "즐거운 채팅방",
    "createdAt": "2025-10-30T15:00:00",
    "updatedAt": "2025-10-30T15:00:00",
    "participantCount": 3
  }
}
```

---

### 2. 모든 채팅방 조회

**요청**
```javascript
async function getAllChatRooms() {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/chatrooms`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**응답 예시**
```json
{
  "success": true,
  "message": "채팅방 목록을 조회했습니다.",
  "chatRooms": [
    {
      "id": 1,
      "chatroomName": "즐거운 채팅방",
      "createdAt": "2025-10-30T15:00:00",
      "updatedAt": "2025-10-30T15:00:00",
      "participantCount": 3
    },
    {
      "id": 2,
      "chatroomName": "프로젝트 회의",
      "createdAt": "2025-10-30T16:00:00",
      "updatedAt": "2025-10-30T16:00:00",
      "participantCount": 5
    }
  ],
  "count": 2
}
```

---

### 3. 특정 채팅방 조회

**요청**
```javascript
async function getChatRoomById(chatroomId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await getChatRoomById(1);
```

**응답 예시**
```json
{
  "success": true,
  "message": "채팅방 정보를 조회했습니다.",
  "chatRoom": {
    "id": 1,
    "chatroomName": "즐거운 채팅방",
    "createdAt": "2025-10-30T15:00:00",
    "updatedAt": "2025-10-30T15:00:00",
    "participantCount": 3
  }
}
```

---

### 4. 채팅방 참가자 조회

**요청**
```javascript
async function getChatRoomParticipants(chatroomId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}/participants`, {
    method: 'GET',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await getChatRoomParticipants(1);
```

**응답 예시**
```json
{
  "success": true,
  "message": "참가자 목록을 조회했습니다.",
  "participants": [
    {
      "participantId": 1,
      "userId": 1,
      "username": "user1",
      "email": "user1@example.com",
      "profileImage": null,
      "joinedAt": "2025-10-30T15:00:00",
      "lastReadAt": "2025-10-30T15:30:00"
    },
    {
      "participantId": 2,
      "userId": 2,
      "username": "user2",
      "email": "user2@example.com",
      "profileImage": "https://example.com/profile2.jpg",
      "joinedAt": "2025-10-30T15:00:00",
      "lastReadAt": "2025-10-30T15:25:00"
    }
  ],
  "count": 2
}
```

---

### 5. 채팅방 삭제

**요청**
```javascript
async function deleteChatRoom(chatroomId) {
  const token = localStorage.getItem('jwt_token');

  const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
    method: 'DELETE',
    headers: authHeaders(token)
  });

  return await response.json();
}
```

**요청 예시**
```javascript
const result = await deleteChatRoom(1);
```

**응답 예시**
```json
{
  "success": true,
  "message": "채팅방이 삭제되었습니다."
}
```

---

## 전체 통합 예제

### Electron Main Process (main.js)

```javascript
const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  mainWindow.loadFile('index.html');
}

app.whenReady().then(createWindow);

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
```

---

### Renderer Process (api.js)

```javascript
// API 설정
const BASE_URL = 'http://localhost:7070';
const API_KEY = 'your-secure-api-key-change-this-in-production';

const baseHeaders = {
  'Content-Type': 'application/json',
  'X-API-Key': API_KEY
};

const authHeaders = (token) => ({
  'Content-Type': 'application/json',
  'X-API-Key': API_KEY,
  'Authorization': `Bearer ${token}`
});

// API 클래스
class ChatDBAPI {
  // 인증
  static async register(email, username, password, profileImage = null) {
    const response = await fetch(`${BASE_URL}/api/auth/register`, {
      method: 'POST',
      headers: baseHeaders,
      body: JSON.stringify({ email, username, password, profileImage })
    });
    return await response.json();
  }

  static async login(email, password) {
    const response = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: baseHeaders,
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();

    if (data.success && data.token) {
      localStorage.setItem('jwt_token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
    }

    return data;
  }

  static logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
  }

  static getToken() {
    return localStorage.getItem('jwt_token');
  }

  static getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  // 친구 관리
  static async sendFriendRequest(friendEmail) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/request`, {
      method: 'POST',
      headers: authHeaders(token),
      body: JSON.stringify({ friendEmail })
    });
    return await response.json();
  }

  static async getReceivedFriendRequests() {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/requests/received`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async getSentFriendRequests() {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/requests/sent`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async acceptFriendRequest(requestId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/accept`, {
      method: 'POST',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async rejectFriendRequest(requestId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/reject`, {
      method: 'POST',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async cancelFriendRequest(requestId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/requests/${requestId}/cancel`, {
      method: 'DELETE',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async getFriendList() {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/list`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async deleteFriend(friendshipId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/friends/${friendshipId}`, {
      method: 'DELETE',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  // 채팅방 관리
  static async createChatRoom(chatroomName, participantIds) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/chatrooms`, {
      method: 'POST',
      headers: authHeaders(token),
      body: JSON.stringify({ chatroomName, participantIds })
    });
    return await response.json();
  }

  static async getAllChatRooms() {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/chatrooms`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async getChatRoomById(chatroomId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async getChatRoomParticipants(chatroomId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}/participants`, {
      method: 'GET',
      headers: authHeaders(token)
    });
    return await response.json();
  }

  static async deleteChatRoom(chatroomId) {
    const token = this.getToken();
    const response = await fetch(`${BASE_URL}/api/chatrooms/${chatroomId}`, {
      method: 'DELETE',
      headers: authHeaders(token)
    });
    return await response.json();
  }
}

// 사용 예제
async function example() {
  try {
    // 1. 로그인
    const loginResult = await ChatDBAPI.login('user@example.com', 'password123');
    console.log('로그인:', loginResult);

    // 2. 친구 목록 조회
    const friendList = await ChatDBAPI.getFriendList();
    console.log('친구 목록:', friendList);

    // 3. 채팅방 생성
    const currentUser = ChatDBAPI.getCurrentUser();
    const chatRoom = await ChatDBAPI.createChatRoom(
      '새로운 채팅방',
      [currentUser.id, 2, 3] // 현재 사용자 + 친구 2명
    );
    console.log('채팅방 생성:', chatRoom);

    // 4. 채팅방 목록 조회
    const chatRooms = await ChatDBAPI.getAllChatRooms();
    console.log('채팅방 목록:', chatRooms);

  } catch (error) {
    console.error('API 호출 오류:', error);
  }
}
```

---

## 에러 처리

모든 API 응답에는 `success` 필드가 포함됩니다:
- `success: true` - 요청 성공
- `success: false` - 요청 실패 (message에 오류 내용)

```javascript
async function handleAPICall(apiFunction) {
  try {
    const result = await apiFunction();

    if (result.success) {
      console.log('성공:', result.message);
      return result;
    } else {
      console.error('실패:', result.message);
      // 사용자에게 에러 메시지 표시
      alert(result.message);
      return null;
    }
  } catch (error) {
    console.error('네트워크 오류:', error);
    alert('서버와 연결할 수 없습니다.');
    return null;
  }
}

// 사용 예제
await handleAPICall(() => ChatDBAPI.login('user@example.com', 'password'));
```

---

## 주의사항

1. **API 키 보안**: 프로덕션 환경에서는 API 키를 환경 변수로 관리하세요.
2. **토큰 만료**: JWT 토큰은 24시간 후 만료됩니다. 만료 시 재로그인이 필요합니다.
3. **CORS**: 서버는 모든 origin을 허용하지만, API 키가 필요합니다.
4. **에러 처리**: 모든 API 호출에 try-catch를 사용하세요.
5. **토큰 저장**: localStorage 대신 더 안전한 저장소 사용을 권장합니다.
