package com.chatdb.controller;

import com.chatdb.dto.LoginRequest;
import com.chatdb.entity.User;
import com.chatdb.service.UserService;
import com.chatdb.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 인증 컨트롤러
 * 회원가입 및 로그인 처리
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 회원가입
     * @param user 사용자 정보 (email, username, password, profileImage)
     * @return 생성된 사용자 정보
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 이메일 중복 체크
            if (userService.existsByEmail(user.getEmail())) {
                response.put("success", false);
                response.put("message", "이미 존재하는 이메일입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 사용자명 중복 체크
            if (userService.existsByUsername(user.getUsername())) {
                response.put("success", false);
                response.put("message", "이미 존재하는 사용자명입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 비밀번호 암호화
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 사용자 저장
            User savedUser = userService.save(user);
            
            // 비밀번호 제외한 사용자 정보 반환
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", savedUser.getId());
            userData.put("email", savedUser.getEmail());
            userData.put("username", savedUser.getUsername());
            userData.put("profileImage", savedUser.getProfileImage());
            userData.put("createdAt", savedUser.getCreatedAt());
            
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("user", userData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 로그인
     * @param loginRequest 로그인 정보 (email, password만 필요)
     * @return 로그인 결과 및 사용자 정보
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            
            // 이메일로 사용자 조회
            Optional<User> userOptional = userService.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "존재하지 않는 이메일입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            User user = userOptional.get();

            // 비밀번호 확인 (암호화된 비밀번호 비교)
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.put("success", false);
                response.put("message", "비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());

            // 로그인 성공 - 비밀번호 제외한 사용자 정보 + JWT 토큰 반환
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("username", user.getUsername());
            userData.put("profileImage", user.getProfileImage());
            userData.put("createdAt", user.getCreatedAt());

            response.put("success", true);
            response.put("message", "로그인에 성공했습니다.");
            response.put("user", userData);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
