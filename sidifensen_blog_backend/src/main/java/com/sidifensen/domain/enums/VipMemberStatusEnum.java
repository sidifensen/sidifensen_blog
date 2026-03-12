package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VIP 会员状态枚举。
 */
@Getter
@AllArgsConstructor
public enum VipMemberStatusEnum {

    NONE("NONE", "未开通"),
    ACTIVE("ACTIVE", "有效"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String description;
}
