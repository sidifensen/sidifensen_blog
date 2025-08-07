package com.sidifensen.security;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.*;
import com.sidifensen.domain.enums.MenuEnum;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.enums.RoleEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.*;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysUserDetailsService implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysMenuMapper umsMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private IpUtils ipUtils;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;


    /**
     * 根据用户名查询用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO 通过请求头 判断登录类型 账号/邮箱登录 或者 第三方登录

        // 根据用户名或邮箱登录
        SysUser sysUser = loginByUsernameOrEmail(username);

        if(sysUser == null){
            throw new BlogException(BlogConstants.NotFoundUser);
            // throw new UsernameNotFoundException("该用户不存在");
            // 如果用UsernameNotFoundException会被AbstractUserDetailsAuthenticationProvider的authenticate拦截，
            // 并且包装成BadCredentialsException, 返回"用户名或密码错误"的错误信息
        }

        sysUser.setLoginType(RegisterOrLoginTypeEnum.EMAIL.getRegisterType());
        sysUser.setLoginTime(new Date());
        sysUser.setLoginIp(ipUtils.getIpAddr());
        sysUserMapper.updateById(sysUser);

        return handleLogin(sysUser);
    }

    // 根据用户名或邮箱登录
    public SysUser loginByUsernameOrEmail(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username)
                .or().eq(SysUser::getEmail, username)
                .eq(SysUser::getRegisterType, RegisterOrLoginTypeEnum.EMAIL.getRegisterType());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        return sysUser;
    }


    // 处理登录
    public LoginUser handleLogin(SysUser sysUser){

        // 查询用户对应的角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, sysUser.getId());
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);

        LambdaQueryWrapper<SysRole> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SysRole::getId, sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
        queryWrapper1.eq(SysRole::getStatus, RoleEnum.ROLE_STATUS_NORMAL.getStatus());
        List<SysRole> sysRoles = sysRoleMapper.selectList(queryWrapper1);

        if (ObjectUtil.isEmpty(sysRoles)){
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        List<Integer> roleIds = sysRoles.stream().map(SysRole::getId).collect(Collectors.toList());

        // 将角色加入到用户信息中
        sysUser.setSysRoles(sysRoles);

        // 查询角色对应的菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(queryWrapper2);

        LambdaQueryWrapper<SysMenu> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.in(SysMenu::getId, sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
        queryWrapper3.eq(SysMenu::getStatus, MenuEnum.MENU_STATUS_NORMAL.getStatus());
        List<SysMenu> sysMenus = umsMenuMapper.selectList(queryWrapper3);

        if (ObjectUtil.isEmpty(sysMenus)){
            throw new BlogException(BlogConstants.NotFoundMenu);
        }
        // 将菜单信息加入到用户信息中
        sysUser.setSysMenus(sysMenus);

        // 查询角色对应的权限
        LambdaQueryWrapper<SysRolePermission> queryWrapper4 = new LambdaQueryWrapper<>();
        queryWrapper4.in(SysRolePermission::getRoleId, roleIds);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectList(queryWrapper4);

        LambdaQueryWrapper<SysPermission> queryWrapper5 = new LambdaQueryWrapper<>();
        queryWrapper5.in(SysPermission::getId, sysRolePermissions.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList()));
        List<SysPermission> sysPermissions = sysPermissionMapper.selectList(queryWrapper5);

        if (ObjectUtil.isEmpty(sysPermissions)){
            throw new BlogException(BlogConstants.NotFoundPermission);
        }

        // 将权限信息加入到用户信息中
        sysUser.setSysPermissions(sysPermissions);


        // 封装登录用户信息
        LoginUser loginUser = new LoginUser(sysUser);
        return loginUser;
    }
}
