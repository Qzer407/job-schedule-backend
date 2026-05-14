package com.qzer.scheduler.modules.auth.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.auth.dto.LoginRequest;
import com.qzer.scheduler.modules.auth.dto.RegisterRequest;
import com.qzer.scheduler.modules.auth.entity.User;
import com.qzer.scheduler.modules.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        User registeredUser = userService.register(user);
        Map<String, Object> result = new HashMap<>();
        result.put("user", registeredUser);
        result.put("message", "注册成功");
        return ApiResponse.success(result);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        User user = userService.login(username, password);
        String token = userService.generateToken(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", "Bearer");
        result.put("user", user);
        log.info("用户登录成功: {}", username);
        return ApiResponse.success(result);
    }

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未授权");
        }
        String token = authHeader.substring(7);
        User user = userService.getUserFromToken(token);
        if (user == null) {
            return ApiResponse.error(401, "token无效或已过期");
        }
        return ApiResponse.success(user);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        String oldPassword = request.get("oldPassword").toString();
        String newPassword = request.get("newPassword").toString();
        userService.changePassword(userId, oldPassword, newPassword);
        return ApiResponse.success(null);
    }
}
