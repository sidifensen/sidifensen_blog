package com.sidifensen.domain.enums;

import lombok.Getter;

/**
 * 私信消息类型枚举
 */
@Getter
public enum PrivateMessageTypeEnum {
    TEXT(1, "文本消息"),
    IMAGE(2, "图片消息");

    private final Integer code;
    private final String desc;

    PrivateMessageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
