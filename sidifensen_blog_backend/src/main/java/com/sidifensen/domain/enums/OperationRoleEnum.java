package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作人角色枚举类
 * 用于标识操作日志中的管理员角色
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Getter
@AllArgsConstructor
public enum OperationRoleEnum {

    /**
     * 超级管理员
     */
    ADMIN("admin", "超级管理员"),

    /**
     * 查看者
     */
    VIEWER("viewer", "查看者");

    private final String role;
    private final String description;
}
