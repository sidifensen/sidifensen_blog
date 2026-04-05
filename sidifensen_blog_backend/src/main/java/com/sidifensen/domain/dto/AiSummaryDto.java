package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 50000, message = "文章内容不能超过50000字符")
    private String content;
}
