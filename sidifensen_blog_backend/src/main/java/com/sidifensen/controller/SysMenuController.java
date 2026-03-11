package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
@RequestMapping("/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 查询登录用户的菜单列表
     *
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.SELECT, description = "管理员获取用户菜单列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("list")
    public Result list() {
        List<SysMenuVo> menuVoList = sysMenuService.listMenu();
        return Result.success(menuVoList);
    }

    /**
     * 查询所有用户的菜单列表
     *
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.SELECT, description = "管理员获取所有菜单列表")
    @PreAuthorize("hasAuthority('system:menu:listAll')")
    @GetMapping("listAll")
    public Result listAll() {
        List<SysMenuVo> menuVoList = sysMenuService.listAllMenu();
        return Result.success(menuVoList);
    }

    /**
     * 分页查询所有菜单
     *
     * @return 菜单分页列表
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.SELECT, description = "管理员分页获取菜单列表")
    @PreAuthorize("hasAuthority('system:menu:listAll')")
    @GetMapping("page")
    public Result<PageVo<List<SysMenuVo>>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(sysMenuService.pageMenu(pageNum, pageSize));
    }

    /**
     * 新增菜单
     *
     * @param sysMenuDto
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.INSERT, description = "管理员新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysMenuDto sysMenuDto) {
        sysMenuService.add(sysMenuDto);
        return Result.success();
    }

    /**
     * 修改菜单
     *
     * @param sysMenuDto 包含更新信息的菜单数据传输对象
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.UPDATE, description = "管理员修改菜单")
    @PreAuthorize("hasAuthority('system:menu:update')")
    @PutMapping("update")
    public Result update(@RequestBody @Valid SysMenuDto sysMenuDto) {
        sysMenuService.update(sysMenuDto);
        return Result.success();
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.DELETE, description = "管理员删除菜单")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("{menuId}")
    public Result delete(@PathVariable @NotEmpty Integer menuId) {
        sysMenuService.delete(menuId);
        return Result.success();
    }

    /**
     * 根据菜单名称查找菜单
     *
     * @param name 菜单名称
     * @return
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索菜单")
    @PreAuthorize("hasAuthority('system:menu:search')")
    @GetMapping("search")
    public Result search(@RequestParam("name") @NotNull(message = "菜单名称不能为空") String name) {
        List<SysMenuVo> menuList = sysMenuService.search(name);
        return Result.success(menuList);
    }

    /**
     * 分页搜索菜单
     *
     * @return 菜单分页列表
     */
    @OperationLog(module = "菜单管理", type = OperationTypeEnum.SEARCH, description = "管理员分页搜索菜单")
    @PreAuthorize("hasAuthority('system:menu:search')")
    @GetMapping("page/search")
    public Result<PageVo<List<SysMenuVo>>> searchPage(
            @RequestParam("name") @NotNull(message = "菜单名称不能为空") String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(sysMenuService.searchPage(name, pageNum, pageSize));
    }


}
