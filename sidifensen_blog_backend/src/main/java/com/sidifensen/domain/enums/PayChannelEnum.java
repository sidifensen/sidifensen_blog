package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道枚举。
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

    ALIPAY("ALIPAY", "支付宝");

    private final String code;
    private final String description;
}
