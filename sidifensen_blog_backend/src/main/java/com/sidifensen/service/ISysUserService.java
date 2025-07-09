package com.sidifensen.service;

import com.sidifensen.domain.dto.EmailDto;
import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.dto.RegisterDto;
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

    /**
     * 注册
     * @param registerDto 注册信息
     */
    void register(RegisterDto registerDto);

    /**
     * 发送注册邮件验证码
     * @param emailDto 邮箱信息
     */
    void registerEmailCheckCode(EmailDto emailDto);
}
