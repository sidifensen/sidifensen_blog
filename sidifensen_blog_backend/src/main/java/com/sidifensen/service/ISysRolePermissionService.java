package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysRolePermissionDto;
import com.sidifensen.domain.entity.SysRolePermission;
import com.sidifensen.domain.vo.SysRoleVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-06
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {

    void add(SysRolePermissionDto sysRolePermissionDto);

    List<SysRoleVo> getRoles(Integer permissionId);
}
