package com.sidifensen.service.impl;

import com.sidifensen.domain.dto.WechatLoginDto;
import com.sidifensen.domain.oauth.Wechat;
import com.sidifensen.exception.BlogException;
import com.sidifensen.service.OauthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OauthService 业务逻辑单元测试
 *
 * @author sidifensen
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OauthServiceTest {

    @Mock
    private Wechat wechat;

    @Spy
    private OauthService oauthService;

    private void setField(Object target, String name, Object value) throws Exception {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (f.getName().equals(name)) {
                f.setAccessible(true);
                f.set(target, value);
                return;
            }
        }
    }

    private void setupOauthService() throws Exception {
        setField(oauthService, "wechat", wechat);
    }

    @Nested
    @DisplayName("wechatMiniprogramLogin 微信小程序登录测试")
    class WechatMiniprogramLoginTests {

        @Test
        @DisplayName("jscode2sessionUrl 为空 - 应抛出 OAuth 登录异常")
        void wechatMiniprogramLogin_Jscode2sessionUrlNull_ThrowsException() throws Exception {
            setupOauthService();
            when(wechat.getJscode2sessionUrl()).thenReturn(null);

            WechatLoginDto loginDto = new WechatLoginDto();
            loginDto.setCode("test_code");

            BlogException e = assertThrows(BlogException.class,
                    () -> oauthService.wechatMiniprogramLogin(loginDto));
            assertEquals("OAuth登录失败", e.getMessage());
        }

        @Test
        @DisplayName("appId 为空 - 应抛出 OAuth 登录异常")
        void wechatMiniprogramLogin_AppIdNull_ThrowsException() throws Exception {
            setupOauthService();
            when(wechat.getJscode2sessionUrl()).thenReturn("https://api.weixin.qq.com/sns/jscode2session");
            when(wechat.getAppId()).thenReturn(null);

            WechatLoginDto loginDto = new WechatLoginDto();
            loginDto.setCode("test_code");

            BlogException e = assertThrows(BlogException.class,
                    () -> oauthService.wechatMiniprogramLogin(loginDto));
            assertEquals("OAuth登录失败", e.getMessage());
        }

        @Test
        @DisplayName("appSecret 为空 - 应抛出 OAuth 登录异常")
        void wechatMiniprogramLogin_AppSecretNull_ThrowsException() throws Exception {
            setupOauthService();
            when(wechat.getJscode2sessionUrl()).thenReturn("https://api.weixin.qq.com/sns/jscode2session");
            when(wechat.getAppId()).thenReturn("test_appid");
            when(wechat.getAppSecret()).thenReturn(null);

            WechatLoginDto loginDto = new WechatLoginDto();
            loginDto.setCode("test_code");

            BlogException e = assertThrows(BlogException.class,
                    () -> oauthService.wechatMiniprogramLogin(loginDto));
            assertEquals("OAuth登录失败", e.getMessage());
        }

        @Test
        @DisplayName("WechatLoginDto 包含昵称和头像")
        void wechatLoginDto_HasNicknameAndAvatar() {
            WechatLoginDto loginDto = new WechatLoginDto();
            loginDto.setCode("test_code");
            loginDto.setNickname("测试用户");
            loginDto.setAvatar("https://example.com/avatar.png");

            assertEquals("test_code", loginDto.getCode());
            assertEquals("测试用户", loginDto.getNickname());
            assertEquals("https://example.com/avatar.png", loginDto.getAvatar());
        }

        @Test
        @DisplayName("WechatLoginDto 昵称和头像为空")
        void wechatLoginDto_NicknameAndAvatarNull() {
            WechatLoginDto loginDto = new WechatLoginDto();
            loginDto.setCode("test_code");

            assertEquals("test_code", loginDto.getCode());
            assertNull(loginDto.getNickname());
            assertNull(loginDto.getAvatar());
        }
    }
}
