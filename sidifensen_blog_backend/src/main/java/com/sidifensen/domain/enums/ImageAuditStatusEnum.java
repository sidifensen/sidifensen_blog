package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author sidifensen
 * @since 2025-08-14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ImageAuditStatusEnum {

    PASS(0, "审核通过"),
    REJECT(1, "审核不通过"),
    MANUAL_REVIEW(2, "人工审核");

    private int code;
    private String desc;

}
