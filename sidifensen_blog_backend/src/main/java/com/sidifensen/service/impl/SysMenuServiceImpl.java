package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysMenuMapper;
import com.sidifensen.mapper.SysRoleMenuMapper;
import com.sidifensen.service.SysMenuService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    // 查询登录用户的菜单
    @Override
    public List<SysMenuVo> listMenu() {
        SysUser user = SecurityUtils.getUser();
        List<SysMenu> sysMenus = user.getSysMenus();
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

    // 查询所有用户的菜单列表
    @Override
    public List<SysMenuVo> listAllMenu() {
        List<SysMenu> sysMenus = this.list();
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

    // 新增菜单
    @Override
    public void add(SysMenuDto sysMenuDto) {
        sysMenuDto.setStatus(0);// 默认状态为正常
        SysMenu sysMenu = BeanUtil.copyProperties(sysMenuDto, SysMenu.class);
        this.save(sysMenu);
    }

    // 更新菜单
    @Override
    public void update(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = BeanUtil.copyProperties(sysMenuDto, SysMenu.class);
        this.updateById(sysMenu);
    }

    // 删除菜单
    @Override
    public void delete(Integer id) {
        boolean exist = this.removeById(id);
        if (!exist) {
            throw new BlogException(BlogConstants.NotFoundMenu);
        }
        // 删除角色_菜单关联表
        sysRoleMenuMapper.delete(new QueryWrapper<SysRoleMenu>().lambda().eq(SysRoleMenu::getMenuId, id));
    }

    // 根据菜单名称查找菜单
    @Override
    public List<SysMenuVo> search(String name) {
        // 使用MyBatis Plus的条件构造器查询菜单名称包含指定字符串的菜单
        List<SysMenu> sysMenus = this.lambdaQuery().like(SysMenu::getName, name).list();
        // 将查询结果转换为SysMenuVo对象列表
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

}