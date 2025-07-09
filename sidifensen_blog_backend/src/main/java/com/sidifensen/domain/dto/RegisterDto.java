package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
public class RegisterDto implements Serializable {

    // 用户名
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    private String username;

    // 密码
    @Length(min = 6, max = 20)
    private String password;

    // 邮箱
    @Email
    @Length(min = 4)
    private String email;

    // 邮箱验证码
    @Length(max = 6, min = 6)
    private String emailCheckCode;

}
