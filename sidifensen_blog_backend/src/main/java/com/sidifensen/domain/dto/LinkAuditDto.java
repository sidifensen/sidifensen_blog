package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 友链审核DTO
 *
 * @author sidifensen
 * @since 2025-09-28
 */
@Data
public class LinkAuditDto {

    /**
     * 友链id
     */
    @NotNull
    private Integer linkId;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

}
