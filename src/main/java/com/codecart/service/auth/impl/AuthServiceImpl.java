package com.codecart.service.auth.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codecart.common.constants.BusinessConstants;
import com.codecart.common.context.LoginUser;
import com.codecart.common.exception.BizException;
import com.codecart.common.util.JwtUtil;
import com.codecart.common.util.PasswordUtil;
import com.codecart.config.properties.JwtProperties;
import com.codecart.dto.auth.LoginRequest;
import com.codecart.dto.auth.RegisterRequest;
import com.codecart.entity.SysUser;
import com.codecart.mapper.SysUserMapper;
import com.codecart.service.auth.AuthService;
import com.codecart.vo.auth.LoginResponse;
import com.codecart.vo.auth.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, request.getUsername())
                .last("LIMIT 1"));
        if (user == null || user.getDeleted() == 1) {
            throw new BizException("用户名或密码错误");
        }
        if (!BusinessConstants.UserStatus.ENABLED.equals(user.getStatus())) {
            throw new BizException("当前账号不可登录");
        }
        if (!PasswordUtil.matchesSha256(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(new LoginUser(user.getId(), user.getUsername(), user.getRoleCode())));
        response.setTokenType("Bearer");
        response.setExpireSeconds(jwtProperties.getExpireSeconds());
        response.setUserInfo(toUserInfo(user));
        return response;
    }

    @Override
    public UserInfoVO register(RegisterRequest request) {
        if (sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, request.getUsername())) > 0) {
            throw new BizException("用户名已存在");
        }
        if (StringUtils.hasText(request.getPhone())
                && sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getPhone, request.getPhone())) > 0) {
            throw new BizException("手机号已被使用");
        }
        if (StringUtils.hasText(request.getEmail())
                && sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getEmail, request.getEmail())) > 0) {
            throw new BizException("邮箱已被使用");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.sha256(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setPhone(StringUtils.hasText(request.getPhone()) ? request.getPhone() : null);
        user.setEmail(StringUtils.hasText(request.getEmail()) ? request.getEmail() : null);
        user.setRoleCode(BusinessConstants.RoleCode.USER);
        user.setStatus(BusinessConstants.UserStatus.ENABLED);
        user.setRemark("用户自助注册");
        sysUserMapper.insert(user);
        return toUserInfo(user);
    }

    @Override
    public UserInfoVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BizException("用户不存在");
        }
        return toUserInfo(user);
    }

    private UserInfoVO toUserInfo(SysUser user) {
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setStatus(user.getStatus());
        return userInfo;
    }
}
