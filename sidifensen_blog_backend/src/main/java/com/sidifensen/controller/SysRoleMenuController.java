package com.sidifensen.controller;


import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.service.ISysRoleMenuService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RestController
@RequestMapping("/role-menu")
public class SysRoleMenuController {

    @Resource
    private ISysRoleMenuService sysRoleService;

    /**
     * 将菜单分配给角色
     *
     * @param sysRoleMenuDto 角色菜单信息
     * @return
     */
    @PreAuthorize("hasAuthority('system:role:menu:add')")
    @PostMapping("add")
    public Result add(@RequestBody SysRoleMenuDto sysRoleMenuDto) {
        sysRoleService.add(sysRoleMenuDto);
        return Result.successMsg("添加成功");
    }

    /**
     * 获取拥有当前菜单的角色列表
     *
     * @param menuId 菜单ID
     * @return
     */
    @PreAuthorize("hasAuthority('system:role:menu:get')")
    @GetMapping("{menuId}")
    public Result getRoles(@PathVariable Integer menuId) {
        List<SysRoleVo> sysRoleVos =  sysRoleService.getRoles(menuId);
        return Result.success(sysRoleVos);
    }


}
