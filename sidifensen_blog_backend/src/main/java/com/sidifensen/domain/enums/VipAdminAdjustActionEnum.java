package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理端手动调整会员时使用的动作类型。
 */
@Getter
@AllArgsConstructor
public enum VipAdminAdjustActionEnum {

    ACTIVATE("ACTIVATE", "立即开通"),
    EXTEND("EXTEND", "延期续期"),
    EXPIRE_NOW("EXPIRE_NOW", "立即失效");

    private final String code;

    private final String description;

    /**
     * 将前端动作编码转成后台枚举，避免控制器里散落硬编码判断。
     */
    public static VipAdminAdjustActionEnum fromCode(String code) {
        for (VipAdminAdjustActionEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
