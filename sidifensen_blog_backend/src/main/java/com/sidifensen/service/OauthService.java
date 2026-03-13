package com.sidifensen.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.OauthExchangeDto;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.LoginStatusEnum;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.enums.StatusEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.JwtUtils;
import jakarta.annotation.Resource;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 处理 OAuth 回调，创建本地用户并生成一次性票据
     *
     * @param authResponse OAuth 回调响应
     * @param code         登录方式编码
     * @return 一次性票据
     */
    public String login(AuthResponse<?> authResponse, Integer code) {
        String ip = ipUtils.getIp();
        String loginType = RegisterOrLoginTypeEnum.getType(code);

        if (authResponse.getCode() != 2000) {
            throw new BlogException(BlogConstants.OauthLoginError + "：" + authResponse.getMsg());
        }

        AuthUser authUser = (AuthUser) authResponse.getData();
        if (authUser == null || ObjectUtil.isEmpty(authUser.getUuid())) {
            throw new BlogException(BlogConstants.OauthLoginError);
        }

        // 以登录方式前缀 + 第三方唯一标识生成本地账号，避免暴露第三方令牌
        String username = buildOauthUsername(loginType, authUser.getUuid());
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        if (ObjectUtil.isEmpty(user)) {
            SysUser sysUser = new SysUser();
            sysUser.setUsername(username);
            // 为 OAuth 注册用户生成站内随机密码，不再使用第三方 access_token 作为本地密码
            sysUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString().replace("-", "")));
            sysUser.setNickname(ObjectUtil.isEmpty(authUser.getNickname()) ? username : authUser.getNickname());
            sysUser.setAvatar(authUser.getAvatar());
            if (ObjectUtil.isNotEmpty(authUser.getRemark())) {
                sysUser.setIntroduction(authUser.getRemark());
            }
            sysUser.setStatus(StatusEnum.NORMAL.getStatus());
            sysUser.setRegisterIp(ip);
            sysUser.setRegisterAddress(ipUtils.getAddress(ip));
            sysUser.setRegisterType(code);
            sysUser.setLoginTime(new Date());
            sysUser.setLoginType(code);
            sysUser.setLoginIp(ip);
            sysUser.setLoginAddress(ipUtils.getAddress(ip));
            int insert = sysUserMapper.insert(sysUser);
            if (insert <= 0) {
                throw new BlogException(BlogConstants.OauthLoginError);
            }

            ipService.setRegisterIp(sysUser.getId(), ip);
            sysUserRoleService.setRegisterRole(sysUser.getId());
            sysLoginLogService.recordLoginLog(sysUser.getId(), username, code, ip, LoginStatusEnum.SUCCESS.getCode());
            return redisComponent.saveOauthTicket(sysUser.getId(), code);
        }

        if (StatusEnum.DISABLE.getStatus().equals(user.getStatus())) {
            throw new BlogException(BlogConstants.UserDisabled);
        }

        // OAuth 登录只更新登录态相关字段，不再改写本地密码
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setLoginTime(new Date());
        updateUser.setLoginType(code);
        updateUser.setLoginIp(ip);
        updateUser.setLoginAddress(ipUtils.getAddress(ip));
        int update = sysUserMapper.updateById(updateUser);
        if (update <= 0) {
            throw new BlogException(BlogConstants.OauthLoginError);
        }

        ipService.setLoginIp(user.getId(), ip);
        sysLoginLogService.recordLoginLog(user.getId(), username, code, ip, LoginStatusEnum.SUCCESS.getCode());
        return redisComponent.saveOauthTicket(user.getId(), code);
    }

    /**
     * 兑换 OAuth 一次性票据，签发本站 JWT
     *
     * @param oauthExchangeDto 票据信息
     * @return 本站 JWT
     */
    public String exchangeOauthTicket(OauthExchangeDto oauthExchangeDto) {
        Map<String, Integer> ticketInfo = redisComponent.consumeOauthTicket(oauthExchangeDto.getTicket());
        if (ObjectUtil.isEmpty(ticketInfo)) {
            throw new BlogException(BlogConstants.OauthTicketInvalid);
        }

        Integer userId = ticketInfo.get("userId");
        if (ObjectUtil.isEmpty(userId)) {
            throw new BlogException(BlogConstants.OauthTicketInvalid);
        }

        SysUser sysUser = sysUserMapper.selectById(userId);
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        if (StatusEnum.DISABLE.getStatus().equals(sysUser.getStatus())) {
            throw new BlogException(BlogConstants.UserDisabled);
        }

        return jwtUtils.createToken(userId, true);
    }

    /**
     * 生成本地 OAuth 用户名，确保不超过数据库字段长度
     *
     * @param loginType 登录方式
     * @param uuid      第三方唯一标识
     * @return 本地用户名
     */
    private String buildOauthUsername(String loginType, String uuid) {
        String prefix = loginType + "_";
        int maxUuidLength = 20 - prefix.length();
        String normalizedUuid = uuid;
        if (uuid.length() > maxUuidLength) {
            normalizedUuid = uuid.substring(0, maxUuidLength);
        }
        return prefix + normalizedUuid;
    }
}
