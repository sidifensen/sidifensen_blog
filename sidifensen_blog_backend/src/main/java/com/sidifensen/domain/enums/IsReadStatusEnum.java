package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阅读状态枚举
 */
@Getter
@AllArgsConstructor
public enum IsReadStatusEnum {

    UNREAD(0, "未读"),
    READ(1, "已读");

    private final Integer code;
    private final String description;

}
