package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 评论筛选条件 DTO
 * 
 * @author sidifensen
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommentFilterDto implements Serializable {

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

}
