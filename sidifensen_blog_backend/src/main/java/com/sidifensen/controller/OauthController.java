package com.sidifensen.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.oauth.Gitee;
import com.sidifensen.domain.oauth.Github;
import com.sidifensen.domain.oauth.QQ;
import com.sidifensen.domain.oauth.Wechat;
import com.sidifensen.service.OauthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidifensen.domain.dto.WechatLoginDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sidifensen
 * @since 2025-08-12
 */
@Slf4j
@RateLimit(10)
@RestController
@RequestMapping("/oauth")
public class OauthController {

    @Resource
    private Gitee gitee;

    @Resource
    private Github github;

    @Resource
    private QQ qq;

    @Resource
    private Wechat wechat;

    @Resource
    private OauthService oauthService;

    @Value(("${frontend.userHost}"))
    private String frontendUserHost;

    @GetMapping("/gitee/login")
    public void giteeLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getGiteeAuthRequest();
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/gitee/callback")
    public void giteeCallback(AuthCallback callback, HttpServletResponse request) throws IOException {
        log.info("【OAuth调试】Gitee callback 收到请求 - callback={}", callback);
        AuthRequest authRequest = getGiteeAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        log.info("【OAuth调试】Gitee authResponse - code={}, msg={}, data={}", authResponse.getCode(), authResponse.getMsg(), authResponse.getData());
        String ticket = oauthService.login(authResponse, RegisterOrLoginTypeEnum.GITEE.getCode());
        log.info("【OAuth调试】Gitee login 成功 - ticket={}", ticket);
        request.sendRedirect(buildFrontendCallbackUrl(ticket));
    }

    @GetMapping("/github/login")
    public void githubLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getGithubAuthRequest();
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/github/callback")
    public void githubCallback(AuthCallback callback, HttpServletResponse request) throws IOException {
        AuthRequest authRequest = getGithubAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        String ticket = oauthService.login(authResponse, RegisterOrLoginTypeEnum.GITHUB.getCode());
        request.sendRedirect(buildFrontendCallbackUrl(ticket));
    }

    @GetMapping("/qq/login")
    public void qqLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getQqAuthRequest();
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/qq/callback")
    public void qqCallback(AuthCallback callback, HttpServletResponse request) throws IOException {
        AuthRequest authRequest = getQqAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        String ticket = oauthService.login(authResponse, RegisterOrLoginTypeEnum.QQ.getCode());
        request.sendRedirect(buildFrontendCallbackUrl(ticket));
    }

    /**
     * 微信小程序登录
     * @param loginDto 微信小程序登录请求 DTO
     * @return 包含 ticket 的响应，用于前端兑换 JWT
     */
    @PostMapping("/wechat/login")
    public Map<String, Object> wechatLogin(@Valid @RequestBody WechatLoginDto loginDto) {
        // 调用微信接口获取 openid
        String jscode2sessionUrl = wechat.getJscode2sessionUrl() + "?appid=" + wechat.getAppId() + "&secret=" + wechat.getAppSecret() + "&js_code=" + loginDto.getCode() + "&grant_type=authorization_code";
        String result = HttpUtil.get(jscode2sessionUrl);
        JSONObject jsonResult = JSON.parseObject(result);

        // 检查微信返回的错误
        if (jsonResult.containsKey("errcode") && jsonResult.getInteger("errcode") != 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "微信登录失败：" + jsonResult.getString("errmsg"));
            return error;
        }

        String openid = jsonResult.getString("openid");
        if (openid == null || openid.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "获取 openid 失败");
            return error;
        }

        // 使用 openid 登录或注册
        String ticket = oauthService.wechatLogin(openid, RegisterOrLoginTypeEnum.WECHAT_MINIPROGRAM.getCode());

        // 返回 ticket 给前端
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", ticket);
        return response;
    }


    public AuthRequest getGiteeAuthRequest() {
        return new AuthGiteeRequest(AuthConfig.builder()
                .clientId(gitee.getClientId())
                .clientSecret(gitee.getClientSecret())
                .redirectUri(gitee.getRedirectUri())
                .build());
    }

    public AuthRequest getGithubAuthRequest() {
        return new AuthGithubRequest(AuthConfig.builder()
                .clientId(github.getClientId())
                .clientSecret(github.getClientSecret())
                .redirectUri(github.getRedirectUri())
                .build());
    }

    public AuthRequest getQqAuthRequest() {
        return new AuthQqRequest(AuthConfig.builder()
                .clientId(qq.getClientId())
                .clientSecret(qq.getClientSecret())
                .redirectUri(qq.getRedirectUri())
                .build());
    }

    /**
     * 构建前端 OAuth 回调地址，仅传递一次性票据
     *
     * @param ticket 一次性票据
     * @return 前端回调地址
     */
    private String buildFrontendCallbackUrl(String ticket) {
        String normalizedHost = frontendUserHost.endsWith("/")
                ? frontendUserHost.substring(0, frontendUserHost.length() - 1)
                : frontendUserHost;
        return UriComponentsBuilder.fromUriString(normalizedHost)
                .path("/oauth/callback")
                .queryParam("ticket", ticket)
                .build()
                .toUriString();
    }
}
