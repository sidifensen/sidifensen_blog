package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 管理端首页完整数据聚合 VO
 * 一次性返回所有 Dashboard 数据，减少前端请求次数
 *
 * @author sidifensen
 * @since 2026-04-10
 */
@Data
@Accessors(chain = true)
public class DashboardAllVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 基础统计（用户、文章、访问量）
     */
    private DashboardStatisticsVo statistics;

    /**
     * 待审核数量（文章、评论、图片）
     */
    private ExamineCountVo examineCount;

    /**
     * VIP 统计数据
     */
    private VipStatisticsVo vipStatistics;

    /**
     * 近7天文章和用户增长趋势
     */
    private List<WeeklyTrendVo> weeklyTrend;

    /**
     * 近7天互动趋势（评论数、点赞数、收藏数）
     */
    private List<InteractionTrendVo> interactionTrend;

}
