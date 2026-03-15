package com.sidifensen.service;

import com.sidifensen.domain.vo.DashboardStatisticsVo;

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

}
