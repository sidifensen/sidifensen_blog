package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


/**
 * @author sidifensen
 * @since 2025-07-09
 */
@Data
public class EmailDto {

    // 邮箱
    @Email
    private String email;

    // 发送邮箱的类型
    @Pattern(regexp = "(register|reset|resetEmail)",message = "邮箱类型错误")
    private String type;
}
