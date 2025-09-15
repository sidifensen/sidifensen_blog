package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 评论数据传输对象
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论id（编辑时需要）
     */
    private Integer id;

    /**
     * 父级评论id（0表示顶级评论）
     */
    @NotNull(message = "父级评论id不能为空")
    @Min(value = 0, message = "父级评论id不能小于0")
    private Integer parentId;

    /**
     * 文章id
     */
    @NotNull(message = "文章id不能为空")
    @Min(value = 1, message = "文章id必须大于0")
    private Integer articleId;

    /**
     * 回复的用户id（回复评论时需要）
     */
    private Integer replyUserId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

}
