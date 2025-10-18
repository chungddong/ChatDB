# ChatDB 개발 튜토리얼

Spring Boot + JPA를 사용한 새로운 기능 추가 가이드

---

## 📋 목차
1. [개발 순서](#개발-순서)
2. [1단계: 엔티티 생성](#1단계-엔티티-생성)
3. [2단계: 리포지토리 생성](#2단계-리포지토리-생성)
4. [3단계: 서비스 생성](#3단계-서비스-생성)
5. [4단계: DTO 생성 (필요시)](#4단계-dto-생성-필요시)
6. [5단계: 컨트롤러 생성](#5단계-컨트롤러-생성)
7. [6단계: 테스트](#6단계-테스트)

---

## 개발 순서

```
1. 엔티티(Entity) 생성
   ↓
2. 리포지토리(Repository) 생성
   ↓
3. 서비스(Service) 생성
   ↓
4. DTO 생성 (필요한 경우만)
   ↓
5. 컨트롤러(Controller) 생성
   ↓
6. Swagger로 테스트
```

---

## 1단계: 엔티티 생성

### 위치
`src/main/java/com/chatdb/entity/`

### 예시: User.java
```java
package com.chatdb.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * users 테이블과 매핑
 */
@Entity
@Table(name = "users")
public class User {
    
    /** 사용자 고유 ID (자동 생성) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 이메일 (필수, 고유값) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /** 사용자명 (필수) */
    @Column(nullable = false, length = 50)
    private String username;
    
    /** 비밀번호 (필수) */
    @Column(nullable = false)
    private String password;
    
    /** 생성 일시 (자동 생성) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /** 수정 일시 (자동 갱신) */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 엔티티 생성 시 자동으로 생성/수정 일시 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 엔티티 수정 시 자동으로 수정 일시 갱신
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter/Setter 생성 필요
}
```

### 체크리스트
- [ ] `@Entity` 어노테이션 추가
- [ ] `@Table(name = "테이블명")` 지정
- [ ] `@Id` 및 `@GeneratedValue` 설정
- [ ] 필요한 컬럼에 `@Column` 설정
- [ ] `@PrePersist`, `@PreUpdate`로 자동 날짜 관리
- [ ] **모든 필드와 메서드에 주석 추가**
- [ ] Getter/Setter 생성

---

## 2단계: 리포지토리 생성

### 위치
`src/main/java/com/chatdb/repository/`

### 예시: UserRepository.java
```java
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
     * 이메일 존재 여부 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}
```

### 체크리스트
- [ ] `JpaRepository<엔티티, ID타입>` 상속
- [ ] `@Repository` 어노테이션 추가
- [ ] 필요한 커스텀 쿼리 메서드 작성
- [ ] **모든 메서드에 주석 추가**

### 주요 메서드 네이밍 규칙
- `findBy필드명`: 조회
- `existsBy필드명`: 존재 여부
- `countBy필드명`: 개수 세기
- `deleteBy필드명`: 삭제

---

## 3단계: 서비스 생성

### 위치
`src/main/java/com/chatdb/service/`

### 예시: UserService.java
```java
package com.chatdb.service;

import com.chatdb.entity.User;
import com.chatdb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 서비스
 * 사용자 관련 비즈니스 로직 처리
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 Optional
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 사용자 저장
     * @param user 사용자
     * @return 저장된 사용자
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
```

### 체크리스트
- [ ] `@Service` 어노테이션 추가
- [ ] Repository를 생성자 주입으로 받기
- [ ] 읽기 전용은 `@Transactional(readOnly = true)` 추가
- [ ] 쓰기 작업은 `@Transactional` 추가
- [ ] **모든 메서드에 주석 추가**
- [ ] 비즈니스 로직(유효성 검사, 중복 체크 등) 구현

---

## 4단계: DTO 생성 (필요시)

### 위치
`src/main/java/com/chatdb/dto/`

### 언제 만드나?
- **요청 데이터가 엔티티와 다를 때** (예: 로그인은 email, password만 필요)
- **응답 데이터에서 일부 필드를 숨겨야 할 때** (예: 비밀번호 제외)

### 예시: LoginRequest.java
```java
package com.chatdb.dto;

/**
 * 로그인 요청 DTO
 * 이메일과 비밀번호만 포함
 */
public class LoginRequest {
    
    /** 이메일 */
    private String email;
    
    /** 비밀번호 */
    private String password;
    
    // Getter/Setter만 작성
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
```

### 체크리스트
- [ ] 필요한 필드만 포함
- [ ] **모든 필드에 주석 추가**
- [ ] Getter/Setter 생성

### ⚠️ 주의사항
**불필요한 DTO는 만들지 말 것!**
- Swagger로 테스트할 거라면 엔티티를 직접 사용해도 됨
- 꼭 필요한 경우에만 생성

---

## 5단계: 컨트롤러 생성

### 위치
`src/main/java/com/chatdb/controller/`

### 예시: AuthController.java
```java
package com.chatdb.controller;

import com.chatdb.dto.LoginRequest;
import com.chatdb.entity.User;
import com.chatdb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 컨트롤러
 * 회원가입 및 로그인 처리
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 회원가입
     * @param user 사용자 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 중복 체크
            if (userService.existsByEmail(user.getEmail())) {
                response.put("success", false);
                response.put("message", "이미 존재하는 이메일입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 저장
            User savedUser = userService.save(user);
            
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("user", savedUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
```

### 체크리스트
- [ ] `@RestController` 어노테이션 추가
- [ ] `@RequestMapping("/api/경로")` 설정
- [ ] `@Tag`로 Swagger 그룹 설정
- [ ] 각 메서드에 `@Operation` 추가 (Swagger 문서화)
- [ ] **모든 클래스와 메서드에 주석 추가**
- [ ] Service를 생성자 주입으로 받기
- [ ] 적절한 HTTP 상태 코드 반환
- [ ] 예외 처리 포함

### HTTP 메서드
- `@GetMapping`: 조회
- `@PostMapping`: 생성
- `@PutMapping`: 전체 수정
- `@PatchMapping`: 부분 수정
- `@DeleteMapping`: 삭제

### HTTP 상태 코드
- `200 OK`: 성공
- `201 CREATED`: 생성 성공
- `400 BAD_REQUEST`: 잘못된 요청
- `401 UNAUTHORIZED`: 인증 실패
- `404 NOT_FOUND`: 리소스 없음
- `500 INTERNAL_SERVER_ERROR`: 서버 오류

---

## 6단계: 테스트

### Swagger UI 접속
```
http://localhost:8080/swagger-ui/index.html
```

### 테스트 순서
1. 애플리케이션 실행
2. Swagger UI 접속
3. API 선택 → "Try it out" 클릭
4. 요청 데이터 입력
5. "Execute" 클릭
6. 응답 확인

### 확인 사항
- [ ] API가 Swagger에 표시되는가?
- [ ] 요청 필드가 올바르게 표시되는가?
- [ ] 성공 응답이 올바른가?
- [ ] 실패 케이스도 올바르게 처리되는가?
- [ ] 데이터베이스에 데이터가 저장되는가?

---

## 📝 추가 규칙

### 1. 주석 작성 필수
- 모든 클래스, 필드, 메서드에 한글 주석 작성
- JavaDoc 형식 사용 (`/** */`)

### 2. DTO는 필요할 때만
- 요청/응답 데이터가 엔티티와 다를 때만 생성
- 단순 테스트용이면 엔티티 직접 사용

### 3. 트랜잭션 관리
- 읽기: `@Transactional(readOnly = true)`
- 쓰기: `@Transactional`

### 4. 네이밍 규칙
- 엔티티: 단수형 (User, Post)
- 테이블: 복수형 (users, posts)
- Repository: 엔티티명 + Repository
- Service: 엔티티명 + Service
- Controller: 기능명 + Controller

### 5. 패키지 구조
```
com.chatdb
├── entity/          # 엔티티
├── repository/      # 리포지토리
├── service/         # 서비스
├── controller/      # 컨트롤러
├── dto/             # DTO (필요시)
└── config/          # 설정
```

---

## 🔧 데이터베이스 설정

### application.properties
```properties
# JPA 설정
spring.jpa.hibernate.ddl-auto=update  # 자동으로 테이블 생성/수정
spring.jpa.show-sql=true              # SQL 쿼리 출력
spring.jpa.properties.hibernate.format_sql=true  # SQL 포맷팅
```

### ddl-auto 옵션
- `none`: 아무것도 하지 않음
- `validate`: 스키마 검증만
- `update`: 변경사항만 반영 (운영 환경 주의)
- `create`: 매번 새로 생성 (기존 데이터 삭제)
- `create-drop`: 종료 시 삭제

---

## ✅ 개발 완료 체크리스트

### 파일 생성
- [ ] Entity 생성
- [ ] Repository 생성
- [ ] Service 생성
- [ ] DTO 생성 (필요시)
- [ ] Controller 생성

### 코드 품질
- [ ] 모든 곳에 주석 추가
- [ ] 트랜잭션 설정
- [ ] 예외 처리
- [ ] Swagger 어노테이션

### 테스트
- [ ] Swagger로 API 테스트
- [ ] 성공 케이스 확인
- [ ] 실패 케이스 확인
- [ ] DB 데이터 확인

---

## 🎯 예제: 새로운 Post 엔티티 만들기

1. **Entity**: `Post.java` 생성 (제목, 내용, 작성자)
2. **Repository**: `PostRepository.java` 생성
3. **Service**: `PostService.java` 생성
4. **Controller**: `PostController.java` 생성
5. **Swagger**: `http://localhost:8080/swagger-ui/index.html` 확인
6. **테스트**: 게시글 생성/조회/수정/삭제

---

## 📚 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Swagger/OpenAPI](https://swagger.io/)

---

**작성일**: 2025-10-18  
**프로젝트**: ChatDB  
**작성자**: GitHub Copilot
