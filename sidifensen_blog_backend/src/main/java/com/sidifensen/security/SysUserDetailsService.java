package com.sidifensen.security;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.*;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.enums.RoleEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.*;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private IpUtils ipUtils;


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
        // 根据账号查询用户
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, sysUser.getId());
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);

        // 查询用户对应的角色
        List<SysRole> sysRoles = sysUserRoles.stream()
                .map(role -> sysRoleMapper.selectById(role.getId()))
                .filter(role -> role.getStatus() == RoleEnum.ROLE_STATUS_NORMAL.getStatus())
                .collect(Collectors.toList());
        // 将角色加入到用户信息中
        sysUser.setRoles(sysRoles);

        if (!CollUtil.isEmpty(sysRoles)){
            // 查询角色对应的菜单权限
            List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectBatchIds(sysRoles.stream().map(SysRole::getId).collect(Collectors.toList()));
            List<Integer> menuIds = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            // 查询菜单
            List<SysMenu> sysMenus = umsMenuMapper.selectBatchIds(menuIds);
            List<String> permissions = sysMenus.stream().map(SysMenu::getPermission).collect(Collectors.toList());
            // 将菜单权限加入到用户信息中
            sysUser.setPermissions(permissions);
        }

        // 根据角色查询权限 menu
        LoginUser loginUser = new LoginUser(sysUser);
        return loginUser;
    }
}
