package com.sidifensen.service;

import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 登录
     * @param loginDto 登录信息
     */
    String login(LoginDto loginDto);
}
