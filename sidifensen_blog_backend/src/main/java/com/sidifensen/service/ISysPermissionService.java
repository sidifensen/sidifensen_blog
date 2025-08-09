package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysPermissionDto;
import com.sidifensen.domain.entity.SysPermission;
import com.sidifensen.domain.vo.SysPermissionVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-06
 */
public interface ISysPermissionService extends IService<SysPermission> {

    // 查询权限列表
    List<SysPermissionVo> listPermission();

    // 新增权限
    void add(SysPermissionDto sysPermissionDto);

    // 更新权限
    void update(SysPermissionDto sysPermissionDto);

    // 删除权限
    void delete(Integer permissionId);

    // 查找权限
    List<SysPermissionVo> search(SysPermissionDto sysPermissionDto);
}
