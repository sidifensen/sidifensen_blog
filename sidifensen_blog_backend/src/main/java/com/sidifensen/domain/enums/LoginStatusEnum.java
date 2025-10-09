package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录状态枚举
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Getter
@AllArgsConstructor
public enum LoginStatusEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 失败
     */
    FAIL(1, "失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String description;
}

