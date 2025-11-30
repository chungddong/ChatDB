# ChatDB API 가이드 (React용)

## 목차
1. [기본 정보](#기본-정보)
2. [인증 (Auth)](#인증-auth)
3. [친구 관리 (Friendship)](#친구-관리-friendship)
4. [채팅방 관리 (ChatRoom)](#채팅방-관리-chatroom)
5. [메시지 관리 (Message)](#메시지-관리-message)
6. [WebSocket 실시간 통신](#websocket-실시간-통신)
7. [에러 처리](#에러-처리)

---

## 기본 정보

### Base URL
```
http://studyswh.kro.kr:41847/api
```

### WebSocket URL
```
ws://studyswh.kro.kr:41847/ws
```

### API Key (필수)
**모든 API 요청에는 API Key가 필요합니다.**

**API Key:** `9F7D521C7C3391115FB42542C7E5F`

API Key는 HTTP Header에 포함하여 전송해야 합니다:

```javascript
// Axios 예시
axios.defaults.headers.common['X-API-KEY'] = '9F7D521C7C3391115FB42542C7E5F';
```

### 인증 방식
- **API Key** (필수): 모든 요청에 포함
- **JWT Bearer Token**: 로그인 후 받은 토큰을 HTTP Header에 포함하여 요청

```javascript
// Axios 기본 설정 예시
axios.defaults.headers.common['X-API-KEY'] = '9F7D521C7C3391115FB42542C7E5F';
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### 공통 응답 형식
모든 API는 다음과 같은 공통 응답 형식을 따릅니다:

```typescript
{
  success: boolean;      // 성공 여부
  message: string;       // 응답 메시지
  // ... 추가 데이터
}
```

### HTTP 상태 코드
- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

---

## 인증 (Auth)

### 1. 회원가입

**Endpoint:** `POST /api/auth/register`

**인증 필요:** ❌

**Request Body:**
```typescript
{
  email: string;           // 이메일 (고유값)
  username: string;        // 사용자 이름 (고유값)
  password: string;        // 비밀번호
  profileImage?: string;   // 프로필 이미지 URL (선택)
}
```

**Response (201 Created):**
```typescript
{
  success: true,
  message: "회원가입이 완료되었습니다.",
  user: {
    id: number;
    email: string;
    username: string;
    profileImage: string | null;
    createdAt: string;  // ISO 8601 형식
  }
}
```

**Error Response (400 Bad Request):**
```typescript
{
  success: false,
  message: "이미 존재하는 이메일입니다." | "이미 존재하는 사용자명입니다."
}
```

**React 예시:**
```javascript
const handleRegister = async (userData) => {
  try {
    const response = await axios.post('/api/auth/register', {
      email: userData.email,
      username: userData.username,
      password: userData.password,
      profileImage: userData.profileImage || null
    }, {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F'
      }
    });

    if (response.data.success) {
      console.log('회원가입 성공:', response.data.user);
      // 로그인 페이지로 이동
    }
  } catch (error) {
    console.error('회원가입 실패:', error.response.data.message);
  }
};
```

---

### 2. 로그인

**Endpoint:** `POST /api/auth/login`

**인증 필요:** ❌

**Request Body:**
```typescript
{
  email: string;     // 이메일
  password: string;  // 비밀번호
}
```

**Response (200 OK):**
```typescript
{
  success: true,
  message: "로그인에 성공했습니다.",
  user: {
    id: number;
    email: string;
    username: string;
    profileImage: string | null;
    createdAt: string;
  },
  token: string;  // JWT 토큰 (인증에 사용)
}
```

**Error Response (401 Unauthorized):**
```typescript
{
  success: false,
  message: "존재하지 않는 이메일입니다." | "비밀번호가 일치하지 않습니다."
}
```

**React 예시:**
```javascript
const handleLogin = async (email, password) => {
  try {
    const response = await axios.post('/api/auth/login', {
      email,
      password
    }, {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F'
      }
    });

    if (response.data.success) {
      const { user, token } = response.data;

      // 토큰 저장 (localStorage 또는 Redux)
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));

      // Axios 기본 헤더에 토큰과 API Key 설정
      axios.defaults.headers.common['X-API-KEY'] = '9F7D521C7C3391115FB42542C7E5F';
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      console.log('로그인 성공:', user);
      // 메인 페이지로 이동
    }
  } catch (error) {
    console.error('로그인 실패:', error.response.data.message);
  }
};
```

---

## 친구 관리 (Friendship)

> 🔒 **모든 Friendship API는 인증이 필요합니다.**
>
> ⚠️ **중요 변경 사항**: 친구 추가 방식이 **즉시 친구 추가**로 변경되었습니다. 승인 절차 없이 친구 요청을 보내면 바로 친구가 됩니다.

### 1. 친구 추가 (즉시 친구로 추가됨)

**Endpoint:** `POST /api/friends/request`

**인증 필요:** ✅

**Request Body:**
```typescript
{
  friendEmail: string;  // 친구로 추가할 사용자의 이메일
}
```

**Response (201 Created):**
```typescript
{
  success: true,
  message: "친구를 추가했습니다.",
  friendshipId: number;  // 친구 관계 ID
}
```

**Error Response (400 Bad Request):**
```typescript
{
  success: false,
  message: "자기 자신을 친구로 추가할 수 없습니다." | "이미 친구 관계입니다." | "해당 이메일의 사용자를 찾을 수 없습니다."
}
```

**React 예시:**
```javascript
const addFriend = async (friendEmail) => {
  try {
    const response = await axios.post('/api/friends/request', {
      friendEmail
    }, {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.data.success) {
      alert('친구를 추가했습니다!');
      // 친구 목록 새로고침
      fetchFriendList();
    }
  } catch (error) {
    alert(error.response.data.message);
  }
};
```

---

### 2. 친구 목록 조회

**Endpoint:** `GET /api/friends/list`

**인증 필요:** ✅

**Response (200 OK):**
```typescript
{
  success: true,
  message: "친구 목록을 조회했습니다.",
  friends: Array<{
    id: number;              // 친구 관계 ID
    userId: number;          // 친구 사용자 ID
    username: string;
    email: string;
    profileImage: string | null;
    status: "ACCEPTED";      // 친구 목록이므로 항상 ACCEPTED
    createdAt: string;
    updatedAt: string;
  }>,
  count: number;
}
```

**React 예시:**
```javascript
const fetchFriendList = async () => {
  try {
    const response = await axios.get('/api/friends/list', {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.data.success) {
      setFriends(response.data.friends);
    }
  } catch (error) {
    console.error('친구 목록 조회 실패:', error);
  }
};
```

---

### 3. 친구 삭제

**Endpoint:** `DELETE /api/friends/{friendshipId}`

**인증 필요:** ✅

**Path Parameters:**
- `friendshipId`: 친구 관계 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "친구를 삭제했습니다."
}
```

---

## 채팅방 관리 (ChatRoom)

> 🔒 **모든 ChatRoom API는 인증이 필요합니다.**

### 1. 채팅방 생성

**Endpoint:** `POST /api/chatrooms`

**인증 필요:** ✅

**Request Body:**
```typescript
{
  chatroomName: string;        // 채팅방 이름
  participantIds: number[];    // 참가자 사용자 ID 목록 (최소 1명)
}
```

**Response (201 Created):**
```typescript
{
  success: true,
  message: "채팅방이 생성되었습니다.",
  chatRoom: {
    id: number;
    chatroomName: string;
    createdAt: string;
    updatedAt: string;
    participantCount: number;
  }
}
```

**React 예시:**
```javascript
const createChatRoom = async (roomName, participantIds) => {
  try {
    const response = await axios.post('/api/chatrooms', {
      chatroomName: roomName,
      participantIds: participantIds
    }, {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.data.success) {
      console.log('채팅방 생성 성공:', response.data.chatRoom);
      // 채팅방 목록으로 이동
      navigate(`/chatroom/${response.data.chatRoom.id}`);
    }
  } catch (error) {
    alert(error.response.data.message);
  }
};
```

---

### 2. 내가 참여한 채팅방 목록 조회

**Endpoint:** `GET /api/chatrooms`

**인증 필요:** ✅

**Response (200 OK):**
```typescript
{
  success: true,
  message: "채팅방 목록을 조회했습니다.",
  chatRooms: Array<{
    id: number;
    chatroomName: string;
    createdAt: string;
    updatedAt: string;
    participantCount: number;
  }>,
  count: number;
}
```

**React 예시:**
```javascript
const fetchMyChatRooms = async () => {
  try {
    const response = await axios.get('/api/chatrooms', {
      headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.data.success) {
      setChatRooms(response.data.chatRooms);
    }
  } catch (error) {
    console.error('채팅방 목록 조회 실패:', error);
  }
};

// 컴포넌트 예시
function ChatRoomList() {
  const [chatRooms, setChatRooms] = useState([]);

  useEffect(() => {
    fetchMyChatRooms();
  }, []);

  return (
    <div>
      {chatRooms.map(room => (
        <div key={room.id}>
          <h3>{room.chatroomName}</h3>
          <p>참가자: {room.participantCount}명</p>
        </div>
      ))}
    </div>
  );
}
```

---

### 3. 특정 채팅방 조회

**Endpoint:** `GET /api/chatrooms/{chatroomId}`

**인증 필요:** ✅

**Path Parameters:**
- `chatroomId`: 채팅방 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "채팅방 정보를 조회했습니다.",
  chatRoom: {
    id: number;
    chatroomName: string;
    createdAt: string;
    updatedAt: string;
    participantCount: number;
  }
}
```

**Error Response (404 Not Found):**
```typescript
{
  success: false,
  message: "존재하지 않는 채팅방입니다."
}
```

---

### 4. 채팅방 삭제

**Endpoint:** `DELETE /api/chatrooms/{chatroomId}`

**인증 필요:** ✅

**Path Parameters:**
- `chatroomId`: 채팅방 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "채팅방이 삭제되었습니다."
}
```

**주의:** 채팅방 삭제 시 참가자 및 메시지도 함께 삭제됩니다.

---

### 5. 채팅방 참가자 조회

**Endpoint:** `GET /api/chatrooms/{chatroomId}/participants`

**인증 필요:** ✅

**Path Parameters:**
- `chatroomId`: 채팅방 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "참가자 목록을 조회했습니다.",
  participants: Array<{
    participantId: number;
    userId: number;
    username: string;
    email: string;
    profileImage: string | null;
    joinedAt: string;        // 참가 시간
    lastReadAt: string | null;  // 마지막 읽은 시간
  }>,
  count: number;
}
```

**React 예시:**
```javascript
const fetchParticipants = async (chatroomId) => {
  try {
    const response = await axios.get(
      `/api/chatrooms/${chatroomId}/participants`,
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      setParticipants(response.data.participants);
    }
  } catch (error) {
    console.error('참가자 조회 실패:', error);
  }
};
```

---

## 메시지 관리 (Message)

> 🔒 **모든 Message API는 인증이 필요합니다.**

### 1. 메시지 목록 조회 (페이징)

**Endpoint:** `GET /api/messages/chatrooms/{chatroomId}`

**인증 필요:** ✅

**Path Parameters:**
- `chatroomId`: 채팅방 ID (number)

**Query Parameters:**
```typescript
{
  page?: number;   // 페이지 번호 (기본값: 0)
  size?: number;   // 페이지 크기 (기본값: 50)
}
```

**Response (200 OK):**
```typescript
{
  success: true,
  message: "메시지 목록을 조회했습니다.",
  messages: Array<{
    messageId: number;
    chatroomId: number;
    senderId: number;
    senderName: string;
    senderProfileImage: string | null;
    content: string;
    messageType: "TEXT" | "IMAGE" | "FILE";
    sendAt: string;
  }>,
  currentPage: number;
  totalPages: number;
  totalMessages: number;
}
```

**React 예시:**
```javascript
const fetchMessages = async (chatroomId, page = 0, size = 50) => {
  try {
    const response = await axios.get(
      `/api/messages/chatrooms/${chatroomId}?page=${page}&size=${size}`,
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      setMessages(response.data.messages);
      setTotalPages(response.data.totalPages);
    }
  } catch (error) {
    console.error('메시지 조회 실패:', error);
  }
};

// 무한 스크롤 예시
function MessageList({ chatroomId }) {
  const [messages, setMessages] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const loadMoreMessages = async () => {
    const response = await axios.get(
      `/api/messages/chatrooms/${chatroomId}?page=${page}&size=50`,
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      setMessages(prev => [...prev, ...response.data.messages]);
      setPage(prev => prev + 1);
      setHasMore(response.data.currentPage < response.data.totalPages - 1);
    }
  };

  useEffect(() => {
    loadMoreMessages();
  }, []);

  return (
    <InfiniteScroll
      dataLength={messages.length}
      next={loadMoreMessages}
      hasMore={hasMore}
    >
      {messages.map(msg => (
        <MessageItem key={msg.messageId} message={msg} />
      ))}
    </InfiniteScroll>
  );
}
```

---

### 2. 모든 메시지 조회 (페이징 없음)

**Endpoint:** `GET /api/messages/chatrooms/{chatroomId}/all`

**인증 필요:** ✅

**Response (200 OK):**
```typescript
{
  success: true,
  message: "모든 메시지를 조회했습니다.",
  messages: Array<{
    messageId: number;
    chatroomId: number;
    senderId: number;
    senderName: string;
    senderProfileImage: string | null;
    content: string;
    messageType: "TEXT" | "IMAGE" | "FILE";
    sendAt: string;
  }>,
  count: number;
}
```

---

### 3. 안 읽은 메시지 개수 조회

**Endpoint:** `GET /api/messages/chatrooms/{chatroomId}/unread-count`

**인증 필요:** ✅

**Response (200 OK):**
```typescript
{
  success: true,
  message: "안 읽은 메시지 개수를 조회했습니다.",
  chatroomId: number;
  unreadCount: number;
}
```

**React 예시:**
```javascript
const fetchUnreadCount = async (chatroomId) => {
  try {
    const response = await axios.get(
      `/api/messages/chatrooms/${chatroomId}/unread-count`,
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      setUnreadCount(response.data.unreadCount);
    }
  } catch (error) {
    console.error('안 읽은 메시지 개수 조회 실패:', error);
  }
};

// 뱃지 표시 예시
function ChatRoomItem({ room }) {
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    fetchUnreadCount(room.id);
  }, [room.id]);

  return (
    <div className="chatroom-item">
      <h3>{room.chatroomName}</h3>
      {unreadCount > 0 && (
        <span className="badge">{unreadCount}</span>
      )}
    </div>
  );
}
```

---

### 4. 메시지 읽음 처리

**Endpoint:** `POST /api/messages/chatrooms/{chatroomId}/read`

**인증 필요:** ✅

**Response (200 OK):**
```typescript
{
  success: true,
  message: "메시지를 읽음 처리했습니다."
}
```

**React 예시:**
```javascript
const markMessagesAsRead = async (chatroomId) => {
  try {
    await axios.post(
      `/api/messages/chatrooms/${chatroomId}/read`,
      {},
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
  } catch (error) {
    console.error('읽음 처리 실패:', error);
  }
};

// 채팅방 입장 시 자동 읽음 처리
useEffect(() => {
  if (chatroomId) {
    markMessagesAsRead(chatroomId);
  }
}, [chatroomId]);
```

---

### 5. 메시지 전송 (REST API)

**Endpoint:** `POST /api/messages/chatrooms/{chatroomId}`

**인증 필요:** ✅

**Request Body:**
```typescript
{
  content: string;                           // 메시지 내용
  messageType?: "TEXT" | "IMAGE" | "FILE";  // 메시지 타입 (기본값: TEXT)
}
```

**Response (201 Created):**
```typescript
{
  success: true,
  message: "메시지를 전송했습니다.",
  data: {
    messageId: number;
    chatroomId: number;
    senderId: number;
    senderName: string;
    senderProfileImage: string | null;
    content: string;
    messageType: "TEXT" | "IMAGE" | "FILE";
    sendAt: string;
  }
}
```

**React 예시:**
```javascript
const sendMessage = async (chatroomId, content, messageType = 'TEXT') => {
  try {
    const response = await axios.post(
      `/api/messages/chatrooms/${chatroomId}`,
      {
        content,
        messageType
      },
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      console.log('메시지 전송 성공:', response.data.data);
      // 메시지 목록 업데이트
      setMessages(prev => [...prev, response.data.data]);
    }
  } catch (error) {
    alert('메시지 전송 실패:', error.response.data.message);
  }
};

// 메시지 전송 폼 예시
function MessageInput({ chatroomId }) {
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    await sendMessage(chatroomId, message);
    setMessage('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="메시지를 입력하세요"
      />
      <button type="submit">전송</button>
    </form>
  );
}
```

---

### 6. 메시지 수정

**Endpoint:** `PUT /api/messages/{messageId}`

**인증 필요:** ✅

**Path Parameters:**
- `messageId`: 메시지 ID (number)

**Request Body:**
```typescript
{
  content: string;  // 수정할 메시지 내용
}
```

**Response (200 OK):**
```typescript
{
  success: true,
  message: "메시지를 수정했습니다.",
  data: {
    messageId: number;
    chatroomId: number;
    senderId: number;
    senderName: string;
    senderProfileImage: string | null;
    content: string;  // 수정된 내용
    messageType: "TEXT" | "IMAGE" | "FILE";
    sendAt: string;
  }
}
```

**주의:** 자신이 작성한 메시지만 수정 가능합니다.

---

### 7. 메시지 삭제

**Endpoint:** `DELETE /api/messages/{messageId}`

**인증 필요:** ✅

**Path Parameters:**
- `messageId`: 메시지 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "메시지를 삭제했습니다."
}
```

**주의:** 자신이 작성한 메시지만 삭제 가능합니다.

**React 예시:**
```javascript
const deleteMessage = async (messageId) => {
  if (!window.confirm('메시지를 삭제하시겠습니까?')) return;

  try {
    const response = await axios.delete(
      `/api/messages/${messageId}`,
      {
        headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );

    if (response.data.success) {
      // 메시지 목록에서 제거
      setMessages(prev => prev.filter(msg => msg.messageId !== messageId));
    }
  } catch (error) {
    alert(error.response.data.message);
  }
};
```

---

### 8. 특정 메시지 조회

**Endpoint:** `GET /api/messages/{messageId}`

**인증 필요:** ✅

**Path Parameters:**
- `messageId`: 메시지 ID (number)

**Response (200 OK):**
```typescript
{
  success: true,
  message: "메시지를 조회했습니다.",
  data: {
    messageId: number;
    chatroomId: number;
    senderId: number;
    senderName: string;
    senderProfileImage: string | null;
    content: string;
    messageType: "TEXT" | "IMAGE" | "FILE";
    sendAt: string;
  }
}
```

---

## WebSocket 실시간 통신

### WebSocket 연결 설정

**WebSocket Endpoint:**
```
ws://studyswh.kro.kr:41847/ws
```

**STOMP 프로토콜 사용**

### React + SockJS + STOMP 설정 예시

**1. 패키지 설치:**
```bash
npm install sockjs-client @stomp/stompjs
```

**2. WebSocket 연결 및 구독:**
```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// WebSocket 클라이언트 생성
const createWebSocketClient = (chatroomId, onMessageReceived, onReadNotification) => {
  const socket = new SockJS('http://studyswh.kro.kr:41847/ws');
  const stompClient = new Client({
    webSocketFactory: () => socket,

    connectHeaders: {
      // API Key와 JWT 토큰을 헤더에 포함
      'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
      Authorization: `Bearer ${localStorage.getItem('token')}`
    },

    onConnect: () => {
      console.log('WebSocket 연결 성공');

      // 채팅방 메시지 구독
      stompClient.subscribe(`/topic/chatroom/${chatroomId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        onMessageReceived(receivedMessage);
      });

      // 읽음 알림 구독
      stompClient.subscribe(`/topic/chatroom/${chatroomId}/read`, (notification) => {
        const readNotif = JSON.parse(notification.body);
        onReadNotification(readNotif);
      });
    },

    onStompError: (frame) => {
      console.error('STOMP error:', frame);
    }
  });

  stompClient.activate();
  return stompClient;
};

