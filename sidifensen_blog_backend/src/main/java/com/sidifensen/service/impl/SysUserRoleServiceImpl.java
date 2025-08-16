package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysUserRoleDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.entity.SysUserRole;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.mapper.SysUserRoleMapper;
import com.sidifensen.service.ISysUserRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserMapper sysUserMapper;


    // 给角色分配用户
    @Override
    public void addUser(SysUserRoleDto sysUserRoleDto) {
        List<Integer> userIds = sysUserRoleDto.getUserIds();
        Integer roleId = sysUserRoleDto.getRoleId();

        if (ObjectUtil.isEmpty(userIds)) {
            // 用户没有选择, 删除所有关联记录
            this.remove(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getRoleId, roleId));
            return;
        }

        // 已经存在的关联记录
        List<SysUserRole> existSysUserRoles = this.lambdaQuery()
                .in(SysUserRole::getUserId, userIds)
                .eq(SysUserRole::getRoleId, roleId)
                .list();

        Set<Integer> existUserIds = existSysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());

        // 过滤掉已存在的关联记录-需要新增的关联记录
        List<SysUserRole> sysUserRoles = userIds.stream()
                .filter(userId -> !existUserIds.contains(userId))
                .map(userId -> {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(userId);
                    sysUserRole.setRoleId(roleId);
                    return sysUserRole;
                })
                .collect(Collectors.toList());

        // 批量保存关联记录
        if (ObjectUtil.isNotEmpty(sysUserRoles)) {
            boolean exist = this.saveBatch(sysUserRoles);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
        }

        // 删除用户没有传过来的用户id的关联记录
        List<SysUserRole> notExistSysUserRoles = this.lambdaQuery()
                .notIn(SysUserRole::getUserId, userIds)
                .eq(SysUserRole::getRoleId, roleId)
                .list();

        if (ObjectUtil.isNotEmpty(notExistSysUserRoles)) {
            boolean exist = this.removeBatchByIds(notExistSysUserRoles);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
        }


    }

    // 获取拥有当前角色的用户列表
    @Override
    public List<SysRoleVo> getRoles(Integer userId) {
        // 根据用户ID查询角色列表
        List<SysUserRole> sysRoleUsers = this.lambdaQuery().eq(SysUserRole::getUserId, userId).list();
        if (sysRoleUsers.isEmpty()) {
            // sysRoleUsers为空，说明没有角色与用户关联，返回空列表
            return new ArrayList<>();
        }
        List<Integer> roleIds = sysRoleUsers.stream().map(SysUserRole::getRoleId).toList();
        // 根据角色ID列表查询角色列表
        List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(roleIds);
        if (sysRoles.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        List<SysRoleVo> sysRoleVos = BeanUtil.copyToList(sysRoles, SysRoleVo.class);
        return sysRoleVos;
    }

    // 给用户添加角色
    @Override
    public void addRole(SysUserRoleDto sysUserRoleDto) {
        List<Integer> roleIds = sysUserRoleDto.getRoleIds();
        Integer userId = sysUserRoleDto.getUserId();

        if (ObjectUtil.isEmpty(roleIds)) {
            // 用户没有选择, 删除所有关联记录
            this.remove(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, userId));
            return;
        }

        List<SysUserRole> existSysUserRoles = this.lambdaQuery()
                .in(SysUserRole::getRoleId, roleIds)
                .eq(SysUserRole::getUserId, userId)
                .list();

        Set<Integer> existUserIds = existSysUserRoles.stream()
                .map(SysUserRole::getUserId)
                .collect(Collectors.toSet());

        // 过滤掉已存在的关联记录- 需要新增的关联记录
        List<SysUserRole> sysUserRoles = roleIds.stream()
                .filter(roleId -> !existUserIds.contains(roleId))
                .map(roleId -> {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(userId);
                    sysUserRole.setRoleId(roleId);
                    return sysUserRole;
                })
                .collect(Collectors.toList());

        if (ObjectUtil.isNotEmpty(sysUserRoles)) {
            // 批量保存关联记录
            boolean exist = this.saveBatch(sysUserRoles);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
        }

        // 删除用户没有传过来的角色id的关联记录
        List<SysUserRole> notExistSysUserRoles = this.lambdaQuery()
                .notIn(SysUserRole::getRoleId, roleIds)
                .eq(SysUserRole::getUserId, userId)
                .list();

        if (ObjectUtil.isNotEmpty(notExistSysUserRoles)) {
            boolean exist = this.removeBatchByIds(notExistSysUserRoles);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
        }

    }

    // 获取当前用户拥有的角色列表
    @Override
    public List<SysUserVo> getUsers(Integer roleId) {
        // 根据角色ID查询用户列表
        List<SysUserRole> sysRoleUsers = this.lambdaQuery().eq(SysUserRole::getRoleId, roleId).list();
        if (sysRoleUsers.isEmpty()) {
            // sysRoleUsers为空，说明没有角色与用户关联，返回空列表
            return new ArrayList<>();
        }
        List<Integer> userIds = sysRoleUsers.stream().map(SysUserRole::getUserId).toList();
        // 根据用户ID列表查询用户列表
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(userIds);
        if (sysUsers.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }
        List<SysUserVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserVo.class);
        return sysUserVos;
    }

    ExecutorService executor = new ThreadPoolExecutor(1, 1, 0l, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500));

    @Override
    public void setRegisterRole(Integer userId) {
        executor.execute(() -> {
            // 设置注册用户的角色
            SysRole sysRole = sysRoleMapper.selectOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRole, "user"));
            if (ObjectUtil.isEmpty(sysRole)) {
                throw new BlogException(BlogConstants.NotFoundRole);
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(sysRole.getId());
            boolean exist = this.save(sysUserRole);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistUserRole);
            }
        });
    }
}
