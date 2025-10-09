package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 访客统计 VO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
@Accessors(chain = true)
public class VisitorStatisticsVo {

    /**
     * 今日访客数
     */
    private Long todayCount;

    /**
     * 昨日访客数
     */
    private Long yesterdayCount;

    /**
     * 本周访客数
     */
    private Long weekCount;

    /**
     * 本月访客数
     */
    private Long monthCount;

    /**
     * 总访客数
     */
    private Long totalCount;

    /**
     * PC端访客数
     */
    private Long pcCount;

    /**
     * 移动端访客数
     */
    private Long mobileCount;
}

