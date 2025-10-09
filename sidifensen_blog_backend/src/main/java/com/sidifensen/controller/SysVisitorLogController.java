package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysVisitorLogQueryDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysVisitorLogVo;
import com.sidifensen.domain.vo.VisitorStatisticsVo;
import com.sidifensen.domain.vo.VisitorTrendVo;
import com.sidifensen.service.SysVisitorLogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-10-06
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/visitorLog")
public class SysVisitorLogController {

    @Resource
    private SysVisitorLogService sysVisitorLogService;

    /**
     * 查询所有访客日志（按时间倒序）
     *
     * @return 访客日志列表
     */
    @PreAuthorize("hasAuthority('system:visitorLog:list')")
    @GetMapping("/admin/list")
    public Result<List<SysVisitorLogVo>> getVisitorLogList() {
        List<SysVisitorLogVo> result = sysVisitorLogService.getVisitorLogList();
        return Result.success(result);
    }

    /**
     * 搜索访客日志
     *
     * @param queryDto 查询条件
     * @return 访客日志列表
     */
    @PreAuthorize("hasAuthority('system:visitorLog:search')")
    @PostMapping("/admin/search")
    public Result<List<SysVisitorLogVo>> searchVisitorLog(@RequestBody @Valid SysVisitorLogQueryDto queryDto) {
        List<SysVisitorLogVo> result = sysVisitorLogService.searchVisitorLog(queryDto);
        return Result.success(result);
    }

    /**
     * 批量删除访客日志
     *
     * @param ids 访客日志ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:visitorLog:delete')")
    @DeleteMapping("/admin/batch")
    public Result<Void> deleteVisitorLogs(@RequestBody @NotEmpty List<Integer> ids) {
        sysVisitorLogService.deleteVisitorLogs(ids);
        return Result.success();
    }

    /**
     * 获取访客统计数据
     * 仅管理员可访问
     */
    @PreAuthorize("hasAuthority('system:visitorLog:list')")
    @GetMapping("/statistics")
    public Result<VisitorStatisticsVo> getStatistics() {
        VisitorStatisticsVo statistics = sysVisitorLogService.getVisitorStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取访客趋势（最近N天）
     * 仅管理员可访问
     *
     * @param days 天数，默认7天
     */
    @PreAuthorize("hasAuthority('system:visitorLog:list')")
    @GetMapping("/trend")
    public Result<List<VisitorTrendVo>> getTrend(@RequestParam(defaultValue = "7") @NotNull(message = "天数不能为空") Integer days) {
        List<VisitorTrendVo> trend = sysVisitorLogService.getVisitorTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取今日访问量
     */
    @PreAuthorize("hasAuthority('system:visitorLog:list')")
    @GetMapping("/today/count")
    public Result<Long> getTodayCount() {
        Long count = sysVisitorLogService.getTodayVisitorCount();
        return Result.success(count);
    }

}

