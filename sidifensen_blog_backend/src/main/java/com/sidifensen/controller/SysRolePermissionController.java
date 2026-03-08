package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysRolePermissionDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysRolePermissionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/role-permission")
public class SysRolePermissionController {

    @Resource
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 将权限授权给角色
     *
     * @param sysRolePermissionDto 角色权限信息
     * @return
     */
    @OperationLog(module = "角色权限管理", type = OperationTypeEnum.ASSIGN, description = "管理员给角色分配权限")
    @PreAuthorize("hasAuthority('system:role:permission:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysRolePermissionDto sysRolePermissionDto) {
        sysRolePermissionService.add(sysRolePermissionDto);
        return Result.success();
    }

    /**
     * 获取拥有当前权限的角色列表
     *
     * @param permissionId 权限ID
     * @return
     */
    @OperationLog(module = "角色权限管理", type = OperationTypeEnum.SELECT, description = "管理员获取拥有指定权限的角色列表")
    @PreAuthorize("hasAuthority('system:role:permission:get')")
    @GetMapping("{permissionId}")
    public Result getRoles(@PathVariable @NotNull(message = "权限ID不能为空") Integer permissionId) {
        List<SysRoleVo> sysRoleVos = sysRolePermissionService.getRoles(permissionId);
        return Result.success(sysRoleVos);
    }


    /**
     * 将权限批量授权给角色
     *
     * @param sysRolePermissionDto 角色权限信息
     * @return
     */
    @OperationLog(module = "角色权限管理", type = OperationTypeEnum.ASSIGN, description = "管理员批量给角色分配权限")
    @PreAuthorize("hasAuthority('system:role:permission:addBatch')")
    @PostMapping("addBatch")
    public Result addBatch(@RequestBody @Valid SysRolePermissionDto sysRolePermissionDto) {
        sysRolePermissionService.addBatch(sysRolePermissionDto);
        return Result.success();
    }

}
