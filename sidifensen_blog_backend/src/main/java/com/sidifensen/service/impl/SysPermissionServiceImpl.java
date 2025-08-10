package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysPermissionDto;
import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysPermission;
import com.sidifensen.domain.entity.SysRolePermission;
import com.sidifensen.domain.vo.SysPermissionVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysMenuMapper;
import com.sidifensen.mapper.SysPermissionMapper;
import com.sidifensen.mapper.SysRolePermissionMapper;
import com.sidifensen.service.ISysPermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-06
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysPermissionVo> listPermission() {
        List<SysPermission> sysPermissions = this.list();
        List<SysPermissionVo> sysPermissionVos = BeanUtil.copyToList(sysPermissions, SysPermissionVo.class);
        if (ObjectUtil.isNotEmpty(sysPermissions)) {
            List<SysMenu> sysMenus = sysMenuMapper.selectBatchIds(sysPermissions.stream().map(SysPermission::getMenuId).toList());
            // 设置菜单名称
            sysPermissionVos.stream().forEach(sysPermission -> {
                sysPermission.setMenuName(sysMenus.stream().filter(sysMenu -> sysMenu.getId().equals(sysPermission.getMenuId())).findFirst().get().getName());
                sysPermission.setIcon(sysMenus.stream().filter(sysMenu -> sysMenu.getId().equals(sysPermission.getMenuId())).findFirst().get().getIcon());
            });
        }
        return sysPermissionVos;
    }

    @Override
    public void add(SysPermissionDto sysPermissionDto) {
        SysPermission sysPermission = BeanUtil.copyProperties(sysPermissionDto, SysPermission.class);
        this.save(sysPermission);
    }

    @Override
    public void update(SysPermissionDto sysPermissionDto) {
        SysPermission sysPermission = BeanUtil.copyProperties(sysPermissionDto, SysPermission.class);
        this.updateById(sysPermission);
    }

    @Override
    public void delete(Integer permissionId) {
        boolean exist = this.removeById(permissionId);
        if (!exist) {
            throw new BlogException(BlogConstants.NotFoundPermission);
        }
        // 删除角色_权限关联表
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getPermissionId, permissionId));
    }

    @Override
    public List<SysPermissionVo> search(SysPermissionDto sysPermissionDto) {
        LambdaQueryWrapper<SysPermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(ObjectUtil.isNotEmpty(sysPermissionDto.getDescription()), SysPermission::getDescription, sysPermissionDto.getDescription())
                .like(ObjectUtil.isNotEmpty(sysPermissionDto.getPermission()), SysPermission::getPermission, sysPermissionDto.getPermission())
                .eq(ObjectUtil.isNotEmpty(sysPermissionDto.getMenuId()), SysPermission::getMenuId, sysPermissionDto.getMenuId());

        List<SysPermission> sysPermissions = this.list(lambdaQueryWrapper);
        List<SysPermissionVo> sysPermissionVos = BeanUtil.copyToList(sysPermissions, SysPermissionVo.class);

        if (ObjectUtil.isNotEmpty(sysPermissions)) {
            List<SysMenu> sysMenus = sysMenuMapper.selectBatchIds(sysPermissions.stream().map(SysPermission::getMenuId).toList());
            // 设置菜单名称
            sysPermissionVos.stream().forEach(sysPermission -> {
                sysPermission.setMenuName(sysMenus.stream().filter(sysMenu -> sysMenu.getId().equals(sysPermission.getMenuId())).findFirst().get().getName());
                sysPermission.setIcon(sysMenus.stream().filter(sysMenu -> sysMenu.getId().equals(sysPermission.getMenuId())).findFirst().get().getIcon());
            });
        }
        return sysPermissionVos;
    }
}
