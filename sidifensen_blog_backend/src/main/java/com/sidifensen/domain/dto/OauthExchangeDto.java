package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * OAuth 票据兑换 DTO
 *
 * @author sidifensen
 * @since 2026-03-13
 */
@Data
public class OauthExchangeDto {

    /**
     * 一次性 OAuth 票据
     */
    @NotEmpty(message = "OAuth票据不能为空")
    private String ticket;

}
