package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto{

    //用户名或邮箱
    @NotEmpty(message = "用户名或邮箱不能为空")
    private String username;
    //密码
    @NotEmpty(message = "密码不能为空")
    private String password;
    //记住我
    @NotEmpty(message = "记住我不能为空")
    private Boolean rememberMe;
    //验证码key
    @NotEmpty(message = "验证码key不能为空")
    private String checkCodeKey;
    //验证码
    @NotEmpty(message = "验证码不能为空")
    private String checkCode;

}
