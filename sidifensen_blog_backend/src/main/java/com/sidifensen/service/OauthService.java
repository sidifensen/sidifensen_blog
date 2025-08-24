package com.sidifensen.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OauthService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private IpService ipService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    public String login(AuthResponse authResponse, HttpServletResponse request, Integer code) {
        if (authResponse.getCode() == 2000) {
            AuthUser authUser = (AuthUser) authResponse.getData();
            String type = RegisterOrLoginTypeEnum.getType(code);

            String username = type + "_" + authUser.getUuid();
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
            SysUser user = sysUserMapper.selectOne(queryWrapper);
            String password = passwordEncoder.encode(authUser.getToken().getAccessToken());
            String ip = ipUtils.getIp();
            if (ObjectUtil.isEmpty(user)) {
                // 注册
                SysUser sysUser = new SysUser();
                sysUser.setUsername(username);
                sysUser.setPassword(password);
                sysUser.setNickname(authUser.getNickname());
                sysUser.setAvatar(authUser.getAvatar());
                if (ObjectUtil.isNotEmpty(authUser.getRemark())) {
                    sysUser.setIntroduction(authUser.getRemark());
                }
                sysUser.setRegisterIp(ip);
                sysUser.setRegisterType(code);
                sysUser.setLoginTime(new Date());
                sysUser.setLoginType(code);
                sysUser.setLoginIp(ip);
                int insert = sysUserMapper.insert(sysUser);
                if (insert > 0) {
                    ipService.setRegisterIp(sysUser.getId(), ip);
                    sysUserRoleService.setRegisterRole(sysUser.getId());
                }
            }
            // 登录
            SysUser sysUser = new SysUser();
            sysUser.setPassword(password);
            sysUserMapper.updateById(sysUser);
            ipService.setLoginIp(sysUser.getId(), ip);
            switch (code) {
                case 1:
                    return "?login_type=gitee&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
                case 2:
                    return "?login_type=github&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
            }
            return "?login_type=gitee&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
        } else {
            log.error("oauth登录失败：{}", authResponse.getMsg());
            throw new BlogException("oauth登录失败：" + authResponse.getMsg());
        }
    }
}
