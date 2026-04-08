package com.codecart.vo.auth;

import lombok.Data;

@Data
public class UserInfoVO {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private String roleCode;

    private String status;
}
