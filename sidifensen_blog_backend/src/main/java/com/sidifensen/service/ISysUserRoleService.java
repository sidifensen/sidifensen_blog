package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysUserRoleDto;
import com.sidifensen.domain.entity.SysUserRole;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.vo.SysUserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    void addRole(SysUserRoleDto sysUserRoleDto);

    List<SysRoleVo> getRoles(Integer roleId);

    void addUser(SysUserRoleDto sysUserRoleDto);

    List<SysUserVo> getUsers(Integer roleId);

    void setRegisterRole(Integer userId);

}
