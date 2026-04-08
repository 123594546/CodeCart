package com.codecart.vo.auth;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private String tokenType;

    private Long expireSeconds;

    private UserInfoVO userInfo;
}
