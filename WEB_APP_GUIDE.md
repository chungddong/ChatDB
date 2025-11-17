# ChatDB 웹 애플리케이션 사용 가이드

## 📋 개요

모든 API 기능을 테스트할 수 있는 완벽한 채팅 웹 애플리케이션입니다.

## 🚀 실행 방법

### 1. 서버 실행
```bash
mvn spring-boot:run
```

### 2. 웹 애플리케이션 접속
브라우저에서 다음 URL로 접속하세요:
```
http://localhost:7070
```

## ✨ 구현된 기능

### 인증 (Authentication)
- ✅ 회원가입
- ✅ 로그인
- ✅ 로그아웃
- ✅ 자동 로그인 (토큰 기반)

### 친구 관리 (Friendship)
- ✅ 친구 요청 보내기
- ✅ 받은 친구 요청 조회
- ✅ 보낸 친구 요청 조회
- ✅ 친구 요청 수락
- ✅ 친구 요청 거절
- ✅ 친구 요청 취소
- ✅ 친구 목록 조회
- ✅ 친구 삭제

### 채팅방 관리 (ChatRoom)
- ✅ 채팅방 생성 (친구 선택)
- ✅ 채팅방 목록 조회
- ✅ 특정 채팅방 조회
- ✅ 채팅방 참가자 목록 조회
- ✅ 채팅방 삭제 (나가기)

### 메시지 관리 (Message)
- ✅ 메시지 전송 (REST API)
- ✅ 실시간 메시지 전송 (WebSocket)
- ✅ 메시지 목록 조회
- ✅ 메시지 수정
- ✅ 메시지 삭제
- ✅ 안 읽은 메시지 개수 조회
- ✅ 메시지 읽음 처리

### 실시간 통신 (WebSocket)
- ✅ SockJS 연결
- ✅ STOMP 프로토콜
- ✅ 채팅방 구독
- ✅ 실시간 메시지 수신

## 📝 사용 방법

### 1단계: 회원가입 및 로그인

1. 웹 페이지에 접속하면 로그인 화면이 나타납니다.
2. **"회원가입"** 탭을 클릭합니다.
3. 이메일, 사용자명, 비밀번호를 입력합니다.
4. (선택) 프로필 이미지 URL을 입력합니다.
5. **"회원가입"** 버튼을 클릭합니다.
6. 회원가입 성공 후, **"로그인"** 탭에서 로그인합니다.

### 2단계: 친구 추가

1. 로그인 후, 좌측 사이드바에서 **"친구"** 탭을 클릭합니다.
2. 우측 상단의 **+** 버튼을 클릭합니다.
3. 친구로 추가할 사용자의 이메일을 입력합니다.
4. **"친구 요청 보내기"** 버튼을 클릭합니다.

### 3단계: 친구 요청 관리

1. **"친구요청"** 탭을 클릭합니다.
2. **"받은 요청"** 탭에서:
   - 수락: 친구 요청을 수락합니다.
   - 거절: 친구 요청을 거절합니다.
3. **"보낸 요청"** 탭에서:
   - 취소: 보낸 친구 요청을 취소합니다.

### 4단계: 채팅방 생성

1. **"채팅방"** 탭을 클릭합니다.
2. 우측 상단의 **+** 버튼을 클릭합니다.
3. 채팅방 이름을 입력합니다.
4. 친구 목록에서 채팅방에 초대할 친구를 선택합니다.
5. **"채팅방 생성"** 버튼을 클릭합니다.

### 5단계: 채팅하기

1. 채팅방 목록에서 채팅방을 선택합니다.
2. 하단의 메시지 입력창에 메시지를 입력합니다.
3. **"전송"** 버튼을 클릭하거나 Enter 키를 누릅니다.
4. 실시간으로 메시지가 전송되고 수신됩니다.

### 6단계: 메시지 관리

1. 자신이 보낸 메시지에 마우스를 올리면:
   - **수정**: 메시지 내용을 수정합니다.
   - **삭제**: 메시지를 삭제합니다.

### 7단계: 참가자 보기

1. 채팅방 상단의 **사람 아이콘**을 클릭합니다.
2. 채팅방에 참여 중인 모든 사용자를 확인할 수 있습니다.

### 8단계: 채팅방 나가기

1. 채팅방 상단의 **쓰레기통 아이콘**을 클릭합니다.
2. 확인 메시지에서 **확인**을 클릭합니다.

## 🎨 주요 화면 구성

### 로그인 화면
- 로그인 폼
- 회원가입 폼
- 탭 전환

### 메인 채팅 화면
#### 좌측 사이드바
- 사용자 정보 표시
- 3개의 탭: 채팅방 / 친구 / 친구요청
- 각 탭별 목록 및 액션 버튼

#### 우측 채팅 영역
- 채팅방 헤더 (이름, 참가자 수, 액션 버튼)
- 메시지 영역 (실시간 메시지 표시)
- 메시지 입력 영역

