package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.*;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.OauthService;
import com.sidifensen.service.SysUserService;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private OauthService oauthService;

    /**
     * 登录时的图片验证码
     *
     * @return
     */
    @GetMapping("/checkCode")
    public Result checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 32);
        // 只生成加法运算，避免出现负数结果
        captcha.setCharType(2);
        // checkCode是验证码的值
        String checkCode = captcha.text();
        // checkCodeKey是验证码的唯一标识，验证码存入redis
        String checkCodeKey = redisComponent.saveCheckCode(checkCode);
        String checkCodeBase64 = captcha.toBase64();
        // 将验证码图片(base64)和验证码key存入map，返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("checkCodeBase64", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return Result.success(result);
    }

    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid LoginDto loginDto) {
        String jwt = sysUserService.login(loginDto);
        return Result.success(jwt);
    }

    /**
     * OAuth 票据兑换
     *
     * @param oauthExchangeDto 一次性票据信息
     * @return JWT
     */
    @PostMapping("/oauth/exchange")
    public Result oauthExchange(@RequestBody @Valid OauthExchangeDto oauthExchangeDto) {
        String jwt = oauthService.exchangeOauthTicket(oauthExchangeDto);
        return Result.success(jwt);
    }

    /**
     * 邮件发送
     */
    @PostMapping("/sendEmail")
    public Result sendEmail(@RequestBody @Valid EmailDto emailDto) {
        sysUserService.sendEmailCheckCode(emailDto);
        return Result.success();
    }

    /**
     * 注册
     *
     * @param registerDto
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid RegisterDto registerDto) {
        sysUserService.register(registerDto);
        return Result.success();
    }

    /**
     * 重置密码时校验邮箱验证码
     */
    @PostMapping("/verifyResetPassword")
    public Result verifyResetPassword(@RequestBody @Valid VerifyResetDto verifyResetDto) {
        sysUserService.verifyResetPassword(verifyResetDto);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        sysUserService.resetPassword(resetPasswordDto);
        return Result.success();
    }

    /**
     * 修改邮箱时校验邮箱验证码
     */
    @PostMapping("/verifyResetEmail")
    public Result verifyResetEmail(@RequestBody @Valid VerifyEmailDto verifyEmailDto) {
        sysUserService.verifyResetEmail(verifyEmailDto);
        return Result.success();
    }

    /**
     * 重置邮箱
     *
     * @param updateEmailDto 邮箱信息
     * @return
     */
    @PutMapping("/resetEmail")
    public Result resetEmail(@Valid @RequestBody UpdateEmailDto updateEmailDto) {
        sysUserService.resetEmail(updateEmailDto);
        return Result.success();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @RateLimit
    @GetMapping("/info")
    public Result info() {
        SysUserVo sysUserVo = sysUserService.info();
        return Result.success(sysUserVo);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return
     */
    @RateLimit
    @GetMapping("/info/{userId}")
    public Result getUserInfoById(@PathVariable @NotNull(message = "用户ID不能为空") Integer userId) {
        SysUserVo sysUserVo = sysUserService.getUserInfoById(userId);
        return Result.success(sysUserVo);
    }

    /**
     * 更新当前用户信息
     *
     * @param updateUserInfoDto 用户信息
     * @return
     */
    @PutMapping("/info")
    public Result updateUserInfo(@Valid @RequestBody UpdateUserInfoDto updateUserInfoDto) {
        sysUserService.updateUserInfo(updateUserInfoDto);
        return Result.success();
    }

    /**
     * 获取推荐作者列表（按文章数量排序的活跃作者）
     *
     * @param limit 返回数量限制，默认 10
     * @return 推荐作者列表
     */
    @RateLimit
    @GetMapping("/authors/recommended")
    public Result getRecommendedAuthors(@RequestParam(defaultValue = "10") Integer limit) {
        List<SysUserVo> authors = sysUserService.getRecommendedAuthors(limit);
        return Result.success(authors);
    }

    /**
     * 获取社区统计数据（文章总数、用户总数、总阅读量、活跃作者数）
     *
     * @return 社区统计数据
     */
    @RateLimit
    @GetMapping("/community/stats")
    public Result getCommunityStats() {
        Map<String, Object> stats = sysUserService.getCommunityStats();
        return Result.success(stats);
    }

    /**
     * 获取热门搜索列表（基于搜索频率统计）
     *
     * @param limit 返回数量限制，默认 10
     * @return 热门搜索列表
     */
    @RateLimit
    @GetMapping("/search/hot")
    public Result getHotSearches(@RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(limit);
        return Result.success(hotSearches);
    }

    // 管理端

    /**
     * 管理端登录
     *
     * @param AdminLoginDto
     * @return
     */
    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody @Valid AdminLoginDto AdminLoginDto) {
        String jwt = sysUserService.adminLogin(AdminLoginDto);
        return Result.success(jwt);
    }

    /**
     * 管理端获取登录用户信息
     *
     * @return
     */
    @GetMapping("/admin/info")
    public Result adminInfo() {
        SysUserVo sysUserVo = sysUserService.getAdminInfo();
        return Result.success(sysUserVo);
    }

    /**
     * 管理端获取用户列表
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SELECT, description = "管理员获取用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/list")
    public Result listUser() {
        List<SysUserVo> sysUserVos = sysUserService.listUser();
        return Result.success(sysUserVos);
    }

    /**
     * 管理端分页获取用户列表
     *
     * @return 用户分页列表
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SELECT, description = "管理员分页获取用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/page")
    public Result<PageVo<List<SysUserVo>>> pageUser(
            @RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
            @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize) {
        PageVo<List<SysUserVo>> sysUserVos = sysUserService.pageUser(pageNum, pageSize);
        return Result.success(sysUserVos);
    }

    /**
     * 管理端获取用户列表（包含文章数量）
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SELECT, description = "管理员获取用户列表（含文章数量）")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/listWithArticleCount")
    public Result listUserWithArticleCount() {
        List<SysUserWithArticleCountVo> sysUserVos = sysUserService.listUserWithArticleCount();
        return Result.success(sysUserVos);
    }

    /**
     * 管理端获取用户列表（包含评论数量）
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SELECT, description = "管理员获取用户列表（含评论数量）")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/listWithCommentCount")
    public Result listUserWithCommentCount() {
        List<SysUserWithCommentCountVo> sysUserVos = sysUserService.listUserWithCommentCount();
        return Result.success(sysUserVos);
    }

    /**
     * 管理端获取用户列表（包含专栏数量）
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SELECT, description = "管理员获取用户列表（含专栏数量）")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/listWithColumnCount")
    public Result listUserWithColumnCount() {
        List<SysUserWithColumnCountVo> sysUserVos = sysUserService.listUserWithColumnCount();
        return Result.success(sysUserVos);
    }

    /**
     * 管理端修改用户
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.UPDATE, description = "管理员修改用户信息")
    @PreAuthorize("hasAuthority('system:user:update')")
    @PostMapping("/admin/update")
    public Result updateUser(@RequestBody @Valid SysUserDto sysUserDto) {
        sysUserService.updateUser(sysUserDto);
        return Result.success();
    }

    /**
     * 管理端删除用户
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.DELETE, description = "管理员删除用户")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @DeleteMapping("/admin/{userId}")
    public Result deleteUser(@PathVariable @NotNull(message = "用户ID不能为空") Integer userId) {
        sysUserService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 管理端搜索用户
     *
     * @return
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索用户")
    @PreAuthorize("hasAuthority('system:user:search')")
    @PostMapping("/admin/search")
    public Result searchUser(@RequestBody @Valid SysUserSearchDTO sysUserSearchDTO) {
        List<SysUserVo> sysUserVos = sysUserService.searchUser(sysUserSearchDTO);
        return Result.success(sysUserVos);
    }

    /**
     * 管理端分页搜索用户
     *
     * @return 用户分页列表
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.SEARCH, description = "管理员分页搜索用户")
    @PreAuthorize("hasAuthority('system:user:search')")
    @PostMapping("/admin/page/search")
    public Result<PageVo<List<SysUserVo>>> searchUserPage(@RequestBody @Valid SysUserSearchDTO sysUserSearchDTO) {
        PageVo<List<SysUserVo>> sysUserVos = sysUserService.searchUserPage(sysUserSearchDTO);
        return Result.success(sysUserVos);
    }

    /**
     * 管理端获取用户详细信息
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.GET, description = "管理员获取用户详细信息")
    @PreAuthorize("hasAuthority('system:user:info')")
    @GetMapping("/admin/{userId}")
    public Result getUserInfo(@PathVariable @NotNull(message = "用户ID不能为空") Integer userId) {
        SysUserDetailVo sysUserDetailVo = sysUserService.getUserInfo(userId);
        return Result.success(sysUserDetailVo);
    }

    /**
     * 管理端获取用户总数统计
     *
     * @return 用户总数
     */
    @OperationLog(module = "用户管理", type = OperationTypeEnum.GET, description = "管理员获取用户总数统计")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/count")
    public Result getUserTotalCount() {
        Long totalCount = sysUserService.getUserTotalCount();
        return Result.success(totalCount);
    }

}
