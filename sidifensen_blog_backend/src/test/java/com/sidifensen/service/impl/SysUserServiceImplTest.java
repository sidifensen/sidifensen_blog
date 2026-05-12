package com.sidifensen.service.impl;

import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.LoginStatusEnum;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.NotificationThreadPool;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.security.SysUserDetailsService;
import com.sidifensen.service.IpService;
import com.sidifensen.service.SysLoginLogService;
import com.sidifensen.service.SysUserRoleService;
import com.sidifensen.service.UserSettingsService;
import com.sidifensen.service.VipService;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SysUserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private RedisComponent redisComponent;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SysUserDetailsService sysUserDetailsService;

    @Mock
    private IpUtils ipUtils;

    @Mock
    private IpService ipService;

    @Mock
    private SysUserRoleService sysUserRoleService;

    @Mock
    private SysLoginLogService sysLoginLogService;

    @Mock
    private VipService vipService;

    @Mock
    private UserSettingsService userSettingsService;

    @Mock
    private NotificationThreadPool notificationThreadPool;

    @Spy
    private SysUserServiceImpl sysUserService;

    private void setField(Object target, String name, Object value) throws Exception {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
        }
        Class<?> superClass = target.getClass().getSuperclass();
        while (superClass != null) {
            for (Field field : superClass.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    field.set(target, value);
                    return;
                }
            }
            superClass = superClass.getSuperclass();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        sysUserService = spy(new SysUserServiceImpl());
        setField(sysUserService, "authenticationManager", authenticationManager);
        setField(sysUserService, "jwtUtils", jwtUtils);
        setField(sysUserService, "sysUserMapper", sysUserMapper);
        setField(sysUserService, "redisComponent", redisComponent);
        setField(sysUserService, "rabbitTemplate", rabbitTemplate);
        setField(sysUserService, "passwordEncoder", passwordEncoder);
        setField(sysUserService, "sysUserDetailsService", sysUserDetailsService);
        setField(sysUserService, "ipUtils", ipUtils);
        setField(sysUserService, "ipService", ipService);
        setField(sysUserService, "sysUserRoleService", sysUserRoleService);
        setField(sysUserService, "sysLoginLogService", sysLoginLogService);
        setField(sysUserService, "vipService", vipService);
        setField(sysUserService, "userSettingsService", userSettingsService);
        setField(sysUserService, "notificationThreadPool", notificationThreadPool);
    }

    @Test
    @DisplayName("登录失败时只记录失败日志，不写成功副作用")
    void login_FailedAuthentication_ShouldNotWriteSuccessSideEffects() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("tester");
        loginDto.setPassword("secret123");
        loginDto.setRememberMe(false);
        loginDto.setCheckCodeKey("key");
        loginDto.setCheckCode("ABCD");

        when(redisComponent.getCheckCode("key")).thenReturn("ABCD");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));
        when(ipUtils.getIp()).thenReturn("127.0.0.1");

        assertThrows(BadCredentialsException.class, () -> sysUserService.login(loginDto));

        verify(sysUserMapper, never()).updateById((SysUser) any(SysUser.class));
        verify(ipService, never()).setLoginIp(any(), any());
        verify(sysLoginLogService, never()).recordLoginLog(any(), any(), any(), any(), eq(LoginStatusEnum.SUCCESS.getCode()));
        verify(sysLoginLogService).recordLoginLog(
                eq(null),
                eq("tester"),
                eq(RegisterOrLoginTypeEnum.EMAIL.getCode()),
                eq("127.0.0.1"),
                eq(LoginStatusEnum.FAIL.getCode())
        );
    }

    @Test
    @DisplayName("登录成功时应写入成功副作用并返回 token")
    void login_SuccessfulAuthentication_ShouldWriteSuccessSideEffects() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("tester");
        loginDto.setPassword("secret123");
        loginDto.setRememberMe(true);
        loginDto.setCheckCodeKey("key");
        loginDto.setCheckCode("ABCD");

        SysUser sysUser = new SysUser()
                .setId(1)
                .setUsername("tester")
                .setLoginType(RegisterOrLoginTypeEnum.EMAIL.getCode());
        LoginUser loginUser = new LoginUser(sysUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        when(redisComponent.getCheckCode("key")).thenReturn("ABCD");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(ipUtils.getIp()).thenReturn("127.0.0.1");
        when(ipUtils.getAddress()).thenReturn("中国-上海");
        when(jwtUtils.createToken(1, true)).thenReturn("jwt-token");
        when(sysUserMapper.updateById((SysUser) any(SysUser.class))).thenReturn(1);

        String token = sysUserService.login(loginDto);

        assertEquals("jwt-token", token);
        verify(sysUserMapper).updateById((SysUser) any(SysUser.class));
        verify(ipService).setLoginIp(1, "127.0.0.1");
        verify(sysLoginLogService).recordLoginLog(
                eq(1),
                eq("tester"),
                eq(RegisterOrLoginTypeEnum.EMAIL.getCode()),
                eq("127.0.0.1"),
                eq(LoginStatusEnum.SUCCESS.getCode())
        );
    }
}
