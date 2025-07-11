package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ResetPasswordDto {

    // 邮箱
    @Email
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    // 密码
    @Length(min = 6, max = 20)
    private String password;

}
