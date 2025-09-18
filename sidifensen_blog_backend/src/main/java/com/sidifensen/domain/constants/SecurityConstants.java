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
            "/user/info/{userId:\\d+}",
            "/album/listAll",
            "/album/get/{albumId:\\d+}",
            "/oauth/gitee/login",
            "/oauth/gitee/callback",
            "/oauth/github/login",
            "/oauth/github/callback",
            "/user/oauthLogin",
            "/article/listAll",
            "/article/user/list",
            "/article/user/{userId:\\d+}/statistics",
            "/article/incrReadCount/{articleId:\\d+}",
            "/favicon.ico"
    };

    /**
     * 可选登录的接口（有token就认证，没有token也不报错）
     */
    public static final String[] Optional_Auth_Urls = {
            "/article/get/{articleId:\\d+}", // 获取文章详情，限制articleId只能是数字
            "/comment/list", // 获取评论列表，使用查询参数
            "/comment/reply/list", // 获取回复列表，使用查询参数
    };

    // 允许的用户代理列表（可根据需要扩展）
    public static final List<String> Allowed_User_Agents = Arrays.asList(
            "Mozilla", "Chrome", "Firefox", "Safari", "Edge");

}
