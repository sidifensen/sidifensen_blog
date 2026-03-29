package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysRoleMenuAssignDto;
import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/role-menu")
public class SysRoleMenuController {

    @Resource
    private SysRoleMenuService sysRoleService;

    /**
     * 将菜单分配给角色
     *
     * @param sysRoleMenuDto 角色菜单信息
     * @return
     */
    @OperationLog(module = "角色菜单管理", type = OperationTypeEnum.ASSIGN, description = "管理员给角色分配菜单")
    @PreAuthorize("hasAuthority('system:role:menu:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysRoleMenuDto sysRoleMenuDto) {
        sysRoleService.add(sysRoleMenuDto);
        return Result.success();
    }

    /**
     * 获取拥有当前菜单的角色列表
     *
     * @param menuId 菜单ID
     * @return
     */
    @OperationLog(module = "角色菜单管理", type = OperationTypeEnum.SELECT, description = "管理员获取拥有指定菜单的角色列表")
    @PreAuthorize("hasAuthority('system:role:menu:get')")
    @GetMapping("{menuId}")
    public Result getRoles(@PathVariable @NotNull(message = "菜单ID不能为空") Integer menuId) {
        List<SysRoleVo> sysRoleVos = sysRoleService.getRoles(menuId);
        return Result.success(sysRoleVos);
    }

    /**
     * 根据角色ID获取菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @OperationLog(module = "角色菜单管理", type = OperationTypeEnum.SELECT, description = "管理员获取指定角色的菜单权限")
    @PreAuthorize("hasAuthority('system:role:menu:get')")
    @GetMapping("/role/{roleId}")
    public Result getMenus(@PathVariable @NotNull(message = "角色ID不能为空") Integer roleId) {
        List<Integer> menuIds = sysRoleService.getMenus(roleId);
        return Result.success(menuIds);
    }

    /**
     * 给角色分配菜单权限
     *
     * @param dto 角色菜单分配信息
     * @return
     */
    @OperationLog(module = "角色菜单管理", type = OperationTypeEnum.ASSIGN, description = "管理员给角色分配菜单权限")
    @PreAuthorize("hasAuthority('system:role:menu:assign')")
    @PostMapping("assign")
    public Result assignMenus(@RequestBody @Valid SysRoleMenuAssignDto dto) {
        sysRoleService.assignMenus(dto);
        return Result.success();
    }


}
