package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
public class SecurityConstants {

    private static final String Login = "/user/login";
    private static final String CheckCode = "/user/checkCode";
    private static final String Register = "/user/register";
    private static final String SendEmail = "/user/sendEmail";
    private static final String VerifyReset = "/user/verifyReset";
    private static final String ResetPassword = "/user/resetPassword";

    /**
     * 数组常量
     */
    public static final String[] No_Auth_Urls = {
            Login,
            CheckCode,
            Register,
            SendEmail,
            VerifyReset,
            ResetPassword
    };
}
