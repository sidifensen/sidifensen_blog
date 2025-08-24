package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysRolePermissionDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysRolePermission;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRolePermissionMapper;
import com.sidifensen.service.SysRolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-06
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void add(SysRolePermissionDto sysRolePermissionDto) {

        List<Integer> roleIds = sysRolePermissionDto.getRoleIds();
        Integer permissionId = sysRolePermissionDto.getPermissionId();

        if (ObjectUtil.isEmpty(roleIds)) {
            // 用户没有选择角色，直接全部删除原有关联记录
            this.remove(Wrappers.<SysRolePermission>lambdaQuery().eq(SysRolePermission::getPermissionId, permissionId));
            return;
        }

        List<SysRolePermission> sysRolePermissions = this.lambdaQuery()
                .in(SysRolePermission::getRoleId, roleIds)
                .eq(SysRolePermission::getPermissionId, permissionId)
                .list();

        Set<Integer> existRoleIds = sysRolePermissions.stream()
                .map(SysRolePermission::getRoleId)
                .collect(Collectors.toSet());

        // 过滤掉已存在的关联记录- 需要新增的关联记录
        List<SysRolePermission> newSysRolePermissions = roleIds.stream()
                .filter(roleId -> !existRoleIds.contains(roleId))
                .map(roleId -> {
                    SysRolePermission sysRolePermission = new SysRolePermission();
                    sysRolePermission.setRoleId(roleId);
                    sysRolePermission.setPermissionId(permissionId);
                    return sysRolePermission;
                })
                .collect(Collectors.toList());

        if (ObjectUtil.isNotEmpty(newSysRolePermissions)) {
            // 批量保存关联记录
            boolean exist = this.saveBatch(newSysRolePermissions);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistRolePermission);
            }
        }

        // 删除用户没有传过来的角色id的关联记录
        List<SysRolePermission> notExistSysRolePermissions = this.lambdaQuery()
                .notIn(SysRolePermission::getRoleId, roleIds)
                .eq(SysRolePermission::getPermissionId, permissionId)
                .list();

        if (ObjectUtil.isNotEmpty(notExistSysRolePermissions)) {
            boolean exist = this.removeBatchByIds(notExistSysRolePermissions);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
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

    @Override
    public void addBatch(SysRolePermissionDto sysRolePermissionDto) {
        List<Integer> roleIds = sysRolePermissionDto.getRoleIds();
        List<Integer> permissionIds = sysRolePermissionDto.getPermissionIds();

        if (ObjectUtil.isEmpty(roleIds) || ObjectUtil.isEmpty(permissionIds)) {
            return;
        }

        // 查询已存在的角色权限关联
        List<SysRolePermission> existRolePermissions = this.lambdaQuery()
                .in(SysRolePermission::getRoleId, roleIds)
                .in(SysRolePermission::getPermissionId, permissionIds)
                .list();

        // 构建已存在的角色权限对的集合 (roleId_permissionId)
        Set<String> existPair = existRolePermissions.stream()
                .map(rp -> rp.getRoleId() + "_" + rp.getPermissionId())
                .collect(Collectors.toSet());

        // 构建需要新增的角色权限关联列表
        List<SysRolePermission> newRolePermissions = new ArrayList<>();
        for (Integer roleId : roleIds) {
            for (Integer permissionId : permissionIds) {
                String pair = roleId + "_" + permissionId;
                // 如果该角色权限对不存在，则添加到新增列表中
                if (!existPair.contains(pair)) {
                    SysRolePermission rolePermission = new SysRolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permissionId);
                    newRolePermissions.add(rolePermission);
                }
            }
        }

        // 批量保存新增的角色权限关联
        if (ObjectUtil.isNotEmpty(newRolePermissions)) {
            boolean saved = this.saveBatch(newRolePermissions);
            if (!saved) {
                throw new BlogException(BlogConstants.ExistRolePermission);
            }
        }
    }
}
