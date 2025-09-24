# ChatDB Application

Spring Boot 기반의 사용자 관리 애플리케이션입니다.

## 기술 스택

- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17+
- **Database**: MariaDB
- **ORM**: Spring Data JPA
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Build Tool**: Maven

## 주요 기능

- 사용자 생성 및 관리
- RESTful API 제공
- Swagger UI를 통한 API 문서화

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/chatdb/
│   │   ├── ChatDbApplication.java      # 메인 애플리케이션 클래스
│   │   ├── config/
│   │   │   └── OpenApiConfig.java      # Swagger 설정
│   │   ├── controller/
│   │   │   └── UserController.java     # REST API 컨트롤러
│   │   ├── dto/
│   │   │   └── CreateUserRequest.java  # Request DTO
│   │   ├── entity/
│   │   │   └── User.java               # JPA 엔티티
│   │   ├── repository/
│   │   │   └── UserRepository.java     # 데이터 액세스 레이어
│   │   └── service/
│   │       └── UserService.java        # 비즈니스 로직 레이어
│   └── resources/
│       ├── application.properties.template  # 설정 템플릿
│       └── application.properties      # 애플리케이션 설정 (Git에서 제외)
└── test/
    └── java/com/chatdb/
        └── ChatDbApplicationTests.java # 기본 테스트
```

## API 엔드포인트

- `POST /api/users` - 새 사용자 생성

더 자세한 API 문서는 Swagger UI에서 확인할 수 있습니다.

## 설정 방법

### 1. 데이터베이스 설정
`src/main/resources/application.properties.template` 파일을 참고하여 `application.properties` 파일을 생성하고 데이터베이스 연결 정보를 입력하세요.

```properties
spring.datasource.url=jdbc:mariadb://your-host:port/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password
```

### 2. 애플리케이션 실행
```bash
./mvnw spring-boot:run
```

### 3. 또는 JAR 빌드 후 실행
```bash
./mvnw clean package
java -jar target/chatdb-app-0.0.1-SNAPSHOT.jar
```

## 접속 정보

- **애플리케이션 URL**: http://localhost:8080
- **Swagger UI (API 문서)**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 개발 환경 요구사항

- Java 17 이상 (필수 설치)
- Maven 3.6 이상 (프로젝트에 Maven Wrapper 포함됨)

### Java 17 설치 방법

1. **Oracle JDK 17**: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. **OpenJDK 17**: https://jdk.java.net/17/
3. **Amazon Corretto 17**: https://aws.amazon.com/corretto/

설치 후 `JAVA_HOME` 환경 변수를 설정하고 `PATH`에 Java bin 디렉토리를 추가해야 합니다.

### 환경 변수 설정 (Windows)

1. 제어판 → 시스템 → 고급 시스템 설정 → 환경 변수
2. 시스템 변수에서 새로 만들기:
   - 변수 이름: `JAVA_HOME`
   - 변수 값: Java 설치 경로 (예: `C:\Program Files\Java\jdk-17`)
3. PATH 변수에 `%JAVA_HOME%\bin` 추가