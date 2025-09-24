package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 新增收藏夹DTO
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddFavoriteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹名称
     */
    @NotBlank(message = "收藏夹名称不能为空")
    private String name;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;
}
