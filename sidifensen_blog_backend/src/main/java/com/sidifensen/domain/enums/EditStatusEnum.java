package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EditStatusEnum {

    PUBLISHED(0, "已发布"),
    DRAFT(1, "草稿箱"),
    RECYCLE(2, "回收站");

    private final Integer code;
    private final String description;

}
