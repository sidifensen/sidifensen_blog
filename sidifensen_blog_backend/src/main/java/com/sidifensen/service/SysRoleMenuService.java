package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysRoleMenuDto;
import com.sidifensen.domain.entity.SysRoleMenu;
import com.sidifensen.domain.vo.SysRoleVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void add(SysRoleMenuDto sysRoleMenuDto);

    List<SysRoleVo> getRoles(Integer menuId);
}
