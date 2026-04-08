package com.codecart.common.context;

import com.codecart.common.exception.UnauthorizedException;

public final class UserContextHolder {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getRequiredUserId() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null) {
            throw new UnauthorizedException("未登录或登录已失效");
        }
        return loginUser.getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
