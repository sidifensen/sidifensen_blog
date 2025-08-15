package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExamineStatusEnum {

    WAIT(0, "待审核"),
    PASS(1, "审核通过"),
    NO_PASS(2, "审核未通过");

    private final Integer code;
    private final String description;

}
