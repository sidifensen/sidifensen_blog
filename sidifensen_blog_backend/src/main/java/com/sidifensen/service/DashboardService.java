package com.sidifensen.service;

import com.sidifensen.domain.vo.ContentActivityVo;
import com.sidifensen.domain.vo.DashboardStatisticsVo;
import com.sidifensen.domain.vo.ExamineCountVo;
import com.sidifensen.domain.vo.InteractionTrendVo;
import com.sidifensen.domain.vo.UserDistributionVo;
import com.sidifensen.domain.vo.VipStatisticsVo;
import com.sidifensen.domain.vo.VisitorTrendVo;
import com.sidifensen.domain.vo.WeeklyTrendVo;

import java.util.List;

/**
 * 管理端首页 Dashboard 统计服务
 *
 * @author sidifensen
 * @since 2026-03-15
 */
public interface DashboardService {

    /**
     * 获取管理端首页统计数据（带 Redis 缓存，默认 7 天趋势）
     *
     * @return Dashboard 统计数据
     */
    DashboardStatisticsVo getDashboardStatistics();

    /**
     * 获取管理端首页统计数据（自定义趋势天数）
     *
     * @param trendDays 趋势天数（7/14/30）
     * @return Dashboard 统计数据
     */
    DashboardStatisticsVo getDashboardStatistics(Integer trendDays);

    /**
     * 刷新 Dashboard 缓存
     * 当数据发生变化时（如新增用户、文章等）调用此方法
     */
    void refreshDashboardCache();

    /**
     * 获取近7天文章和用户增长趋势
     *
     * @return 周趋势数据列表
     */
    List<WeeklyTrendVo> getWeeklyTrend();

    /**
     * 获取用户角色分布
     *
     * @return 用户分布数据列表
     */
    List<UserDistributionVo> getUserDistribution();

    /**
     * 获取内容模块活跃度
     *
     * @return 内容活跃度数据列表
     */
    List<ContentActivityVo> getContentActivity();

    /**
     * 获取待审核数量统计
     *
     * @return 待审核数量（文章、评论、图片）
     */
    ExamineCountVo getExamineCount();

    /**
     * 获取 VIP 统计数据
     *
     * @return VIP 统计（VIP总数、近7天新增VIP）
     */
    VipStatisticsVo getVipStatistics();

    /**
     * 获取访客趋势数据
     *
     * @param days 天数（7/14/30）
     * @return 访客趋势数据列表
     */
    List<VisitorTrendVo> getVisitorTrendByDays(Integer days);

    /**
     * 获取近7天互动趋势（评论数、点赞数、收藏数）
     *
     * @return 互动趋势数据列表
     */
    List<InteractionTrendVo> getInteractionTrend();

}
