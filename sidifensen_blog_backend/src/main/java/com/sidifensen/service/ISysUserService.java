package com.sidifensen.service;

import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.vo.UserVo;

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
    void sendEmailCheckCode(EmailDto emailDto);

    /**
     * 重置密码时校验邮箱验证码
     * @param verifyResetDto
     */
    void verifyReset(VerifyResetDto verifyResetDto);

    /**
     * 重置密码
     * @param resetPasswordDto
     */
    void resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 获取用户信息
     * @return UserVo 用户信息
     */
    UserVo info();

}
