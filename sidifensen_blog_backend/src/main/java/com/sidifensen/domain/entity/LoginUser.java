package com.sidifensen.domain.entity;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class LoginUser implements UserDetails {

    // 登录账号
    private SysUser sysUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> permissions = sysUser.getPermissions();
        if(ObjectUtil.isNotEmpty(permissions)){
            return permissions.stream().map(permission -> new SimpleGrantedAuthority(permission)).collect(Collectors.toList());
        }
        return null;
    }

    // 密码
    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    // 用户名
    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    // 账号是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账号是否未被锁定
    @Override
    public boolean isAccountNonLocked() {
        return sysUser.getStatus() == 0;// 0表示未被锁定
    }

    // 密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 是否启用
    @Override
    public boolean isEnabled() {
        return true;
    }
}
