package com.sidifensen.controller;


import com.sidifensen.domain.dto.SysUserRoleDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.service.SysUserRoleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RestController
@RequestMapping("/user-role")
public class SysUserRoleController {

    @Resource
    private SysUserRoleService sysUserRoleService;


    /**
     * 给角色分配用户
     *
     * @param sysUserRoleDto 角色菜单信息
     * @return
     */
    @PreAuthorize("hasAuthority('system:user:role:addUser')")
    @PostMapping("addUser")
    public Result addUser(@RequestBody SysUserRoleDto sysUserRoleDto) {
        sysUserRoleService.addUser(sysUserRoleDto);
        return Result.success();
    }

    /**
     * 获取拥有当前角色的用户列表
     *
     * @param roleId 角色id
     * @return
     */
    @PreAuthorize("hasAuthority('system:user:role:getUsers')")
    @GetMapping("getUsers/{roleId}")
    public Result getUsers(@PathVariable Integer roleId) {
        List<SysUserVo> sysUserVos = sysUserRoleService.getUsers(roleId);
        return Result.success(sysUserVos);
    }

    /**
     * 给用户添加角色
     *
     * @param sysUserRoleDto 角色菜单信息
     * @return
     */
    @PreAuthorize("hasAuthority('system:user:role:addRole')")
    @PostMapping("addRole")
    public Result addRole(@RequestBody SysUserRoleDto sysUserRoleDto) {
        sysUserRoleService.addRole(sysUserRoleDto);
        return Result.success();
    }

    /**
     * 获取当前用户拥有的角色列表
     *
     * @param userId 用户id
     * @return
     */
    @PreAuthorize("hasAuthority('system:user:role:getRoles')")
    @GetMapping("getRoles/{userId}")
    public Result getRoles(@PathVariable Integer userId) {
        List<SysRoleVo> sysRoleVos = sysUserRoleService.getRoles(userId);
        return Result.success(sysRoleVos);
    }


}
