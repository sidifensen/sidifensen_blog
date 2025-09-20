package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 专栏筛选条件 DTO
 * 
 * @author sidifensen
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ColumnFilterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 年份筛选
     */
    private Integer year;

    /**
     * 月份筛选
     */
    private Integer month;

    /**
     * 展示状态筛选 0-公开 1-私密
     */
    private Integer showStatus;

}
