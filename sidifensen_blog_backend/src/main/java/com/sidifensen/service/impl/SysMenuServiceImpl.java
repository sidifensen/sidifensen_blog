package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.entity.SysUserRole;
import com.sidifensen.domain.enums.StatusEnum;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysMenuMapper;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysRoleMenuMapper;
import com.sidifensen.mapper.SysUserRoleMapper;
import com.sidifensen.service.SysMenuService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 查询登录用户的菜单
     * 直接通过数据库查询当前用户角色对应的菜单，避免每次请求都加载完整用户信息
     *
     * @return 用户菜单列表
     */
    @Override
    public List<SysMenuVo> listMenu() {
        // 获取当前登录用户 ID
        Integer userId = SecurityUtils.getUserId();
        if (userId == null || userId == 0) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 1. 查询用户的所有角色 ID
        List<Integer> roleIds = sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>()
                                .eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundRole);
        }

        // 2. 查询角色对应的菜单 ID（通过 sys_role_menu 表）
        List<Integer> menuIds = sysRoleMenuMapper.selectList(
                        new LambdaQueryWrapper<SysRoleMenu>()
                                .in(SysRoleMenu::getRoleId, roleIds))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundMenu);
        }

        // 3. 查询菜单详情（只查询正常状态的菜单）
        List<SysMenu> sysMenus = this.lambdaQuery()
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getStatus, StatusEnum.NORMAL.getStatus())
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId)
                .list();

        // 4. 转换为 VO 返回
        return BeanUtil.copyToList(sysMenus, SysMenuVo.class);
    }

    // 查询所有用户的菜单列表
    @Override
    public List<SysMenuVo> listAllMenu() {
        List<SysMenu> sysMenus = this.lambdaQuery()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId)
                .list();
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

    @Override
    public PageVo<List<SysMenuVo>> pageMenu(Integer pageNum, Integer pageSize) {
        return buildMenuPage(null, pageNum, pageSize);
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
        List<SysMenu> sysMenus = this.lambdaQuery()
                .like(SysMenu::getName, name)
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId)
                .list();
        // 将查询结果转换为SysMenuVo对象列表
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

    @Override
    public PageVo<List<SysMenuVo>> searchPage(String name, Integer pageNum, Integer pageSize) {
        return buildMenuPage(name, pageNum, pageSize);
    }

    private PageVo<List<SysMenuVo>> buildMenuPage(String name, Integer pageNum, Integer pageSize) {
        List<SysMenu> allMenus = this.lambdaQuery()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId)
                .list();
        if (allMenus.isEmpty()) {
            return new PageVo<>(List.of(), 0L);
        }

        Map<Integer, SysMenu> menuMap = new HashMap<>();
        Map<Integer, List<SysMenu>> childrenMap = new HashMap<>();
        List<Integer> rootIds = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            menuMap.put(menu.getId(), menu);
            Integer parentId = menu.getParentId() == null ? 0 : menu.getParentId();
            childrenMap.computeIfAbsent(parentId, key -> new ArrayList<>()).add(menu);
            if (isRootMenu(menu)) {
                rootIds.add(menu.getId());
            }
        }

        List<Integer> filteredRootIds = filterRootIds(rootIds, allMenus, menuMap, name);
        long total = filteredRootIds.size();
        int start = Math.min((pageNum - 1) * pageSize, filteredRootIds.size());
        int end = Math.min(start + pageSize, filteredRootIds.size());
        if (start >= end) {
            return new PageVo<>(List.of(), total);
        }

        List<SysMenu> pageMenus = new ArrayList<>();
        for (Integer rootId : filteredRootIds.subList(start, end)) {
            appendMenuSubtree(rootId, menuMap, childrenMap, pageMenus);
        }
        return new PageVo<>(BeanUtil.copyToList(pageMenus, SysMenuVo.class), total);
    }

    private List<Integer> filterRootIds(List<Integer> rootIds, List<SysMenu> allMenus, Map<Integer, SysMenu> menuMap, String name) {
        if (name == null || name.isBlank()) {
            return rootIds;
        }

        Set<Integer> matchedRootIds = new LinkedHashSet<>();
        for (SysMenu menu : allMenus) {
            if (menu.getName() != null && menu.getName().contains(name)) {
                matchedRootIds.add(resolveRootId(menu, menuMap));
            }
        }

        return rootIds.stream().filter(matchedRootIds::contains).toList();
    }

    private Integer resolveRootId(SysMenu menu, Map<Integer, SysMenu> menuMap) {
        SysMenu current = menu;
        while (!isRootMenu(current)) {
            SysMenu parent = menuMap.get(current.getParentId());
            if (parent == null) {
                break;
            }
            current = parent;
        }
        return current.getId();
    }

    private void appendMenuSubtree(Integer menuId, Map<Integer, SysMenu> menuMap, Map<Integer, List<SysMenu>> childrenMap,
            List<SysMenu> result) {
        SysMenu current = menuMap.get(menuId);
        if (current == null) {
            return;
        }

        result.add(current);
        for (SysMenu child : childrenMap.getOrDefault(menuId, List.of())) {
            appendMenuSubtree(child.getId(), menuMap, childrenMap, result);
        }
    }

    private boolean isRootMenu(SysMenu menu) {
        return menu.getParentId() == null || menu.getParentId() == 0;
    }

}
