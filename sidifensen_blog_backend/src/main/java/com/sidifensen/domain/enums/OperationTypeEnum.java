package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举类
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Getter
@AllArgsConstructor
public enum OperationTypeEnum {

    /**
     * 获取
     */
    GET("获取"),

    /**
     * 新增
     */
    INSERT("新增"),

    /**
     * 修改
     */
    UPDATE("修改"),

    /**
     * 删除
     */
    DELETE("删除"),

    /**
     * 查询
     */
    SELECT("查询"),

    /**
     * 搜索
     */
    SEARCH("搜索"),

    /**
     * 审核
     */
    AUDIT("审核"),

    /**
     * 分配
     */
    ASSIGN("分配"),

    /**
     * 其他
     */
    OTHER("其他")
    ;

    private final String description;
}
