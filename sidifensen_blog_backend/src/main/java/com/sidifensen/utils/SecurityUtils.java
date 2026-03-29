package com.sidifensen.utils;

import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author sidifensen
 * @since 2025-07-11
 */
public class SecurityUtils {

    /**
     * 从Spring Security的上下文中获取当前登录用户id
     *
     * @return 用户id
     */
    public static Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser user) {
            return user.getSysUser().getId();
        }
        return 0;
    }


    /**
     * 获取当前登录用户
     * @return
     */
    public static SysUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (!(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return null;
        }
        return loginUser.getSysUser();
    }

    /**
     * 获取当前spring security的登录用户
     * @return
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (!(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return null;
        }
        return loginUser;
    }

}
