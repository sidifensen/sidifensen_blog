package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysRoleDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.entity.SysRolePermission;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRoleMenuMapper;
import com.sidifensen.mapper.SysRolePermissionMapper;
import com.sidifensen.service.ISysRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysRoleVo> listRole() {
        List<SysRole> roleList = this.list();
        List<SysRoleVo> sysRoleVos = BeanUtil.copyToList(roleList, SysRoleVo.class);
        return sysRoleVos;
    }

    @Override
    public void add(SysRoleDto sysRoleDto) {
        SysRole sysRole = BeanUtil.copyProperties(sysRoleDto, SysRole.class);
        this.save(sysRole);
    }

    @Override
    public void update(SysRoleDto sysRoleDto) {
        SysRole sysRole = BeanUtil.copyProperties(sysRoleDto, SysRole.class);
        this.updateById(sysRole);
    }

    @Override
    public void delete(Integer roleId) {
        boolean exist = this.removeById(roleId);
        if (!exist){
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        // 删除角色_菜单关联表
        sysRoleMenuMapper.delete(new QueryWrapper<SysRoleMenu>().lambda().eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色_权限关联表
        sysRolePermissionMapper.delete(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId));
    }

    @Override
    public List<SysRoleVo> search(String name) {
        List<SysRole> sysRoles = this.lambdaQuery().like(SysRole::getName, name).list();
        List<SysRoleVo> sysRoleVos = BeanUtil.copyToList(sysRoles, SysRoleVo.class);
        return sysRoleVos;
    }
}
