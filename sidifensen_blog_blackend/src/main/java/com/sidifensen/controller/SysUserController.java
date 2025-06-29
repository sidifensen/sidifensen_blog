package com.sidifensen.controller;


import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.ISysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("login")
    public Result login(LoginDto loginDto) {
        String jwt = sysUserService.login(loginDto);
        return Result.success(jwt);
    }

}
