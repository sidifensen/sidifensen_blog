package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShowStatusEnum {

    PUBLIC(0, "公开"),
    PRIVATE(1, "私密");

    private final Integer code;
    private final String description;

}
