package com.codecart.common.context;

import com.codecart.common.constants.BusinessConstants;
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

    public static LoginUser getRequiredLoginUser() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null) {
            throw new UnauthorizedException("未登录或登录已失效");
        }
        return loginUser;
    }

    public static Long getRequiredUserId() {
        return getRequiredLoginUser().getUserId();
    }

    public static void requireAdmin() {
        LoginUser loginUser = getRequiredLoginUser();
        if (!BusinessConstants.RoleCode.ADMIN.equals(loginUser.getRoleCode())) {
            throw new UnauthorizedException("无管理员权限");
        }
    }

    public static void clear() {
        HOLDER.remove();
    }
}
