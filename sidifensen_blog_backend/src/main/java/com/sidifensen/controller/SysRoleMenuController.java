package com.sidifensen.controller;


import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
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
    @PreAuthorize("hasAuthority('system:role:menu:get')")
    @GetMapping("{menuId}")
    public Result getRoles(@PathVariable @NotNull(message = "菜单ID不能为空") Integer menuId) {
        List<SysRoleVo> sysRoleVos = sysRoleService.getRoles(menuId);
        return Result.success(sysRoleVos);
    }


}
