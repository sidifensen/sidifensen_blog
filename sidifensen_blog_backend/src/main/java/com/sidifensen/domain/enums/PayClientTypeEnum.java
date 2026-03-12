package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付发起端类型枚举。
 */
@Getter
@AllArgsConstructor
public enum PayClientTypeEnum {

    PC("PC", "电脑网站"),
    H5("H5", "手机网站");

    private final String code;
    private final String description;

    /**
     * 将前端传入的客户端类型编码转换成枚举。
     */
    public static PayClientTypeEnum fromCode(String code) {
        for (PayClientTypeEnum value : values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
