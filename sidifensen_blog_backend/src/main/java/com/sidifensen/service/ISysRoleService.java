package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysRoleDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.vo.SysRoleVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface ISysRoleService extends IService<SysRole> {

    // 查询角色列表
    List<SysRoleVo> listRole();

    // 新增角色
    void add(SysRoleDto sysRoleDto);

    // 更新角色
    void update(SysRoleDto sysRoleDto);

    // 删除角色
    void delete(Integer roleId);

    // 查找角色
    List<SysRoleVo> search(String name);
}