### 모달 창
- 친구 추가 모달
- 채팅방 생성 모달
- 참가자 목록 모달
- 메시지 수정 모달

## 🔧 기술 스택

### 프론트엔드
- **HTML5**: 시맨틱 마크업
- **CSS3**: 모던 반응형 디자인
  - CSS Variables
  - Flexbox
  - Grid
  - Animations
- **JavaScript (ES6+)**:
  - Async/Await
  - Fetch API
  - Event Delegation
  - Local Storage

### 실시간 통신
- **SockJS**: WebSocket 폴백
- **STOMP**: 메시징 프로토콜

### 백엔드 연동
- **REST API**: CRUD 작업
- **WebSocket**: 실시간 메시지
- **JWT**: 인증 토큰

## 📱 반응형 디자인

모바일, 태블릿, 데스크톱 모든 화면 크기에서 최적화되어 작동합니다.

## 🎯 테스트 시나리오

### 시나리오 1: 첫 사용자 등록 및 채팅
1. 브라우저 A에서 사용자 A로 회원가입 및 로그인
2. 브라우저 B에서 사용자 B로 회원가입 및 로그인
3. 사용자 A가 사용자 B에게 친구 요청
4. 사용자 B가 친구 요청 수락
5. 사용자 A가 채팅방 생성 (사용자 B 초대)
6. 양쪽에서 실시간 메시지 송수신 테스트

### 시나리오 2: 메시지 관리
1. 메시지 전송
2. 메시지 수정 (수정됨 표시 확인)
3. 메시지 삭제
4. 읽음 처리 확인

### 시나리오 3: 친구 관리
1. 친구 요청 보내기
2. 친구 요청 취소
3. 친구 요청 거절
4. 친구 삭제

### 시나리오 4: 채팅방 관리
1. 1:1 채팅방 생성
2. 그룹 채팅방 생성 (3명 이상)
3. 참가자 목록 확인
4. 채팅방 나가기

## 🔒 보안 기능

- JWT 토큰 기반 인증
- API 키 검증
- 비밀번호 암호화
- XSS 방지
- CORS 설정

## 📂 파일 구조

```
src/main/resources/static/
├── index.html          # 메인 HTML 파일
├── css/
│   └── style.css       # 스타일시트
└── js/
    └── app.js          # 애플리케이션 로직
```

## 💡 주요 특징

### 1. 실시간 채팅
- WebSocket을 통한 즉각적인 메시지 전송
- 채팅방별 구독 관리
- 자동 스크롤

### 2. 친화적인 UI/UX
- 직관적인 인터페이스
- 부드러운 애니메이션
- 실시간 알림 (Toast)
- 로딩 상태 표시

### 3. 완벽한 API 통합
- 모든 REST API 엔드포인트 사용
- WebSocket 실시간 통신
- 에러 핸들링
- 재연결 로직

### 4. 반응형 디자인
- 모바일 최적화
- 터치 친화적
- 다양한 화면 크기 지원

## 🐛 디버깅

### 브라우저 콘솔 확인
```javascript
// 현재 로그인된 사용자 확인
console.log(getCurrentUser());

// 토큰 확인
console.log(getToken());

// WebSocket 연결 상태 확인
console.log(stompClient);
```

### 네트워크 탭 확인
- Chrome DevTools > Network 탭에서 API 요청/응답 확인
- WS 탭에서 WebSocket 프레임 확인

## 🎉 모든 API 테스트 완료!

이 웹 애플리케이션으로 다음 모든 API를 테스트할 수 있습니다:

✅ **인증 API** (2개)
- POST /api/auth/register
- POST /api/auth/login

✅ **친구 API** (8개)
- POST /api/friends/request
- GET /api/friends/requests/received
- GET /api/friends/requests/sent
- POST /api/friends/requests/{requestId}/accept
- POST /api/friends/requests/{requestId}/reject
- DELETE /api/friends/requests/{requestId}/cancel
- GET /api/friends/list
- DELETE /api/friends/{friendshipId}

✅ **채팅방 API** (5개)
- POST /api/chatrooms
- GET /api/chatrooms
- GET /api/chatrooms/{chatroomId}
- GET /api/chatrooms/{chatroomId}/participants
- DELETE /api/chatrooms/{chatroomId}

✅ **메시지 API** (7개)
- GET /api/messages/chatrooms/{chatroomId}
- GET /api/messages/chatrooms/{chatroomId}/all
- GET /api/messages/chatrooms/{chatroomId}/unread-count
- POST /api/messages/chatrooms/{chatroomId}/read
- POST /api/messages/chatrooms/{chatroomId}
- PUT /api/messages/{messageId}
- DELETE /api/messages/{messageId}

✅ **WebSocket** (실시간 채팅)
- /ws 엔드포인트
- /app/chat/{chatroomId} (전송)
- /topic/chatroom/{chatroomId} (구독)

**총 22개 API + WebSocket 실시간 통신 완벽 구현!**
