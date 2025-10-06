package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分类排序 DTO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CategorySortDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String category;

    /**
     * 新的排序值
     */
    private Integer newSort;

}

