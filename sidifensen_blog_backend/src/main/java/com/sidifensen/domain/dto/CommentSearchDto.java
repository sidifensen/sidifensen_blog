package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论搜索数据传输对象
 *
 * @author sidifensen
 * @since 2025-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommentSearchDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审核状态（可选）0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 用户id（可选）
     */
    @Min(value = 1, message = "用户id必须大于0")
    private Integer userId;

    /**
     * 文章id（可选）
     */
    @Min(value = 1, message = "文章id必须大于0")
    private Integer articleId;

    /**
     * 关键词搜索（可选）
     */
    private String keyword;

    /**
     * 创建时间开始（用于搜索）
     */
    private Date createTimeStart;

    /**
     * 创建时间结束（用于搜索）
     */
    private Date createTimeEnd;

}
