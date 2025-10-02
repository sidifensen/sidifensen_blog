package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 校验邮箱验证码DTO
 * @author sidifensen
 */
@Data
public class VerifyEmailDto {

    // 原邮箱
    @Email(message = "邮箱格式不正确")
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    // 邮箱验证码
    @NotEmpty(message = "验证码不能为空")
    @Length(max = 6, min = 6, message = "验证码长度必须为6位")
    private String emailCheckCode;

}

