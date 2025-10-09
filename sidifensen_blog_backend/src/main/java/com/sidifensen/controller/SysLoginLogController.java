package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysLoginLogQueryDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysLoginLogVo;
import com.sidifensen.service.SysLoginLogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
@RequestMapping("/loginLog")
public class SysLoginLogController {

    @Resource
    private SysLoginLogService sysLoginLogService;

    /**
     * 查询所有登录日志（按时间倒序）
     *
     * @return 登录日志列表
     */
    @PreAuthorize("hasAuthority('system:loginLog:list')")
    @GetMapping("/admin/list")
    public Result<List<SysLoginLogVo>> getLoginLogList() {
        List<SysLoginLogVo> result = sysLoginLogService.getLoginLogList();
        return Result.success(result);
    }

    /**
     * 搜索登录日志
     *
     * @param queryDto 查询条件
     * @return 登录日志列表
     */
    @PreAuthorize("hasAuthority('system:loginLog:search')")
    @PostMapping("/admin/search")
    public Result<List<SysLoginLogVo>> searchLoginLog(@RequestBody @Valid SysLoginLogQueryDto queryDto) {
        List<SysLoginLogVo> result = sysLoginLogService.searchLoginLog(queryDto);
        return Result.success(result);
    }

    /**
     * 批量删除登录日志
     *
     * @param ids 登录日志ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:loginLog:delete')")
    @DeleteMapping("/admin/batch")
    public Result<Void> deleteLoginLogs(@RequestBody @NotEmpty List<Integer> ids) {
        sysLoginLogService.deleteLoginLogs(ids);
        return Result.success();
    }
}
