package com.sidifensen.domain.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class LoginUser implements UserDetails {

    private SysUser sysUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
        return sysUser.getStatus() == 0;
    }

    // 密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 是否启用
    @Override
    public boolean isEnabled() {
        return sysUser.getStatus() == 0;
    }
}
