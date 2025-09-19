package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 评论审核DTO
 *
 * @author sidifensen
 * @since 2025-09-19
 */
@Data
public class CommentAuditDto {

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 审核未通过的原因
     */
    private String examineReason;

}
