package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.BlacklistAddDto;
import com.sidifensen.domain.dto.BlacklistSearchDto;
import com.sidifensen.domain.dto.BlacklistUpdateDto;
import com.sidifensen.domain.entity.SysBlacklist;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.SysBlacklistService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-10-02
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/blacklist")
public class SysBlacklistController {

    @Resource
    private SysBlacklistService sysBlacklistService;

    /**
     * 管理员获取黑名单列表
     *
     * @return 黑名单列表
     */
    @PreAuthorize("hasAuthority('system:blacklist:list')")
    @GetMapping("/admin/list")
    public Result<List<SysBlacklist>> adminGetBlacklistList() {
        List<SysBlacklist> blacklistList = sysBlacklistService.adminGetBlacklistList();
        return Result.success(blacklistList);
    }

    /**
     * 管理员批量新增黑名单用户
     *
     * @param blacklistAddDto 黑名单新增信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:blacklist:add')")
    @PostMapping("/admin/add")
    public Result<Void> adminAddBlacklist(@RequestBody @Valid BlacklistAddDto blacklistAddDto) {
        sysBlacklistService.adminAddBlacklist(blacklistAddDto);
        return Result.success();
    }

    /**
     * 管理员搜索黑名单
     *
     * @param blacklistSearchDto 搜索条件
     * @return 黑名单列表
     */
    @PreAuthorize("hasAuthority('system:blacklist:search')")
    @PostMapping("/admin/search")
    public Result<List<SysBlacklist>> adminSearchBlacklist(@RequestBody @Valid BlacklistSearchDto blacklistSearchDto) {
        List<SysBlacklist> blacklistList = sysBlacklistService.adminSearchBlacklist(blacklistSearchDto);
        return Result.success(blacklistList);
    }

    /**
     * 管理员修改黑名单
     *
     * @param blacklistUpdateDto 黑名单修改信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:blacklist:update')")
    @PutMapping("/admin/update")
    public Result<Void> adminUpdateBlacklist(@RequestBody @Valid BlacklistUpdateDto blacklistUpdateDto) {
        sysBlacklistService.adminUpdateBlacklist(blacklistUpdateDto);
        return Result.success();
    }

    /**
     * 管理员批量删除黑名单
     *
     * @param blacklistIds 黑名单ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('system:blacklist:delete')")
    @DeleteMapping("/admin/delete")
    public Result<Void> adminDeleteBlacklist(@RequestBody @NotNull(message = "黑名单ID列表不能为空") List<Integer> blacklistIds) {
        sysBlacklistService.adminDeleteBlacklist(blacklistIds);
        return Result.success();
    }
}

