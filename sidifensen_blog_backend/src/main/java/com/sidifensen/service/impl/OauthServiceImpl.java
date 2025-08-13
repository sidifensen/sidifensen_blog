package com.sidifensen.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.ISysUserRoleService;
import com.sidifensen.service.IpService;
import com.sidifensen.service.OauthService;
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
public class OauthServiceImpl implements OauthService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private IpService ipService;

    @Resource
    private ISysUserRoleService sysUserRoleService;

    @Override
    public String login(AuthResponse authResponse, HttpServletResponse request, Integer type) {
        if (authResponse.getCode() == 2000) {
            AuthUser authUser = (AuthUser) authResponse.getData();
            String password = passwordEncoder.encode(authUser.getToken().getAccessToken());
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, authUser.getUuid()).eq(ObjectUtil.isNotEmpty(authUser.getEmail()), SysUser::getEmail, authUser.getEmail());
            SysUser user = sysUserMapper.selectOne(queryWrapper);

            String ip = ipUtils.getIp();
            if (ObjectUtil.isEmpty(user)) {
                // 注册
                SysUser sysUser = new SysUser();
                sysUser.setUsername(authUser.getUsername() + "_" + authUser.getUuid());
                sysUser.setPassword(password);
                if (ObjectUtil.isNotEmpty(authUser.getEmail())) {
                    sysUser.setEmail(authUser.getEmail());
                }
                sysUser.setNickname(authUser.getNickname());
                sysUser.setAvatar(authUser.getAvatar());
                if (ObjectUtil.isNotEmpty(authUser.getRemark())) {
                    sysUser.setIntroduction(authUser.getRemark());
                }
                sysUser.setRegisterIp(ip);
                sysUser.setRegisterType(type);
                sysUser.setLoginTime(new Date());
                sysUser.setLoginType(type);
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

            return "?user_name=" + authUser.getUsername() + "_" + authUser.getUuid() + "&access_token=" + authUser.getToken().getAccessToken();
        } else {
            log.error("oauth登录失败：{}", authResponse.getMsg());
            throw new BlogException("oauth登录失败：" + authResponse.getMsg());
        }
    }
}
