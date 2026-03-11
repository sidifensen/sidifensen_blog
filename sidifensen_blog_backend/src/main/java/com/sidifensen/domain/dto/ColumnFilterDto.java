package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

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

    /**
     * 审核状态筛选 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 用户ID筛选
     */
    private Integer userId;

}
