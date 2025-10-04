package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.BlacklistAddDto;
import com.sidifensen.domain.dto.BlacklistSearchDto;
import com.sidifensen.domain.dto.BlacklistUpdateDto;
import com.sidifensen.domain.entity.Blacklist;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.BlacklistService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-10-02
 */
@RateLimit(30)
@RestController
@RequestMapping("/blacklist")
public class BlacklistController {

    @Resource
    private BlacklistService blacklistService;

    /**
     * 管理员获取黑名单列表
     *
     * @return 黑名单列表
     */
    @PreAuthorize("hasAuthority('blacklist:list')")
    @GetMapping("/admin/list")
    public Result<List<Blacklist>> adminGetBlacklistList() {
        List<Blacklist> blacklistList = blacklistService.adminGetBlacklistList();
        return Result.success(blacklistList);
    }

    /**
     * 管理员批量新增黑名单用户
     *
     * @param blacklistAddDto 黑名单新增信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('blacklist:add')")
    @PostMapping("/admin/add")
    public Result<Void> adminAddBlacklist(@RequestBody @Valid BlacklistAddDto blacklistAddDto) {
        blacklistService.adminAddBlacklist(blacklistAddDto);
        return Result.success();
    }

    /**
     * 管理员搜索黑名单
     *
     * @param blacklistSearchDto 搜索条件
     * @return 黑名单列表
     */
    @PreAuthorize("hasAuthority('blacklist:search')")
    @PostMapping("/admin/search")
    public Result<List<Blacklist>> adminSearchBlacklist(@RequestBody BlacklistSearchDto blacklistSearchDto) {
        List<Blacklist> blacklistList = blacklistService.adminSearchBlacklist(blacklistSearchDto);
        return Result.success(blacklistList);
    }

    /**
     * 管理员修改黑名单
     *
     * @param blacklistUpdateDto 黑名单修改信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('blacklist:update')")
    @PutMapping("/admin/update")
    public Result<Void> adminUpdateBlacklist(@RequestBody @Valid BlacklistUpdateDto blacklistUpdateDto) {
        blacklistService.adminUpdateBlacklist(blacklistUpdateDto);
        return Result.success();
    }

    /**
     * 管理员批量删除黑名单
     *
     * @param blacklistIds 黑名单ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('blacklist:delete')")
    @DeleteMapping("/admin/delete")
    public Result<Void> adminDeleteBlacklist(@RequestBody List<Integer> blacklistIds) {
        blacklistService.adminDeleteBlacklist(blacklistIds);
        return Result.success();
    }
}
