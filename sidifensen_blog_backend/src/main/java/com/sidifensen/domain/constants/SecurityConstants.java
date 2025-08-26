package com.sidifensen.domain.constants;

import java.util.Arrays;
import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
public class SecurityConstants {

    /**
     * 不需要登录的接口
     */
    public static final String[] No_Need_Auth_Urls = {
            "/user/login",
            "/user/checkCode",
            "/user/register",
            "/user/sendEmail",
            "/user/verifyReset",
            "/user/resetPassword",
            "/user/admin/login",
            "/album/listAll",
            "/album/get/{albumId:\\d+}",  // 限制albumId只能是数字
            "/oauth/gitee/login",
            "/oauth/gitee/callback",
            "/oauth/github/login",
            "/oauth/github/callback",
            "/user/oauthLogin",
            "/favicon.ico"
    };

    // 允许的用户代理列表（可根据需要扩展）
    public static final List<String> Allowed_User_Agents = Arrays.asList(
            "Mozilla", "Chrome", "Firefox", "Safari", "Edge"
    );

}