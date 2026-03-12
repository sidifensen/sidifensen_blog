package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付业务类型枚举。
 */
@Getter
@AllArgsConstructor
public enum PayBizTypeEnum {

    VIP_SUBSCRIPTION("VIP_SUBSCRIPTION", "VIP会员开通");

    private final String code;
    private final String description;
}
