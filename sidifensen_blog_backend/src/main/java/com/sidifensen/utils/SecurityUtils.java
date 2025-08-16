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
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = loginUser.getSysUser();
        return sysUser;
    }

    /**
     * 获取当前spring security的登录用户
     * @return
     */
    public static LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser;
    }

}
