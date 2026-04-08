package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private String roleCode;

    private String status;

    private LocalDateTime lastLoginTime;
}
