package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
public class SecurityConstants {

    /**
     * 可选认证接口（有token就认证，没有token就跳过，认证失败也放行）
     * 包括：不需要登录的公开接口 + 可选登录的接口
     */
    public static final String[] Optional_Auth_Urls = {
            // 用户相关
            "/user/login",
            "/user/checkCode",
            "/user/register",
            "/user/sendEmail",
            "/user/verifyResetPassword",
            "/user/resetPassword",
            "/user/admin/login",
            "/user/info/{userId:\\d+}",
            "/user/oauthLogin",

            // 文章相关
            "/article/listAll",
            "/article/user/list",
            "/article/user/{userId:\\d+}/statistics",
            "/article/get/{articleId:\\d+}",
            "/article/incrReadCount/{articleId:\\d+}",
            "/article/hot",

            // 评论相关
            "/comment/list",
            "/comment/reply/list",

            // 专栏相关
            "/column/list/{userId:\\d+}",
            "/column/detail/{columnId:\\d+}",

            // 相册相关
            "/album/listAll",
            "/album/get/{albumId:\\d+}",

            // 收藏相关
            "/favorite/listByUser",
            "/favorite/articles",

            // OAuth相关
            "/oauth/gitee/login",
            "/oauth/gitee/callback",
            "/oauth/github/login",
            "/oauth/github/callback",
            "/oauth/qq/login",
            "/oauth/qq/callback",

            // 友链相关
            "/link/list",

            // 其他
            "/favicon.ico"
    };

}