// 사용 예시
function ChatRoom({ chatroomId }) {
  const [messages, setMessages] = useState([]);
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    const client = createWebSocketClient(
      chatroomId,
      // 메시지 수신 콜백
      (newMessage) => {
        setMessages(prev => [...prev, newMessage]);
      },
      // 읽음 알림 콜백
      (readNotif) => {
        console.log('읽음 처리:', readNotif);
        // UI 업데이트 (읽음 표시 등)
      }
    );

    setStompClient(client);

    // 컴포넌트 언마운트 시 연결 해제
    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, [chatroomId]);

  return (
    <div>
      {/* 채팅 UI */}
    </div>
  );
}
```

---

### 1. 메시지 전송 (WebSocket)

**Client → Server:** `/app/chat/{chatroomId}/send`

**Request Body:**
```typescript
{
  content: string;
  messageType?: "TEXT" | "IMAGE" | "FILE";
}
```

**Server → Client:** `/topic/chatroom/{chatroomId}`

**Response:**
```typescript
{
  messageId: number;
  chatroomId: number;
  senderId: number;
  senderName: string;
  senderProfileImage: string | null;
  content: string;
  messageType: "TEXT" | "IMAGE" | "FILE";
  sendAt: string;
}
```

**React 예시:**
```javascript
const sendMessageViaWebSocket = (stompClient, chatroomId, content, messageType = 'TEXT') => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: `/app/chat/${chatroomId}/send`,
      body: JSON.stringify({
        content,
        messageType
      })
    });
  } else {
    console.error('WebSocket이 연결되지 않았습니다.');
  }
};

