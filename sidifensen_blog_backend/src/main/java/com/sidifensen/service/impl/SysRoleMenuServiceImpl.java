package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.vo.SysRoleVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRoleMenuMapper;
import com.sidifensen.service.ISysRoleMenuService;
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
 * @since 2025-06-29
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void add(SysRoleMenuDto sysRoleMenuDto) {
        // 获取角色ID列表和菜单ID
        List<Integer> roleIds = sysRoleMenuDto.getRoleIds();
        Integer menuId = sysRoleMenuDto.getMenuId();
        
        // 创建关联记录列表
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        
        // 为每个角色创建与菜单的关联记录
        for (Integer roleId : roleIds) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            roleMenuList.add(sysRoleMenu);
        }
        
        // 批量保存关联记录
        boolean success = this.saveBatch(roleMenuList);
        if (!success) {
            throw new BlogException(BlogConstants.ExistRoleMenu);
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