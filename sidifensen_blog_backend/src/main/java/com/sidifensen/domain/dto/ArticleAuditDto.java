package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author sidifensen
 * @since 2025-08-29
 */
@Data
public class ArticleAuditDto {

    /**
     * 文章id
     */
    private Integer articleId;

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
