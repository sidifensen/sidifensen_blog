package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysUserDetailVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.domain.vo.SysUserWithArticleCountVo;
import com.sidifensen.domain.vo.SysUserWithCommentCountVo;
import com.sidifensen.domain.vo.SysUserWithColumnCountVo;

import java.util.List;
import java.util.Map;

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
    void verifyResetPassword(VerifyResetDto verifyResetDto);

    /**
     * 重置密码
     *
     * @param resetPasswordDto
     */
    void resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 修改邮箱时校验邮箱验证码
     *
     * @param verifyEmailDto 邮箱校验信息
     */
    void verifyResetEmail(VerifyEmailDto verifyEmailDto);

    /**
     * 重置邮箱
     *
     * @param updateEmailDto 邮箱信息
     */
    void resetEmail(UpdateEmailDto updateEmailDto);

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

    /**
     * 更新当前用户信息
     *
     * @param updateUserInfoDto 用户信息
     */
    void updateUserInfo(UpdateUserInfoDto updateUserInfoDto);

    // 管理端

    // 管理员登录
    String adminLogin(AdminLoginDto adminLoginDto);

    // 管理端获取用户信息
    SysUserVo getAdminInfo();

    // 管理端获取用户列表
    List<SysUserVo> listUser();

    // 管理端获取用户列表（包含文章数量）
    List<SysUserWithArticleCountVo> listUserWithArticleCount();

    // 管理端获取用户列表（包含评论数量）
    List<SysUserWithCommentCountVo> listUserWithCommentCount();

    // 管理端获取用户列表（包含专栏数量）
    List<SysUserWithColumnCountVo> listUserWithColumnCount();

    // 管理端分页获取用户列表
    PageVo<List<SysUserVo>> pageUser(Integer pageNum, Integer pageSize);

    // 管理端更新用户
    void updateUser(SysUserDto sysUserDto);

    // 管理端删除用户
    void deleteUser(Integer userId);

    // 管理端搜索用户
    List<SysUserVo> searchUser(SysUserSearchDTO sysUserSearchDTO);

    // 管理端分页搜索用户
    PageVo<List<SysUserVo>> searchUserPage(SysUserSearchDTO sysUserSearchDTO);

    // 管理的获取用户详情
    SysUserDetailVo getUserInfo(Integer userId);

    // 管理端获取用户总数统计
    Long getUserTotalCount();

    /**
     * 获取推荐作者列表（按文章数量排序的活跃作者）
     *
     * @param limit 返回数量限制
     * @return 推荐作者列表
     */
    List<SysUserVo> getRecommendedAuthors(Integer limit);

    /**
     * 获取社区统计数据（文章总数、用户总数、总阅读量、活跃作者数）
     *
     * @return 社区统计数据 Map
     */
    Map<String, Object> getCommunityStats();

    /**
     * 获取热门搜索列表（基于搜索频率统计）
     *
     * @param limit 返回数量限制
     * @return 热门搜索列表
     */
    List<Map<String, Object>> getHotSearches(Integer limit);

}
