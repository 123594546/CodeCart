package com.codecart.controller;

import com.codecart.common.context.UserContextHolder;
import com.codecart.common.result.ApiResult;
import com.codecart.dto.auth.LoginRequest;
import com.codecart.dto.auth.RegisterRequest;
import com.codecart.service.auth.AuthService;
import com.codecart.vo.auth.LoginResponse;
import com.codecart.vo.auth.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResult<UserInfoVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResult.success(authService.register(request));
    }

    @GetMapping("/me")
    public ApiResult<UserInfoVO> currentUser() {
        return ApiResult.success(authService.getCurrentUser(UserContextHolder.getRequiredUserId()));
    }
}
