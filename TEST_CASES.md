# ChatDB API 테스트 케이스

이 문서는 ChatDB의 모든 기능을 체계적으로 테스트하기 위한 시나리오를 제공합니다.

## 📋 목차
- [테스트 환경 설정](#테스트-환경-설정)
- [테스트 계정](#테스트-계정)
- [테스트 시나리오](#테스트-시나리오)

---

## 테스트 환경 설정

### 사전 준비
1. 서버 실행: `http://localhost:7070`
2. Swagger UI 접속: `http://localhost:7070/swagger-ui/index.html`
3. API 키 설정:
   - Authorize 버튼 클릭
   - **apiKey**: `9F7D521C7C3391115FB42542C7E5F` 입력

---

## 테스트 계정

다음 10개의 테스트 계정을 사용합니다. 각 계정은 회원가입을 통해 생성해야 합니다.

| No | 이메일 | 사용자명 | 비밀번호 | 역할 |
|----|--------|---------|---------|------|
| 1 | alice@test.com | Alice | test1234 | 주 테스터 |
| 2 | bob@test.com | Bob | test1234 | Alice의 친구 |
| 3 | charlie@test.com | Charlie | test1234 | Alice의 친구 |
| 4 | david@test.com | David | test1234 | Alice의 친구 |
| 5 | emma@test.com | Emma | test1234 | Bob의 친구 |
| 6 | frank@test.com | Frank | test1234 | 친구 요청 대기 |
| 7 | grace@test.com | Grace | test1234 | 친구 요청 거절 테스트 |
| 8 | henry@test.com | Henry | test1234 | 채팅방 참가자 |
| 9 | iris@test.com | Iris | test1234 | 채팅방 참가자 |
| 10 | jack@test.com | Jack | test1234 | 독립 사용자 |

---

## 테스트 시나리오

### Phase 1: 회원가입 및 로그인

#### 1-1. 회원가입 테스트

**목표**: 10개의 테스트 계정 모두 생성

**진행 순서**:
1. Swagger UI → **Auth** → `POST /api/auth/register` 선택
2. 각 계정에 대해 다음 JSON으로 회원가입:

```json
{
  "email": "alice@test.com",
  "username": "Alice",
  "password": "test1234",
  "profileImage": null
}
```

3. 나머지 9개 계정도 동일하게 반복 (이메일과 사용자명만 변경)

**예상 결과**:
- 각 요청마다 `"success": true` 응답
- 사용자 ID가 1부터 10까지 순차적으로 생성됨

**검증 포인트**:
- ✅ 동일한 이메일로 재가입 시 에러 발생
- ✅ 동일한 사용자명도 가입 가능 (이메일만 고유)

---

#### 1-2. 로그인 테스트

**목표**: Alice 계정으로 로그인하여 JWT 토큰 획득

**진행 순서**:
1. Swagger UI → **Auth** → `POST /api/auth/login` 선택
2. Request body:
```json
{
  "email": "alice@test.com",
  "password": "test1234"
}
```
3. 응답에서 `token` 값 복사

**예상 결과**:
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "user": {
    "id": 1,
    "email": "alice@test.com",
    "username": "Alice",
    "profileImage": null,
    "createdAt": "2025-10-31T..."
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**다음 단계**:
- 토큰을 Authorize 버튼의 **bearerAuth**에 입력
- 이후 모든 API는 이 토큰으로 요청

**검증 포인트**:
- ✅ 잘못된 이메일로 로그인 시 401 에러
- ✅ 잘못된 비밀번호로 로그인 시 401 에러

---

### Phase 2: 친구 관리

#### 2-1. 친구 요청 보내기

**목표**: Alice가 Bob, Charlie, David에게 친구 요청

**사전 조건**: Alice로 로그인된 상태 (JWT 토큰 설정됨)

**진행 순서**:
1. Swagger UI → **Friendship** → `POST /api/friends/request` 선택
2. Bob에게 친구 요청:
```json
{
  "friendEmail": "bob@test.com"
}
```

3. 동일하게 Charlie, David에게도 요청:
```json
{
  "friendEmail": "charlie@test.com"
}
```
```json
{
  "friendEmail": "david@test.com"
}
```

**예상 결과**:
- 각 요청마다 `"success": true`
- `requestId`가 1, 2, 3으로 생성됨

**검증 포인트**:
- ✅ 동일한 사용자에게 중복 요청 시 에러
- ✅ 존재하지 않는 이메일로 요청 시 에러
- ✅ 자기 자신에게 요청 시 에러

---

#### 2-2. 보낸 친구 요청 목록 조회

**목표**: Alice가 보낸 친구 요청 확인

**진행 순서**:
1. Swagger UI → **Friendship** → `GET /api/friends/requests/sent`
2. Execute 클릭

**예상 결과**:
```json
{
  "success": true,
  "message": "보낸 친구 요청 목록을 조회했습니다.",
  "requests": [
    {
      "id": 1,
      "userId": 2,
      "username": "Bob",
      "email": "bob@test.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "...",
      "updatedAt": "..."
    },
    {
      "id": 2,
      "userId": 3,
      "username": "Charlie",
      "email": "charlie@test.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "...",
      "updatedAt": "..."
    },
    {
      "id": 3,
      "userId": 4,
      "username": "David",
      "email": "david@test.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "...",
      "updatedAt": "..."
    }
  ],
  "count": 3
}
```

---

#### 2-3. 받은 친구 요청 조회 및 수락

**목표**: Bob이 Alice의 친구 요청을 수락

**진행 순서**:
1. **Bob으로 로그인**:
   - `POST /api/auth/login`으로 Bob의 토큰 획득
   - Authorize → bearerAuth에 Bob의 토큰 입력

2. 받은 친구 요청 확인:
   - `GET /api/friends/requests/received` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "받은 친구 요청 목록을 조회했습니다.",
  "requests": [
    {
      "id": 1,
      "userId": 1,
      "username": "Alice",
      "email": "alice@test.com",
      "profileImage": null,
      "status": "PENDING",
      "createdAt": "...",
      "updatedAt": "..."
    }
  ],
  "count": 1
}
```

3. 친구 요청 수락:
   - `POST /api/friends/requests/1/accept` 실행 (requestId = 1)

**예상 결과**:
```json
{
  "success": true,
  "message": "친구 요청을 수락했습니다.",
  "friendshipId": 1
}
```

---

#### 2-4. 친구 요청 거절 테스트

**목표**: Charlie가 Alice의 친구 요청을 거절

**진행 순서**:
1. **Charlie로 로그인**
2. 받은 친구 요청 확인: `GET /api/friends/requests/received`
3. 친구 요청 거절: `POST /api/friends/requests/2/reject` (requestId = 2)

**예상 결과**:
```json
{
  "success": true,
  "message": "친구 요청을 거절했습니다."
}
```

**검증**:
- Alice의 보낸 요청 목록에서 Charlie 요청의 status가 `REJECTED`로 변경

---

#### 2-5. 친구 요청 취소 테스트

**목표**: Alice가 David에게 보낸 요청을 취소

**진행 순서**:
1. **Alice로 다시 로그인** (토큰 재설정)
2. 보낸 요청 확인: `GET /api/friends/requests/sent`
3. David 요청 취소: `DELETE /api/friends/requests/3/cancel` (requestId = 3)

**예상 결과**:
```json
{
  "success": true,
  "message": "친구 요청을 취소했습니다."
}
```

**검증**:
- 보낸 요청 목록에서 David 요청이 사라짐

---

#### 2-6. 친구 목록 조회

**목표**: Alice의 친구 목록 확인

**진행 순서**:
1. Alice로 로그인 상태 유지
2. `GET /api/friends/list` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "친구 목록을 조회했습니다.",
  "friends": [
    {
      "id": 1,
      "userId": 2,
      "username": "Bob",
      "email": "bob@test.com",
      "profileImage": null,
      "status": "ACCEPTED",
      "createdAt": "...",
      "updatedAt": "..."
    }
  ],
  "count": 1
}
```

**참고**: Bob만 수락했으므로 친구는 1명

---

#### 2-7. 추가 친구 관계 생성

**목표**: 테스트를 위한 추가 친구 관계 구축

**진행 순서**:

1. **David로 로그인** → Alice에게 친구 요청 → Alice가 수락
2. **Emma로 로그인** → Bob에게 친구 요청 → Bob이 수락
3. **Frank로 로그인** → Alice에게 친구 요청 (수락하지 않음, PENDING 상태 유지)

**최종 친구 관계**:
- Alice ↔ Bob (ACCEPTED)
- Alice ↔ David (ACCEPTED)
- Bob ↔ Emma (ACCEPTED)
- Frank → Alice (PENDING)
- Charlie ← Alice (REJECTED)

---

### Phase 3: 채팅방 관리

#### 3-1. 채팅방 생성 (3명)

**목표**: Alice가 Bob, David와 함께 채팅방 생성

**진행 순서**:
1. Alice로 로그인 상태
2. Alice의 userId 확인 (1번)
3. `POST /api/chatrooms` 실행:
```json
{
  "chatroomName": "프로젝트 팀방",
  "participantIds": [1, 2, 4]
}
```
※ 1=Alice, 2=Bob, 4=David

**예상 결과**:
```json
{
  "success": true,
  "message": "채팅방이 생성되었습니다.",
  "chatRoom": {
    "id": 1,
    "chatroomName": "프로젝트 팀방",
    "createdAt": "...",
    "updatedAt": "...",
    "participantCount": 3
  }
}
```

**검증 포인트**:
- ✅ participantCount가 3명으로 표시
- ✅ 채팅방 ID가 1로 생성됨

---

#### 3-2. 채팅방 생성 (2명 - 1:1 채팅)

**목표**: Bob과 Emma의 1:1 채팅방 생성

**진행 순서**:
1. **Bob으로 로그인**
2. Bob의 userId = 2, Emma의 userId = 5
3. `POST /api/chatrooms` 실행:
```json
{
  "chatroomName": "Bob & Emma",
  "participantIds": [2, 5]
}
```

**예상 결과**:
```json
{
  "success": true,
  "message": "채팅방이 생성되었습니다.",
  "chatRoom": {
    "id": 2,
    "chatroomName": "Bob & Emma",
    "createdAt": "...",
    "updatedAt": "...",
    "participantCount": 2
  }
}
```

---

#### 3-3. 채팅방 생성 (대규모 그룹)

**목표**: 여러 명이 참여하는 대규모 채팅방

**진행 순서**:
1. **Alice로 로그인**
2. `POST /api/chatrooms` 실행:
```json
{
  "chatroomName": "전체 회의방",
  "participantIds": [1, 2, 4, 5, 8, 9]
}
```
※ Alice, Bob, David, Emma, Henry, Iris

**예상 결과**:
- participantCount: 6

---

#### 3-4. 모든 채팅방 조회

**목표**: 생성된 모든 채팅방 확인

**진행 순서**:
1. Alice로 로그인 상태
2. `GET /api/chatrooms` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "채팅방 목록을 조회했습니다.",
  "chatRooms": [
    {
      "id": 1,
      "chatroomName": "프로젝트 팀방",
      "createdAt": "...",
      "updatedAt": "...",
      "participantCount": 3
    },
    {
      "id": 2,
      "chatroomName": "Bob & Emma",
      "createdAt": "...",
      "updatedAt": "...",
      "participantCount": 2
    },
    {
      "id": 3,
      "chatroomName": "전체 회의방",
      "createdAt": "...",
      "updatedAt": "...",
      "participantCount": 6
    }
  ],
  "count": 3
}
```

---

#### 3-5. 특정 채팅방 조회

**목표**: 채팅방 ID로 개별 채팅방 정보 확인

**진행 순서**:
1. `GET /api/chatrooms/1` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "채팅방 정보를 조회했습니다.",
  "chatRoom": {
    "id": 1,
    "chatroomName": "프로젝트 팀방",
    "createdAt": "...",
    "updatedAt": "...",
    "participantCount": 3
  }
}
```

**검증 포인트**:
- ✅ 존재하지 않는 채팅방 ID로 조회 시 404 에러

---

#### 3-6. 채팅방 참가자 조회

**목표**: 특정 채팅방의 참가자 목록 확인

**진행 순서**:
1. `GET /api/chatrooms/1/participants` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "참가자 목록을 조회했습니다.",
  "participants": [
    {
      "participantId": 1,
      "userId": 1,
      "username": "Alice",
      "email": "alice@test.com",
      "profileImage": null,
      "joinedAt": "...",
      "lastReadAt": "..."
    },
    {
      "participantId": 2,
      "userId": 2,
      "username": "Bob",
      "email": "bob@test.com",
      "profileImage": null,
      "joinedAt": "...",
      "lastReadAt": "..."
    },
    {
      "participantId": 3,
      "userId": 4,
      "username": "David",
      "email": "david@test.com",
      "profileImage": null,
      "joinedAt": "...",
      "lastReadAt": "..."
    }
  ],
  "count": 3
}
```

**검증 포인트**:
- ✅ participantId와 userId는 다른 값
- ✅ 참가자 수가 채팅방 정보의 participantCount와 일치

---

#### 3-7. 채팅방 삭제

**목표**: 채팅방 삭제 및 참가자 자동 삭제 확인

**진행 순서**:
1. `DELETE /api/chatrooms/2` 실행 (Bob & Emma 방 삭제)

**예상 결과**:
```json
{
  "success": true,
  "message": "채팅방이 삭제되었습니다."
}
```

**검증**:
1. `GET /api/chatrooms` 실행 → count가 2로 감소
2. `GET /api/chatrooms/2` 실행 → 404 에러
3. `GET /api/chatrooms/2/participants` 실행 → 404 에러

---

### Phase 4: 친구 삭제

#### 4-1. 친구 관계 삭제

**목표**: Alice와 Bob의 친구 관계 해제

**진행 순서**:
1. **Alice로 로그인**
2. Alice의 친구 목록 조회: `GET /api/friends/list`
3. Bob의 friendshipId 확인 (id = 1)
4. 친구 삭제: `DELETE /api/friends/1` 실행

**예상 결과**:
```json
{
  "success": true,
  "message": "친구를 삭제했습니다."
}
```

**검증**:
1. Alice의 친구 목록에서 Bob이 사라짐
2. Bob도 로그인하여 친구 목록 확인 → Alice가 사라짐 (양방향 삭제)

**검증 포인트**:
- ✅ 이미 삭제된 친구를 다시 삭제하면 404 에러
- ✅ 다른 사용자의 친구 관계는 삭제 불가

---

### Phase 5: 엣지 케이스 및 에러 처리

#### 5-1. 인증 에러 테스트

**테스트 항목**:
1. JWT 토큰 없이 보호된 API 호출 → 401 에러
2. 잘못된 JWT 토큰으로 API 호출 → 401 에러
3. 만료된 JWT 토큰으로 API 호출 → 401 에러 (24시간 후)

---

#### 5-2. 권한 에러 테스트

**테스트 항목**:
1. **Jack으로 로그인** (다른 사용자)
2. Alice가 받은 친구 요청(Frank의 요청)을 Jack이 수락 시도 → 400 에러
3. Alice와 Bob의 채팅방에 Jack이 참가자 조회 시도 → 정상 동작 (현재는 제한 없음)

---

#### 5-3. 유효성 검사 테스트

**테스트 항목**:
1. 채팅방 이름 없이 생성 시도 → 400 에러
2. 참가자 없이 채팅방 생성 시도 → 400 에러
3. 존재하지 않는 userId로 채팅방 생성 → 일부 참가자만 추가됨 (현재 동작)

---

## 테스트 결과 체크리스트

### ✅ 회원가입 및 로그인
- [ ] 10개 계정 모두 생성 완료
- [ ] 중복 이메일 에러 확인
- [ ] 로그인 성공 및 JWT 토큰 획득
- [ ] 잘못된 로그인 정보로 에러 확인

### ✅ 친구 관리
- [ ] 친구 요청 보내기 (이메일 기반)
- [ ] 보낸 요청 목록 조회
- [ ] 받은 요청 목록 조회
- [ ] 친구 요청 수락
- [ ] 친구 요청 거절
- [ ] 친구 요청 취소
- [ ] 친구 목록 조회
- [ ] 친구 삭제

### ✅ 채팅방 관리
- [ ] 3명 이상 그룹 채팅방 생성
- [ ] 1:1 채팅방 생성
- [ ] 모든 채팅방 조회
- [ ] 특정 채팅방 조회
- [ ] 채팅방 참가자 조회
- [ ] 채팅방 삭제

### ✅ 에러 처리
- [ ] API 키 없이 요청 시 에러
- [ ] JWT 토큰 없이 요청 시 에러
- [ ] 권한 없는 작업 시도 시 에러
- [ ] 유효하지 않은 데이터 입력 시 에러

---

## 테스트 완료 후 데이터 상태

### 사용자 (10명)
- Alice, Bob, Charlie, David, Emma, Frank, Grace, Henry, Iris, Jack

### 친구 관계
- Alice ↔ David (ACCEPTED)
- Bob ↔ Emma (ACCEPTED)
- Frank → Alice (PENDING)
- Charlie ← Alice (REJECTED)

### 채팅방 (2개)
1. **프로젝트 팀방** - Alice, Bob, David
2. **전체 회의방** - Alice, Bob, David, Emma, Henry, Iris

---

## 추가 테스트 시나리오 (선택)

### 동시성 테스트
1. 두 명이 동시에 서로에게 친구 요청
2. 여러 사용자가 동시에 같은 채팅방 생성

### 대용량 테스트
1. 친구 100명 이상 생성
2. 채팅방 참가자 100명 이상 추가
3. 채팅방 100개 이상 생성

### 성능 테스트
1. 친구 목록 조회 속도 측정
2. 채팅방 목록 조회 속도 측정
3. 참가자가 많은 채팅방 조회 속도 측정
