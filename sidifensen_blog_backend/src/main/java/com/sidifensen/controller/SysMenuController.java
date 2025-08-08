package com.sidifensen.controller;


import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.service.ISysMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        List<SysMenuVo> menuVoList = sysMenuService.listMenu();
        return Result.success(menuVoList);
    }

    /**
     * 查询所有用户的菜单列表
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:listAll')")
    @GetMapping("listAll")
    public Result listAll(){
        List<SysMenuVo> menuVoList = sysMenuService.listAllMenu();
        return Result.success(menuVoList);
    }

    /**
     * 新增菜单
     * @param sysMenuDto
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysMenuDto sysMenuDto) {
        sysMenuService.add(sysMenuDto);
        return Result.successMsg("添加成功");
    }

    /**
     * 修改菜单
     * @param sysMenuDto 包含更新信息的菜单数据传输对象
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:update')")
    @PutMapping("update")
    public Result update(@RequestBody @Valid SysMenuDto sysMenuDto) {
        sysMenuService.update(sysMenuDto);
        return Result.successMsg("更新成功");
    }

    /**
     * 删除菜单
     * @param menuId 菜单ID
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("{menuId}")
    public Result delete(@PathVariable @NotEmpty Integer menuId) {
        sysMenuService.delete(menuId);
        return Result.successMsg("删除成功");
    }

    /**
     * 根据菜单名称查找菜单
     * @param name 菜单名称
     * @return
     */
    @PreAuthorize("hasAuthority('system:menu:search')")
    @GetMapping("search")
    public Result search(@RequestParam("name") String name) {
        List<SysMenuVo> menuList = sysMenuService.search(name);
        return Result.success(menuList);
    }



}