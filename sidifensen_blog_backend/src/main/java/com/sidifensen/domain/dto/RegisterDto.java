package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterDto {

    // 用户名
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名必须为数字、字母或下划线")
    @Length(min = 4, max = 20, message = "用户名长度必须在4-20位之间")
    private String username;

    // 密码
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码只能包含英文和数字")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    // 邮箱
    @Email
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    // 邮箱验证码
    @Length(max = 6, min = 6, message = "验证码长度必须为6位")
    @NotEmpty(message = "邮箱验证码不能为空")
    private String emailCheckCode;

}
