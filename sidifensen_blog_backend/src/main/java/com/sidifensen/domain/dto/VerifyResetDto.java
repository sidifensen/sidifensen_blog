package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class VerifyResetDto{

    // 邮箱
    @Email
    @Length(min = 4)
    private String email;

    // 邮箱验证码
    @Length(max = 6, min = 6)
    private String emailCheckCode;

}
