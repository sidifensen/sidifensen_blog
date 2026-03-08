package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysPermissionDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysPermissionVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysPermissionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-06
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/permission")
public class SysPermissionController {

    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 查询权限列表
     *
     * @return
     */
    @OperationLog(module = "权限管理", type = OperationTypeEnum.SELECT, description = "管理员获取权限列表")
    @PreAuthorize("hasAuthority('system:permission:list')")
    @GetMapping("list")
    public Result list() {
        List<SysPermissionVo> sysPermissionVos = sysPermissionService.listPermission();
        return Result.success(sysPermissionVos);
    }

    /**
     * 新增权限
     *
     * @param sysPermissionDto 权限信息
     * @return
     */
    @OperationLog(module = "权限管理", type = OperationTypeEnum.INSERT, description = "管理员新增权限")
    @PreAuthorize("hasAuthority('system:permission:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysPermissionDto sysPermissionDto) {
        sysPermissionService.add(sysPermissionDto);
        return Result.success();
    }

    /**
     * 修改权限
     *
     * @param sysPermissionDto 包含更新信息的权限数据
     * @return
     */
    @OperationLog(module = "权限管理", type = OperationTypeEnum.UPDATE, description = "管理员修改权限")
    @PreAuthorize("hasAuthority('system:permission:update')")
    @PutMapping("update")
    public Result update(@RequestBody @Valid SysPermissionDto sysPermissionDto) {
        sysPermissionService.update(sysPermissionDto);
        return Result.success();
    }

    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     * @return
     */
    @OperationLog(module = "权限管理", type = OperationTypeEnum.DELETE, description = "管理员删除权限")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    @DeleteMapping("{permissionId}")
    public Result delete(@PathVariable @NotEmpty Integer permissionId) {
        sysPermissionService.delete(permissionId);
        return Result.success();
    }

    /**
     * 根据权限描述,权限标识,菜单di查找权限
     *
     * @param sysPermissionDto 权限信息
     * @return
     */
    @OperationLog(module = "权限管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索权限")
    @PreAuthorize("hasAuthority('system:permission:search')")
    @PostMapping("search")
    public Result search(@RequestBody @Valid SysPermissionDto sysPermissionDto) {
        List<SysPermissionVo> sysPermissionVos = sysPermissionService.search(sysPermissionDto);
        return Result.success(sysPermissionVos);
    }

}
