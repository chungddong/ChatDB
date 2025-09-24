package com.chatdb.controller;

import com.chatdb.dto.CreateUserRequest;
import com.chatdb.entity.User;
import com.chatdb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "사용자 관리 API")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(
        summary = "사용자 생성", 
        description = "새로운 사용자를 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사용자 생성 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateUserRequest.class),
                examples = {
                    @ExampleObject(
                        name = "사용자 생성 예시",
                        summary = "일반적인 사용자 생성",
                        description = "사용자 ID와 비밀번호를 입력하여 새 사용자를 생성합니다.",
                        value = """
                        {
                          "userId": "john123",
                          "password": "mypassword123"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "한글 사용자 생성",
                        summary = "한글 사용자명 예시",
                        description = "한글 사용자명으로 계정을 생성하는 예시입니다.",
                        value = """
                        {
                          "userId": "홍길동",
                          "password": "비밀번호123"
                        }
                        """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "사용자 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 (중복된 사용자명 또는 필수 필드 누락)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "error": "User already exists: john123"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            if (request.getUserId() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "userId and password are required"));
            }
            
            User user = userService.createUser(request.getUserId(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}