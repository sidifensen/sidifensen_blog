package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 专栏文章排序DTO
 *
 * @author sidifensen
 * @since 2025-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ColumnArticleSortDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Integer articleId;

    /**
     * 新的排序值
     */
    @NotNull(message = "排序值不能为空")
    private Integer sort;
}
