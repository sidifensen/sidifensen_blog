package com.sidifensen.controller;


import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.service.ISysMenuService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController {

    @Resource
    private ISysMenuService sysMenuService;

    /**
     * 查询登录用户的菜单列表
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("list")
    public Result list() {
        List<SysMenuVo> menuList = sysMenuService.listMenu();
        return Result.success(menuList);
    }

    /**
     * 新增菜单
     * @param sysMenuDto
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping("add")
    public Result add(SysMenuDto sysMenuDto) {
        sysMenuService.add(sysMenuDto);
        return Result.successMsg("添加成功");
    }

    /**
     * 删除菜单
     * @param id 菜单ID
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @PostMapping("delete")
    public Result delete(Integer id) {
        sysMenuService.delete(id);
        return Result.successMsg("删除成功");
    }

    /**
     * 根据菜单名称查找菜单
     * @param name 菜单名称
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:search')")
    @GetMapping("search")
    public Result search(String name) {
        List<SysMenuVo> menuList = sysMenuService.search(name);
        return Result.success(menuList);
    }

}