// 사용 예시
function MessageInput({ stompClient, chatroomId }) {
  const [message, setMessage] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    sendMessageViaWebSocket(stompClient, chatroomId, message);
    setMessage('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />
      <button type="submit">전송</button>
    </form>
  );
}
```

---

### 2. 읽음 상태 알림 (WebSocket)

**Client → Server:** `/app/chat/{chatroomId}/read`

**Server → Client:** `/topic/chatroom/{chatroomId}/read`

**Response:**
```typescript
{
  userId: number;       // 읽음 처리한 사용자 ID
  chatroomId: number;   // 채팅방 ID
}
```

**React 예시:**
```javascript
const sendReadNotification = (stompClient, chatroomId) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: `/app/chat/${chatroomId}/read`,
      body: JSON.stringify({})
    });
  }
};

// 채팅방 입장 시 또는 메시지 읽을 때
useEffect(() => {
  if (stompClient && chatroomId) {
    sendReadNotification(stompClient, chatroomId);
  }
}, [stompClient, chatroomId]);
```

---

### 완전한 WebSocket 통합 예시

```javascript
import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';

function ChatRoom({ chatroomId, currentUserId }) {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);

  // 초기 메시지 로드
  useEffect(() => {
    const loadInitialMessages = async () => {
      try {
        const response = await axios.get(
          `/api/messages/chatrooms/${chatroomId}/all`,
          {
            headers: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
              'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        );

        if (response.data.success) {
          setMessages(response.data.messages);
        }
      } catch (error) {
        console.error('메시지 로드 실패:', error);
      }
    };

    loadInitialMessages();
  }, [chatroomId]);

  // WebSocket 연결
  useEffect(() => {
    const socket = new SockJS('http://studyswh.kro.kr:41847/ws');
    const client = new Client({
      webSocketFactory: () => socket,

      connectHeaders: {
        'X-API-KEY': '9F7D521C7C3391115FB42542C7E5F',
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },

      onConnect: () => {
        console.log('WebSocket 연결됨');

        // 메시지 구독
        client.subscribe(`/topic/chatroom/${chatroomId}`, (message) => {
          const newMessage = JSON.parse(message.body);
          setMessages(prev => [...prev, newMessage]);

          // 자동 스크롤
          scrollToBottom();
        });

        // 읽음 알림 구독
        client.subscribe(`/topic/chatroom/${chatroomId}/read`, (notification) => {
          const readNotif = JSON.parse(notification.body);
          console.log('읽음 처리:', readNotif);
          // 읽음 표시 업데이트 로직 추가 가능
        });

        // 입장 시 읽음 처리
        client.publish({
          destination: `/app/chat/${chatroomId}/read`,
          body: JSON.stringify({})
        });
      },

      onStompError: (frame) => {
        console.error('STOMP 오류:', frame);
      }
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, [chatroomId]);

  // 메시지 전송
  const handleSendMessage = (e) => {
    e.preventDefault();

    if (!inputMessage.trim() || !stompClient || !stompClient.connected) {
      return;
    }

    stompClient.publish({
      destination: `/app/chat/${chatroomId}/send`,
      body: JSON.stringify({
        content: inputMessage,
        messageType: 'TEXT'
      })
    });

    setInputMessage('');
  };

  // 자동 스크롤
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="chatroom">
      <div className="messages-container">
        {messages.map((msg) => (
          <div
            key={msg.messageId}
            className={msg.senderId === currentUserId ? 'message-mine' : 'message-other'}
          >
            <div className="message-sender">
              {msg.senderProfileImage && (
                <img src={msg.senderProfileImage} alt={msg.senderName} />
              )}
              <span>{msg.senderName}</span>
            </div>
            <div className="message-content">{msg.content}</div>
            <div className="message-time">
              {new Date(msg.sendAt).toLocaleTimeString()}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSendMessage} className="message-input-form">
        <input
          type="text"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          placeholder="메시지를 입력하세요..."
        />
        <button type="submit">전송</button>
      </form>
    </div>
  );
}

export default ChatRoom;
```

---

## 에러 처리

### 공통 에러 응답 형식

모든 API 에러는 다음 형식을 따릅니다:

```typescript
{
  success: false,
  message: string;  // 에러 메시지
}
```

### 에러 상태 코드별 처리

**React Axios 인터셉터 예시:**

```javascript
import axios from 'axios';

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://studyswh.kro.kr:41847/api'
});

// 요청 인터셉터 - API Key와 토큰 자동 추가
api.interceptors.request.use(
  (config) => {
    // API Key는 항상 포함
    config.headers['X-API-KEY'] = '9F7D521C7C3391115FB42542C7E5F';

    // JWT 토큰이 있으면 추가
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 400:
          // Bad Request
          console.error('잘못된 요청:', data.message);
          break;

        case 401:
          // Unauthorized - 토큰 만료 또는 인증 실패
          console.error('인증 실패:', data.message);
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
          break;

        case 404:
          // Not Found
          console.error('리소스를 찾을 수 없음:', data.message);
          break;

        case 500:
          // Internal Server Error
          console.error('서버 오류:', data.message);
          alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
          break;

        default:
          console.error('알 수 없는 오류:', data.message);
      }
    } else if (error.request) {
      // 요청은 보냈으나 응답을 받지 못함
      console.error('네트워크 오류: 서버에 연결할 수 없습니다.');
      alert('네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.');
    } else {
      // 요청 설정 중 오류 발생
      console.error('요청 오류:', error.message);
    }

    return Promise.reject(error);
  }
);

export default api;
```

**사용 예시:**

```javascript
import api from './api';

// 로그인
const login = async (email, password) => {
  try {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  } catch (error) {
    // 에러는 인터셉터에서 처리됨
    throw error;
  }
};

// 친구 목록 조회
const getFriends = async () => {
  try {
    const response = await api.get('/friends/list');
    return response.data.friends;
  } catch (error) {
    throw error;
  }
};
```

---

## 추가 팁

### 1. 환경 변수 사용

`.env` 파일:
```env
REACT_APP_API_BASE_URL=http://studyswh.kro.kr:41847/api
REACT_APP_WS_URL=http://studyswh.kro.kr:41847/ws
REACT_APP_API_KEY=9F7D521C7C3391115FB42542C7E5F
```

사용:
```javascript
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
const WS_URL = process.env.REACT_APP_WS_URL;
const API_KEY = process.env.REACT_APP_API_KEY;

// Axios 인스턴스 생성 시
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'X-API-KEY': API_KEY
  }
});
```

### 2. TypeScript 타입 정의

```typescript
// types.ts
export interface User {
  id: number;
  email: string;
  username: string;
  profileImage: string | null;
  createdAt: string;
}

