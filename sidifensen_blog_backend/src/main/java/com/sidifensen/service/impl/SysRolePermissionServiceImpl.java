package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysRolePermissionDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysRolePermission;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRolePermissionMapper;
import com.sidifensen.service.ISysRolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-06
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void add(SysRolePermissionDto sysRolePermissionDto) {
        // 获取角色ID列表和菜单ID
        List<Integer> roleIds = sysRolePermissionDto.getRoleIds();
        Integer permissionId = sysRolePermissionDto.getPermissionId();

        // 创建关联记录列表
        List<SysRolePermission> rolePermissionList = new ArrayList<>();

        // 为每个角色创建与菜单的关联记录
        for (Integer roleId : roleIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(roleId);
            sysRolePermission.setPermissionId(permissionId);
            rolePermissionList.add(sysRolePermission);
        }

        // 批量保存关联记录
        boolean success = this.saveBatch(rolePermissionList);
        if (!success) {
            throw new BlogException(BlogConstants.ExistRolePermission);
        }
    }

    @Override
    public List<SysRoleVo> getRoles(Integer permissionId) {
        // 根据权限ID查询角色列表
        List<SysRolePermission> sysRolePermissions = this.lambdaQuery().eq(SysRolePermission::getPermissionId, permissionId).list();
        if (sysRolePermissions.isEmpty()) {
            // sysRolePermissions为空，说明没有角色与权限关联，返回空列表
            return new ArrayList<>();
        }
        List<Integer> roleIds = sysRolePermissions.stream().map(SysRolePermission::getRoleId).toList();
        // 根据角色ID列表查询角色列表
        List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(roleIds);
        if (sysRoles.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        List<SysRoleVo> sysRoleVos = BeanUtil.copyToList(sysRoles, SysRoleVo.class);
        return sysRoleVos;
    }
}
