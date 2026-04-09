package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminLoginDto {

    //用户名或邮箱
    @NotEmpty(message = "用户名不能为空")
    private String username;
    //密码
    @NotEmpty(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*._-]+$", message = "密码只能包含英文、数字和特殊字符(!@#$%^&*._-)")
    private String password;
    //记住我
    @NotNull(message = "记住我不能为空")
    private Boolean rememberMe;
    //验证码key
    @NotEmpty(message = "验证码key不能为空")
    private String checkCodeKey;
    //验证码
    @NotEmpty(message = "验证码不能为空")
    private String checkCode;

}
