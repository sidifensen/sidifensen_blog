package com.sidifensen.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.LoginStatusEnum;
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

    @Resource
    private SysLoginLogService sysLoginLogService;

    public String login(AuthResponse<?> authResponse, HttpServletResponse request, Integer code) {
        String ip = ipUtils.getIp();
        String loginType = RegisterOrLoginTypeEnum.getType(code);

        if (authResponse.getCode() == 2000) {
            AuthUser authUser = (AuthUser) authResponse.getData();
            // 确保用户名长度不超过20个字符（数据库字段限制）
            String uuid = authUser.getUuid();
            log.info("uuid: {}", uuid);
            String prefix = loginType + "_";
            int maxUuidLength = 20 - prefix.length();
            if (uuid.length() > maxUuidLength) {
                uuid = uuid.substring(0, maxUuidLength);
            }
            String username = prefix + uuid;

            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
            SysUser user = sysUserMapper.selectOne(queryWrapper);
            String password = passwordEncoder.encode(authUser.getToken().getAccessToken());

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
                    // 异步设置注册IP
                    ipService.setRegisterIp(sysUser.getId(), ip);
                    // 异步设置角色
                    sysUserRoleService.setRegisterRole(sysUser.getId());
                    // 异步记录注册成功日志（使用去重方法避免重复插入）
                    sysLoginLogService.recordOrUpdateLoginLog(sysUser.getId(), username, code, ip, LoginStatusEnum.SUCCESS.getCode());
                }
            } else {
                // 登录
                SysUser sysUser = new SysUser();
                sysUser.setId(user.getId());
                sysUser.setPassword(password);
                sysUserMapper.updateById(sysUser);
                // 异步设置登录IP
                ipService.setLoginIp(sysUser.getId(), ip);
                // 异步记录登录成功日志（使用去重方法避免重复插入）
                sysLoginLogService.recordOrUpdateLoginLog(user.getId(), username, code, ip, LoginStatusEnum.SUCCESS.getCode());
            }

            switch (code) {
                case 1:
                    return "?login_type=gitee&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
                case 2:
                    return "?login_type=github&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
                case 3:
                    return "?login_type=qq&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
            }
            return "?login_type=gitee&user_name=" + username + "&access_token=" + authUser.getToken().getAccessToken();
        } else {
            throw new BlogException(BlogConstants.OauthLoginError + "：" + authResponse.getMsg());
        }
    }
}
