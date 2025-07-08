package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
public class SecurityConstants {

    private static final String Login = "/user/login";
    private static final String CheckCode = "/user/checkCode";

    /**
     * 数组常量
     */
    public static final String[] No_Auth_Urls = {
            Login,
            CheckCode
    };
}
