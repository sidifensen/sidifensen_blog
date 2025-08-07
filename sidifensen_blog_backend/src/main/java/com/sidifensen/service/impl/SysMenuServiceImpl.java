package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.SysMenuVo;
import com.sidifensen.mapper.SysMenuMapper;
import com.sidifensen.service.ISysMenuService;
import com.sidifensen.utils.SecurityUtils;
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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    // 查询用户的菜单
    @Override
    public List<SysMenuVo> listMenu() {
        SysUser user = SecurityUtils.getUser();
        List<SysMenu> sysMenus = user.getSysMenus();
        List<SysMenuVo> sysMenuVos = BeanUtil.copyToList(sysMenus, SysMenuVo.class);
        return sysMenuVos;
    }

    // 新增菜单
    @Override
    public void add(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = BeanUtil.copyProperties(sysMenuDto, SysMenu.class);
        this.save(sysMenu);
    }

    // 删除菜单
    @Override
    public void delete(Integer id) {
        this.removeById(id);
    }

    // 根据菜单名称查找菜单
    @Override
    public List<SysMenuVo> search(String name) {
        // 使用MyBatis Plus的条件构造器查询菜单名称包含指定字符串的菜单
        List<SysMenu> sysMenus = this.lambdaQuery()
                .like(SysMenu::getName, name)
                .list();
        // 将查询结果转换为SysMenuVo对象列表
        return BeanUtil.copyToList(sysMenus, SysMenuVo.class);
    }

}
