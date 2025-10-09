package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 访客趋势 VO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
@Accessors(chain = true)
public class VisitorTrendVo {

    /**
     * 日期（格式：2024-10-06）
     */
    private String date;

    /**
     * 访客数
     */
    private Long count;
}

