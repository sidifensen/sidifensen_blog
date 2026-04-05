package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ColumnDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专栏id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 专栏名称
     */
    @NotBlank(message = "专栏名称不能为空")
    @Size(max = 30, message = "专栏名称不能超过30个字")
    private String name;

    /**
     * 专栏描述
     */
    @Size(max = 100, message = "专栏描述不能超过100个字")
    private String description;

    /**
     * 专栏封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;

}
