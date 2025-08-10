package com.sidifensen.controller;


import com.sidifensen.domain.dto.SysRolePermissionDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.service.ISysRolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-06
 */
@RestController
@RequestMapping("/role-permission")
public class SysRolePermissionController {

    @Resource
    private ISysRolePermissionService sysRolePermissionService;

    /**
     * 将权限授权给角色
     *
     * @param sysRolePermissionDto 角色权限信息
     * @return
     */
    @PreAuthorize("hasAuthority('system:role:permission:add')")
    @PostMapping("add")
    public Result add(@RequestBody SysRolePermissionDto sysRolePermissionDto) {
        sysRolePermissionService.add(sysRolePermissionDto);
        return Result.success();
    }

    /**
     * 获取拥有当前权限的角色列表
     *
     * @param permissionId 权限ID
     * @return
     */
    @PreAuthorize("hasAuthority('system:role:permission:get')")
    @GetMapping("{permissionId}")
    public Result getRoles(@PathVariable Integer permissionId) {
        List<SysRoleVo> sysRoleVos =  sysRolePermissionService.getRoles(permissionId);
        return Result.success(sysRoleVos);
    }


    /**
     * 将权限批量授权给角色
     *
     * @param sysRolePermissionDto 角色权限信息
     * @return
     */
    @PreAuthorize("hasAuthority('system:role:permission:addBatch')")
    @PostMapping("addBatch")
    public Result addBatch(@RequestBody SysRolePermissionDto sysRolePermissionDto) {
        sysRolePermissionService.addBatch(sysRolePermissionDto);
        return Result.success();
    }

}
