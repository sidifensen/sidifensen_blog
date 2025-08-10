package com.sidifensen.controller;

import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ISysUserService;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-06-29
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 登录时的图片验证码
     *
     * @return
     */
    @GetMapping("/checkCode")
    public Result checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 32);
        // checkCode是验证码的值
        String checkCode = captcha.text();
        //checkCodeKey是验证码的唯一标识，验证码存入redis
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
        return Result.success(jwt, "登录成功");
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
    @PostMapping("/verifyReset")
    public Result verifyReset(@RequestBody @Valid VerifyResetDto verifyResetDto) {
        sysUserService.verifyReset(verifyResetDto);
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
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    public Result info() {
        SysUserVo sysUserVo = sysUserService.info();
        return Result.success(sysUserVo);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("用户删除成功");
    }

    // 管理端

    /**
     * 管理端登录
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
     * @return
     */
    @GetMapping("/admin/info")
    public Result adminInfo() {
        SysUserVo sysUserVo = sysUserService.getAdminInfo();
        return Result.success(sysUserVo);
    }

    /**
     * 管理端获取用户列表
     * @return
     */
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/admin/list")
    public Result list() {
        List<SysUserVo> sysUserVos = sysUserService.listUser();
        return Result.success(sysUserVos);
    }

}
