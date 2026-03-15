package com.sidifensen.service.impl;

import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.vo.DashboardStatisticsVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ArticleService;
import com.sidifensen.service.DashboardService;
import com.sidifensen.service.SysVisitorLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理端首页 Dashboard 统计服务实现
 *
 * @author sidifensen
 * @since 2026-03-15
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ArticleService articleService;

    @Resource
    private SysVisitorLogService visitorLogService;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public DashboardStatisticsVo getDashboardStatistics() {
        return getDashboardStatistics(7);
    }

    @Override
    public DashboardStatisticsVo getDashboardStatistics(Integer trendDays) {
        // 1. 先尝试从 Redis 缓存获取
        Object cached = redisComponent.getDashboardStatistics();
        if (cached instanceof DashboardStatisticsVo) {
            DashboardStatisticsVo cachedVo = (DashboardStatisticsVo) cached;
            // 如果缓存中的趋势天数与请求的不一致，需要重新获取趋势数据
            if (cachedVo.getVisitorTrend() != null && cachedVo.getVisitorTrend().size() == trendDays) {
                return cachedVo;
            }
        }

        // 2. Redis 缓存未命中或趋势天数不匹配，从数据库查询并组装数据
        DashboardStatisticsVo result = new DashboardStatisticsVo();

        // 2.1 获取用户总数
        result.setUserTotalCount(getUserTotalCount());

        // 2.2 获取文章统计数据
        result.setArticleStatistics(articleService.getAdminStatistics());

        // 2.3 获取今日访问量和总访问量
        result.setTodayVisits(visitorLogService.getTodayVisitorCount());
        result.setTotalVisits(visitorLogService.getTotalVisitorCount());

        // 2.4 获取访客趋势数据
        result.setVisitorTrend(visitorLogService.getVisitorTrend(trendDays));

        // 3. 写入 Redis 缓存
        redisComponent.setDashboardStatistics(result);

        return result;
    }

    @Override
    public void refreshDashboardCache() {
        redisComponent.removeDashboardStatistics();
    }

    /**
     * 获取用户总数
     *
     * @return 用户总数
     */
    private Long getUserTotalCount() {
        try {
            return (long) sysUserMapper.selectCount(null);
        } catch (Exception e) {
            log.error("获取用户总数失败", e);
            throw new BlogException(BlogConstants.SystemInternalError);
        }
    }

}
