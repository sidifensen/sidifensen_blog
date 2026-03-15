package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 管理端首页控制台统计数据 VO
 *
 * @author sidifensen
 * @since 2026-03-15
 */
@Data
@Accessors(chain = true)
public class DashboardStatisticsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户总数
     */
    private Long userTotalCount;

    /**
     * 文章统计数据
     */
    private ArticleStatisticsVo articleStatistics;

    /**
     * 今日访问量
     */
    private Long todayVisits;

    /**
     * 总访问量
     */
    private Long totalVisits;

    /**
     * 访客趋势数据（最近 N 天）
     */
    private List<VisitorTrendVo> visitorTrend;

}
