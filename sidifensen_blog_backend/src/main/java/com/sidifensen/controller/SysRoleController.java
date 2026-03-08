package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.SysRoleDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.service.SysRoleService;
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
@RequestMapping("/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 查询角色列表
     *
     * @return
     */
    @OperationLog(module = "角色管理", type = OperationTypeEnum.SELECT, description = "管理员获取角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("list")
    public Result list() {
        List<SysRoleVo> sysRoleVos = sysRoleService.listRole();
        return Result.success(sysRoleVos);
    }

    /**
     * 新增角色
     *
     * @param sysRoleDto 角色信息
     * @return
     */
    @OperationLog(module = "角色管理", type = OperationTypeEnum.INSERT, description = "管理员新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping("add")
    public Result add(@RequestBody @Valid SysRoleDto sysRoleDto) {
        sysRoleService.add(sysRoleDto);
        return Result.success();
    }

    /**
     * 修改角色
     *
     * @param sysRoleDto 包含更新信息的角色数据
     * @return
     */
    @OperationLog(module = "角色管理", type = OperationTypeEnum.UPDATE, description = "管理员修改角色")
    @PreAuthorize("hasAuthority('system:role:update')")
    @PutMapping("update")
    public Result update(@RequestBody @Valid SysRoleDto sysRoleDto) {
        sysRoleService.update(sysRoleDto);
        return Result.success();
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return
     */
    @OperationLog(module = "角色管理", type = OperationTypeEnum.DELETE, description = "管理员删除角色")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @DeleteMapping("{roleId}")
    public Result delete(@PathVariable @NotEmpty Integer roleId) {
        sysRoleService.delete(roleId);
        return Result.success();
    }

    /**
     * 根据角色名称查找角色
     *
     * @param name 角色名称
     * @return
     */
    @OperationLog(module = "角色管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索角色")
    @PreAuthorize("hasAuthority('system:role:search')")
    @GetMapping("search")
    public Result search(@RequestParam("name") @NotNull(message = "角色名称不能为空") String name) {
        List<SysRoleVo> roleList = sysRoleService.search(name);
        return Result.success(roleList);
    }
}