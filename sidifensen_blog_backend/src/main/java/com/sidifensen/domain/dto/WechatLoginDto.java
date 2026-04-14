package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信小程序登录请求 DTO
 *
 * @author sidifensen
 * @since 2026-03-23
 */
@Data
@Accessors(chain = true)
public class WechatLoginDto {

    /**
     * 微信小程序登录凭证 code
     */
    @NotBlank(message = "code 不能为空")
    private String code;

    /**
     * 微信用户昵称（可选，用于设置新用户昵称）
     */
    private String nickname;

    /**
     * 微信用户头像 URL（可选，用于设置新用户头像）
     */
    private String avatar;
}
