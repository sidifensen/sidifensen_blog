package com.sidifensen.controller;


import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.dto.RegisterDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.exception.BlogException;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ISysUserService;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 *  前端控制器
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

    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        String jwt = sysUserService.login(loginDto);
        return Result.success(jwt);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto registerDto){
        boolean register = sysUserService.register(registerDto);
        return Result.success(register);
    }

    @GetMapping("/checkCode")
    public Result checkCode(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 32);
        // checkCode是验证码的值
        String checkCode = captcha.text();
        //checkCodeKey是验证码的唯一标识，验证码存入redis
        String checkCodeKey = redisComponent.saveCheckCode(checkCode);
        String checkCodeBase64 = captcha.toBase64();
        // 将验证码图片(base64)和验证码key存入map，返回给前端
        Map<String,Object> result = new HashMap<>();
        result.put("checkCodeBase64", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return Result.success(result);
    }

    @GetMapping("info")
    public Result info() {
        return Result.success("info");
    }

}
