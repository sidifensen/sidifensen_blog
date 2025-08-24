package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisibleRangeEnum {

    ALL(0, "全部可见"),
    ME(1, "仅我可见"),
    FANS(2, "粉丝可见"),
    VIP(3, "vip可见");

    private final Integer code;
    private final String description;

}
