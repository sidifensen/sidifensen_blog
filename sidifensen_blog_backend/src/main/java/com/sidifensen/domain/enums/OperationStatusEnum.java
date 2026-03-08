package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作状态枚举类
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Getter
@AllArgsConstructor
public enum OperationStatusEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    FAILURE(1, "失败"),

    /**
     * 异常
     */
    ERROR(2, "异常");

    private final Integer code;
    private final String description;
}
