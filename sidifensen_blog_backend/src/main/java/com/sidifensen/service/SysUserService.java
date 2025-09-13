package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.SysUserDetailVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.domain.vo.SysUserWithArticleCountVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 登录
     *
     * @param loginDto 登录信息
     */
    String login(LoginDto loginDto);

    /**
     * oauth登录
     *
     * @param oauthLoginDto
     * @return
     */
    String oauthLogin(OauthLoginDto oauthLoginDto);

    /**
     * 注册
     *
     * @param registerDto 注册信息
     */
    void register(RegisterDto registerDto);

    /**
     * 发送注册邮件验证码
     *
     * @param emailDto 邮箱信息
     */
    void sendEmailCheckCode(EmailDto emailDto);

    /**
     * 重置密码时校验邮箱验证码
     *
     * @param verifyResetDto
     */
    void verifyReset(VerifyResetDto verifyResetDto);

    /**
     * 重置密码
     *
     * @param resetPasswordDto
     */
    void resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 获取用户信息
     *
     * @return UserVo 用户信息
     */
    SysUserVo info();

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return SysUserVo 用户信息
     */
    SysUserVo getUserInfoById(Integer userId);

    // 管理端

    // 管理员登录
    String adminLogin(AdminLoginDto adminLoginDto);

    // 管理端获取用户信息
    SysUserVo getAdminInfo();

    // 管理端获取用户列表
    List<SysUserVo> listUser();

    // 管理端获取用户列表（包含文章数量）
    List<SysUserWithArticleCountVo> listUserWithArticleCount();

    // 管理端更新用户
    void updateUser(SysUserDto sysUserDto);

    // 管理端删除用户
    void deleteUser(Integer userId);

    // 管理端搜索用户
    List<SysUserVo> searchUser(SysUserSearchDTO sysUserSearchDTO);

    // 管理的获取用户详情
    SysUserDetailVo getUserInfo(Integer userId);
}
