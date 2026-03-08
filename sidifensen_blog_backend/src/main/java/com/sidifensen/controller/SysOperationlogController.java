package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysOperationlogQueryDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysOperationlogVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysOperationlogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志 Controller
 * 仅提供查询接口，操作日志由 AOP 切面自动记录
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/operationlog")
public class SysOperationlogController {

    @Resource
    private SysOperationlogService sysOperationlogService;

    /**
     * 查询所有操作日志（按时间倒序）
     *
     * @return 操作日志列表
     */
    @OperationLog(module = "操作日志管理", type = OperationTypeEnum.SELECT, description = "管理员获取操作日志列表")
    @PreAuthorize("hasAuthority('system:operationlog:list')")
    @GetMapping("/admin/list")
    public Result<List<SysOperationlogVo>> getOperationlogList() {
        List<SysOperationlogVo> result = sysOperationlogService.getOperationlogList();
        return Result.success(result);
    }

    /**
     * 搜索操作日志
     *
     * @param queryDto 查询条件
     * @return 操作日志列表
     */
    @OperationLog(module = "操作日志管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索操作日志")
    @PreAuthorize("hasAuthority('system:operationlog:search')")
    @PostMapping("/admin/search")
    public Result<List<SysOperationlogVo>> searchOperationlog(@RequestBody @Valid SysOperationlogQueryDto queryDto) {
        List<SysOperationlogVo> result = sysOperationlogService.searchOperationlog(queryDto);
        return Result.success(result);
    }

    /**
     * 批量删除操作日志
     *
     * @param ids 操作日志 ID 列表
     * @return 操作结果
     */
    @OperationLog(module = "操作日志管理", type = OperationTypeEnum.DELETE, description = "管理员批量删除操作日志")
    @PreAuthorize("hasAuthority('system:operationlog:delete')")
    @DeleteMapping("/admin/batch")
    public Result<Void> deleteOperationlogs(@RequestBody @NotEmpty List<Integer> ids) {
        sysOperationlogService.deleteOperationlogs(ids);
        return Result.success();
    }
}
