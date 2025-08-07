package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysMenuDto;
import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.vo.SysMenuVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface ISysMenuService extends IService<SysMenu> {

    // 查询用户的菜单
    List<SysMenuVo> listMenu();

    // 新增菜单
    void add(SysMenuDto sysMenuDto);

    // 删除菜单
    void delete(Integer id);

    // 根据菜单名称查找菜单
    List<SysMenuVo> search(String name);
}
