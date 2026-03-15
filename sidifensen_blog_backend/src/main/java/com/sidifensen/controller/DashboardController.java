package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.DashboardStatisticsVo;
import com.sidifensen.service.DashboardService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端首页 Dashboard 控制器
 *
 * @author sidifensen
 * @since 2026-03-15
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取管理端首页统计数据
     * 仅管理员可访问
     *
     * @param trendDays 访客趋势天数，默认 7 天（可选：7/14/30）
     * @return Dashboard 统计数据
     */
    @OperationLog(module = "Dashboard", type = OperationTypeEnum.GET, description = "管理员获取首页统计数据")
    @PreAuthorize("hasAuthority('system:dashboard:list')")
    @GetMapping("/statistics")
    public Result<DashboardStatisticsVo> getStatistics(
            @RequestParam(defaultValue = "7") @NotNull(message = "趋势天数不能为空")
            @Min(value = 1, message = "趋势天数不能小于 1")
            @Max(value = 365, message = "趋势天数不能超过 365") Integer trendDays) {
        DashboardStatisticsVo statistics = dashboardService.getDashboardStatistics(trendDays);
        return Result.success(statistics);
    }

    /**
     * 刷新 Dashboard 缓存
     * 仅管理员可访问
     *
     * @return 操作结果
     */
    @OperationLog(module = "Dashboard", type = OperationTypeEnum.OTHER, description = "管理员刷新首页缓存")
    @PreAuthorize("hasAuthority('system:dashboard:refresh')")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        dashboardService.refreshDashboardCache();
        return Result.success();
    }

}
