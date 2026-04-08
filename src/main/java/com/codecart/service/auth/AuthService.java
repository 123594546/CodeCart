package com.codecart.service.auth;

import com.codecart.dto.auth.LoginRequest;
import com.codecart.dto.auth.RegisterRequest;
import com.codecart.vo.auth.LoginResponse;
import com.codecart.vo.auth.UserInfoVO;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserInfoVO register(RegisterRequest request);

    UserInfoVO getCurrentUser(Long userId);
}
