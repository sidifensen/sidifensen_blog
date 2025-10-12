package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 提取摘要请求 DTO
 */
@Data
public class AiSummaryDto {

    /**
     * 文章内容（HTML 格式）
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;
}
