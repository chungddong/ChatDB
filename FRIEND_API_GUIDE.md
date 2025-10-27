# 친구 관리 API 가이드

## 목차
1. [JWT 인증 방식](#jwt-인증-방식)
2. [API 엔드포인트](#api-엔드포인트)
3. [Swagger UI 사용법](#swagger-ui-사용법)
4. [API 사용 예시](#api-사용-예시)
5. [테스트 시나리오](#테스트-시나리오)

---

## JWT 인증 방식

이 프로젝트는 JWT(JSON Web Token) 기반 인증을 사용합니다. 모든 친구 관리 API는 인증된 사용자만 접근할 수 있습니다.

### 인증 흐름
1. 회원가입 또는 로그인을 통해 JWT 토큰을 발급받습니다.
2. 발급받은 토큰을 `Authorization` 헤더에 `Bearer {token}` 형식으로 포함하여 API를 호출합니다.
3. 토큰의 유효기간은 24시간(86400000ms)입니다.

### 보안 기능
- 비밀번호는 BCrypt로 암호화되어 저장됩니다.
- JWT 토큰에는 사용자 ID와 이메일이 포함됩니다.
- 로그인하지 않은 사용자는 친구 관리 API에 접근할 수 없습니다.

---

## API 엔드포인트

### 1. 인증 관련 API

#### 1.1 회원가입
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "UserName",
  "password": "password123",
  "profileImage": "https://i.pravatar.cc/150?img=1"
}
```

**응답:**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "UserName",
    "profileImage": "https://i.pravatar.cc/150?img=1",
    "createdAt": "2025-01-15T10:30:00"
  }
}
```

#### 1.2 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답:**
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "UserName",
    "profileImage": "https://i.pravatar.cc/150?img=1",
    "createdAt": "2025-01-15T10:30:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. 친구 관리 API

모든 친구 관리 API는 `Authorization: Bearer {token}` 헤더가 필요합니다.

#### 2.1 친구 요청 보내기
```http
POST /api/friends/request
Authorization: Bearer {your-jwt-token}
Content-Type: application/json

{
  "friendUserId": 2
}
```

#### 2.2 받은 친구 요청 목록 조회
```http
GET /api/friends/requests/received
Authorization: Bearer {your-jwt-token}
```

#### 2.3 보낸 친구 요청 목록 조회
```http
GET /api/friends/requests/sent
Authorization: Bearer {your-jwt-token}
```

#### 2.4 친구 요청 수락
```http
POST /api/friends/requests/{requestId}/accept
Authorization: Bearer {your-jwt-token}
```

#### 2.5 친구 요청 거절
```http
POST /api/friends/requests/{requestId}/reject
Authorization: Bearer {your-jwt-token}
```

#### 2.6 친구 목록 조회
```http
GET /api/friends/list
Authorization: Bearer {your-jwt-token}
```

#### 2.7 친구 요청 취소
```http
DELETE /api/friends/requests/{requestId}/cancel
Authorization: Bearer {your-jwt-token}
```

#### 2.8 친구 삭제
```http
DELETE /api/friends/{friendshipId}
Authorization: Bearer {your-jwt-token}
```

---

## Swagger UI 사용법

Swagger UI를 통해 API를 쉽게 테스트할 수 있습니다.

### 1. Swagger UI 접속
애플리케이션을 실행한 후 브라우저에서 다음 URL로 접속합니다:
```
http://localhost:7070/swagger-ui.html
```

### 2. 로그인 및 토큰 발급
1. `Auth` 섹션에서 `/api/auth/register`를 통해 회원가입하거나
2. `/api/auth/login`을 통해 로그인합니다.
3. 응답에서 `token` 값을 복사합니다.

### 3. 인증 설정
1. Swagger UI 우측 상단의 **🔓 Authorize** 버튼을 클릭합니다.
2. `bearerAuth` 입력창에 복사한 토큰을 붙여넣습니다. (Bearer 제외, 토큰만 입력)
3. **Authorize** 버튼을 클릭합니다.
4. 잠금 아이콘이 🔒로 변경되면 인증이 완료된 것입니다.

### 4. API 테스트
이제 `Friendship` 섹션의 모든 API를 테스트할 수 있습니다.
- 각 API의 **Try it out** 버튼을 클릭합니다.
- 필요한 파라미터를 입력합니다.
- **Execute** 버튼을 클릭하여 API를 호출합니다.

---

## API 사용 예시 (cURL)

### 예시 1: 회원가입 및 로그인

#### 1단계: 회원가입
```bash
curl -X POST http://localhost:7070/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "username": "Alice",
    "password": "password123",
    "profileImage": "https://i.pravatar.cc/150?img=1"
  }'
```

#### 2단계: 로그인
```bash
curl -X POST http://localhost:7070/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

**응답에서 token 값을 저장합니다:**
```json
{
  "success": true,
  "message": "로그인에 성공했습니다.",
  "user": { ... },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 예시 2: 친구 요청 보내기

```bash
# TOKEN 변수에 로그인에서 받은 토큰을 저장
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:7070/api/friends/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "friendUserId": 2
  }'
```

**응답:**
```json
{
  "success": true,
  "message": "친구 요청을 보냈습니다.",
  "requestId": 1
}
```

### 예시 3: 받은 친구 요청 조회

```bash
curl -X GET http://localhost:7070/api/friends/requests/received \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "받은 친구 요청 목록을 조회했습니다.",
  "count": 2,
  "requests": [
    {
      "id": 1,
      "userId": 2,
      "username": "Bob",
      "email": "bob@example.com",
      "profileImage": "https://i.pravatar.cc/150?img=2",
      "status": "PENDING",
      "createdAt": "2025-01-15T09:00:00",
      "updatedAt": "2025-01-15T09:00:00"
    }
  ]
}
```

### 예시 4: 친구 요청 수락

```bash
curl -X POST http://localhost:7070/api/friends/requests/1/accept \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "친구 요청을 수락했습니다.",
  "friendshipId": 1
}
```

### 예시 5: 친구 목록 조회

```bash
curl -X GET http://localhost:7070/api/friends/list \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "친구 목록을 조회했습니다.",
  "count": 2,
  "friends": [
    {
      "id": 1,
      "userId": 2,
      "username": "Bob",
      "email": "bob@example.com",
      "profileImage": "https://i.pravatar.cc/150?img=2",
      "status": "ACCEPTED",
      "createdAt": "2025-01-15T08:00:00",
      "updatedAt": "2025-01-15T10:00:00"
    }
  ]
}
```

### 예시 6: 친구 요청 거절

```bash
curl -X POST http://localhost:7070/api/friends/requests/2/reject \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "친구 요청을 거절했습니다."
}
```

### 예시 7: 친구 요청 취소

```bash
curl -X DELETE http://localhost:7070/api/friends/requests/3/cancel \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "친구 요청을 취소했습니다."
}
```

### 예시 8: 친구 삭제

```bash
curl -X DELETE http://localhost:7070/api/friends/1 \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "친구를 삭제했습니다."
}
```

---

## 테스트 시나리오

### 시나리오 1: 새로운 사용자 간 친구 관계 맺기

1. **Alice 회원가입 및 로그인**
   ```bash
   # Alice 회원가입
   curl -X POST http://localhost:7070/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"alice@example.com","username":"Alice","password":"password123"}'

   # Alice 로그인
   curl -X POST http://localhost:7070/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"alice@example.com","password":"password123"}'

   # 받은 토큰을 ALICE_TOKEN에 저장
   ALICE_TOKEN="..."
   ```

2. **Bob 회원가입 및 로그인**
   ```bash
   # Bob 회원가입
   curl -X POST http://localhost:7070/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"bob@example.com","username":"Bob","password":"password123"}'

   # Bob 로그인
   curl -X POST http://localhost:7070/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"bob@example.com","password":"password123"}'

   # Bob의 userId를 확인 (응답의 user.id)
   # 받은 토큰을 BOB_TOKEN에 저장
   BOB_TOKEN="..."
   ```

3. **Alice가 Bob에게 친구 요청**
   ```bash
   curl -X POST http://localhost:7070/api/friends/request \
     -H "Authorization: Bearer $ALICE_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"friendUserId": 2}'
   ```

4. **Bob이 받은 친구 요청 확인**
   ```bash
   curl -X GET http://localhost:7070/api/friends/requests/received \
     -H "Authorization: Bearer $BOB_TOKEN"
   ```

5. **Bob이 친구 요청 수락**
   ```bash
   curl -X POST http://localhost:7070/api/friends/requests/{requestId}/accept \
     -H "Authorization: Bearer $BOB_TOKEN"
   ```

6. **Alice와 Bob 모두 친구 목록 확인**
   ```bash
   # Alice의 친구 목록
   curl -X GET http://localhost:7070/api/friends/list \
     -H "Authorization: Bearer $ALICE_TOKEN"

   # Bob의 친구 목록
   curl -X GET http://localhost:7070/api/friends/list \
     -H "Authorization: Bearer $BOB_TOKEN"
   ```

### 시나리오 2: 친구 요청 거절

1. **Charlie가 Alice에게 친구 요청**
   ```bash
   curl -X POST http://localhost:7070/api/friends/request \
     -H "Authorization: Bearer $CHARLIE_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"friendUserId": 1}'
   ```

2. **Alice가 받은 요청 확인 후 거절**
   ```bash
   # 받은 요청 확인
   curl -X GET http://localhost:7070/api/friends/requests/received \
     -H "Authorization: Bearer $ALICE_TOKEN"

   # 요청 거절
   curl -X POST http://localhost:7070/api/friends/requests/{requestId}/reject \
     -H "Authorization: Bearer $ALICE_TOKEN"
   ```

### 시나리오 3: 친구 요청 취소

1. **David가 Eve에게 친구 요청**
   ```bash
   curl -X POST http://localhost:7070/api/friends/request \
     -H "Authorization: Bearer $DAVID_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"friendUserId": 5}'
   ```

2. **David가 보낸 요청 확인**
   ```bash
   curl -X GET http://localhost:7070/api/friends/requests/sent \
     -H "Authorization: Bearer $DAVID_TOKEN"
   ```

3. **David가 요청 취소**
   ```bash
   curl -X DELETE http://localhost:7070/api/friends/requests/{requestId}/cancel \
     -H "Authorization: Bearer $DAVID_TOKEN"
   ```

### 시나리오 4: 에러 케이스 테스트

#### 4.1 인증 없이 API 호출
```bash
curl -X GET http://localhost:7070/api/friends/list
```
**예상 응답:** 401 Unauthorized

#### 4.2 만료된 토큰으로 API 호출
```bash
curl -X GET http://localhost:7070/api/friends/list \
  -H "Authorization: Bearer expired_token"
```
**예상 응답:** 401 Unauthorized

#### 4.3 자기 자신에게 친구 요청
```bash
curl -X POST http://localhost:7070/api/friends/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"friendUserId": 1}'
```
**예상 응답:**
```json
{
  "success": false,
  "message": "자기 자신에게 친구 요청을 보낼 수 없습니다."
}
```

#### 4.4 이미 친구인 사용자에게 재요청
```bash
curl -X POST http://localhost:7070/api/friends/request \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"friendUserId": 2}'
```
**예상 응답:**
```json
{
  "success": false,
  "message": "이미 친구 요청이 존재하거나 친구 관계입니다."
}
```

#### 4.5 존재하지 않는 사용자에게 친구 요청
```bash
curl -X POST http://localhost:7070/api/friends/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"friendUserId": 9999}'
```
**예상 응답:**
```json
{
  "success": false,
  "message": "친구 요청을 받을 사용자를 찾을 수 없습니다."
}
```

---

## 주의사항

1. **JWT 토큰**:
   - 로그인 시 발급받은 JWT 토큰은 24시간 동안 유효합니다.
   - 토큰이 만료되면 다시 로그인하여 새 토큰을 발급받아야 합니다.
   - 토큰은 `Authorization: Bearer {token}` 형식으로 전송해야 합니다.

2. **비밀번호 보안**:
   - 비밀번호는 BCrypt를 사용하여 암호화되어 저장됩니다.
   - 평문 비밀번호는 저장되지 않습니다.

3. **인증 필수**:
   - 회원가입과 로그인을 제외한 모든 친구 관리 API는 인증이 필요합니다.
   - 인증 없이 접근 시 401 Unauthorized 응답을 받습니다.

4. **데이터베이스**:
   - 현재 MariaDB를 사용하고 있습니다.
   - 애플리케이션 시작 시 테이블이 자동으로 생성됩니다 (spring.jpa.hibernate.ddl-auto=update).

5. **양방향 친구 관계**:
   - 한 사용자가 요청하고 다른 사용자가 수락하면 친구 관계가 성립됩니다.
   - 친구 목록 조회 시 양방향으로 검색됩니다.

---

## 추가 개선 사항

현재 구현된 기능 외에 다음과 같은 기능을 추가로 구현할 수 있습니다:

1. **토큰 갱신**: Refresh Token을 사용한 토큰 갱신 기능
2. **친구 검색**: 사용자명 또는 이메일로 친구 검색
3. **친구 추천**: 공통 친구를 기반으로 한 친구 추천
4. **알림 기능**: 친구 요청 수신 시 실시간 알림
5. **차단 기능**: 특정 사용자 차단 및 차단 해제
6. **친구 그룹**: 친구를 그룹으로 분류하여 관리
7. **프로필 수정**: 사용자 프로필 정보 수정 기능

---

## 문의 및 피드백

API 사용 중 문제가 발생하거나 개선사항이 있다면 이슈를 등록해주세요.
