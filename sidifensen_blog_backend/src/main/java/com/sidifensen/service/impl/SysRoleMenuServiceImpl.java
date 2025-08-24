package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRoleMenuMapper;
import com.sidifensen.service.SysRoleMenuService;
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
 * @since 2025-06-29
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void add(SysRoleMenuDto sysRoleMenuDto) {
        // 获取角色ID列表和菜单ID
        List<Integer> roleIds = sysRoleMenuDto.getRoleIds();
        Integer menuId = sysRoleMenuDto.getMenuId();

        if (ObjectUtil.isEmpty(roleIds)) {
            // 用户没有选择, 删除所有关联记录
            this.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getMenuId, menuId));
            return;
        }

        // 查询已存在的关联记录
        List<SysRoleMenu> existingRoleMenus = this.lambdaQuery()
                .in(SysRoleMenu::getRoleId, roleIds)
                .eq(SysRoleMenu::getMenuId, menuId)
                .list();

        // 构建已存在的roleId集合
        Set<Integer> existingRoleIds = existingRoleMenus.stream()
                .map(SysRoleMenu::getRoleId)
                .collect(Collectors.toSet());

        // 过滤出需要保存的记录
        List<SysRoleMenu> roleMenuList = roleIds.stream()
                .filter(roleId -> !existingRoleIds.contains(roleId))
                .map(roleId -> {
                    SysRoleMenu sysRoleMenu = new SysRoleMenu();
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenu.setMenuId(menuId);
                    return sysRoleMenu;
                })
                .collect(Collectors.toList());

        // 如果没有需要保存的记录
        if (ObjectUtil.isNotEmpty(roleMenuList)) {
            // 批量保存不存在的记录
            boolean exist = this.saveBatch(roleMenuList);
            if (!exist) {
                throw new BlogException(BlogConstants.ExistRoleMenu);
            }
        }

        // 过滤出需要删除的记录
        List<SysRoleMenu> noExistingRoleMenus = this.lambdaQuery()
                .notIn(SysRoleMenu::getRoleId, roleIds)
                .eq(SysRoleMenu::getMenuId, menuId)
                .list();

        // 批量删除不存在的记录
        if (ObjectUtil.isNotEmpty(noExistingRoleMenus)) {
            boolean exist = this.removeByIds(noExistingRoleMenus.stream().map(SysRoleMenu::getId).toList());
            if (!exist) {
                throw new BlogException(BlogConstants.ExistRoleMenu);
            }
        }

    }

    @Override
    public List<SysRoleVo> getRoles(Integer menuId) {
        // 根据菜单ID查询角色列表
        List<SysRoleMenu> sysRoleMenus = this.lambdaQuery().eq(SysRoleMenu::getMenuId, menuId).list();
        if (sysRoleMenus.isEmpty()) {
            // sysRoleMenus为空，说明没有角色与菜单关联，返回空列表
            return new ArrayList<>();
        }
        List<Integer> roleIds = sysRoleMenus.stream().map(SysRoleMenu::getRoleId).toList();
        // 根据角色ID列表查询角色列表
        List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(roleIds);
        if (sysRoles.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        List<SysRoleVo> sysRoleVos = BeanUtil.copyToList(sysRoles, SysRoleVo.class);
        return sysRoleVos;
    }


}