export interface Message {
  messageId: number;
  chatroomId: number;
  senderId: number;
  senderName: string;
  senderProfileImage: string | null;
  content: string;
  messageType: 'TEXT' | 'IMAGE' | 'FILE';
  sendAt: string;
}

export interface ChatRoom {
  id: number;
  chatroomName: string;
  createdAt: string;
  updatedAt: string;
  participantCount: number;
}

export interface Friendship {
  id: number;
  userId: number;
  username: string;
  email: string;
  profileImage: string | null;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  [key: string]: any;
}
```

### 3. React Query 사용 예시

```javascript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from './api';

// 친구 목록 조회
export const useFriends = () => {
  return useQuery({
    queryKey: ['friends'],
    queryFn: async () => {
      const response = await api.get('/friends/list');
      return response.data.friends;
    }
  });
};

// 친구 요청 보내기
export const useSendFriendRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (friendEmail: string) => {
      const response = await api.post('/friends/request', { friendEmail });
      return response.data;
    },
    onSuccess: () => {
      // 친구 요청 목록 새로고침
      queryClient.invalidateQueries({ queryKey: ['friendRequests'] });
    }
  });
};

// 사용
function FriendList() {
  const { data: friends, isLoading } = useFriends();
  const sendRequest = useSendFriendRequest();

  if (isLoading) return <div>로딩 중...</div>;

  return (
    <div>
      {friends?.map(friend => (
        <div key={friend.id}>{friend.username}</div>
      ))}
    </div>
  );
}
```

---


**문서 버전:** 1.0.0
**최종 업데이트:** 2025-11-30
