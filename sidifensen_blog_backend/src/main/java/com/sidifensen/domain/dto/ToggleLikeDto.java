package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 点赞/取消点赞请求DTO
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ToggleLikeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞类型 0-文章 1-评论
     */
    @NotNull(message = "点赞类型不能为空")
    private Integer type;

    /**
     * 点赞类型id
     */
    @NotNull(message = "点赞类型id不能为空")
    private Integer typeId;
}
