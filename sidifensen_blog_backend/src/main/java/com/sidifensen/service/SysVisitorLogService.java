package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysVisitorLogQueryDto;
import com.sidifensen.domain.entity.SysVisitorLog;
import com.sidifensen.domain.vo.SysVisitorLogVo;
import com.sidifensen.domain.vo.VisitorStatisticsVo;
import com.sidifensen.domain.vo.VisitorTrendVo;

import java.util.List;

/**
 * 访客记录 Service 接口
 *
 * @author sidifensen
 * @since 2025-10-06
 */
public interface SysVisitorLogService extends IService<SysVisitorLog> {

    /**
     * 插入访客记录
     *
     * @param sysVisitorLog 访客信息
     */
    void insertVisitorRecord(SysVisitorLog sysVisitorLog);

    /**
     * 查询所有访客日志（按时间倒序）
     *
     * @return 访客日志列表
     */
    List<SysVisitorLogVo> getVisitorLogList();

    /**
     * 搜索访客日志
     *
     * @param queryDto 查询条件
     * @return 访客日志列表
     */
    List<SysVisitorLogVo> searchVisitorLog(SysVisitorLogQueryDto queryDto);

    /**
     * 批量删除访客日志
     *
     * @param ids 访客日志ID列表
     */
    void deleteVisitorLogs(List<Integer> ids);

    /**
     * 获取访客统计数据
     *
     * @return 访客统计VO
     */
    VisitorStatisticsVo getVisitorStatistics();

    /**
     * 获取最近N天的访客趋势
     *
     * @param days 天数
     * @return 访客趋势列表
     */
    List<VisitorTrendVo> getVisitorTrend(Integer days);

    /**
     * 获取今日访问量（实时）
     * 优先从 Redis 获取，Redis 不可用时降级到数据库查询
     *
     * @return 今日访问量
     */
    Long getTodayVisitorCount();

    /**
     * 获取总访问量
     *
     * @return 总访问量
     */
    Long getTotalVisitorCount();

}
