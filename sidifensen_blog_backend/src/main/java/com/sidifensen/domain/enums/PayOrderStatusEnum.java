package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付订单状态枚举。
 */
@Getter
@AllArgsConstructor
public enum PayOrderStatusEnum {

    CREATED("CREATED", "已创建"),
    PAYING("PAYING", "待支付"),
    PAID("PAID", "已支付"),
    CLOSED("CLOSED", "已关闭"),
    FAILED("FAILED", "失败");

    private final String code;
    private final String description;
}
