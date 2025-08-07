package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuEnum {

    MENU_STATUS_NORMAL(0, "状态：正常"),
    MENU_STATUS_DISABLE(1, "状态：禁用");

    // 类型
    private final Integer status;
    // 描述
    private final String desc;
}