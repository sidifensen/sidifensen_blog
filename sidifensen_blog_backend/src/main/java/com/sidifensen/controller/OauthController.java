package com.sidifensen.controller;

import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.oauth.Gitee;
import com.sidifensen.domain.oauth.Github;
import com.sidifensen.service.OauthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author sidifensen
 * @since 2025-08-12
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
public class OauthController {

    @Resource
    private Gitee gitee;

    @Resource
    private Github github;

    @Resource
    private OauthService oauthService;

    @Value(("${frontend.host}"))
    private String frontendHost;

    @GetMapping("/gitee/login")
    public void giteeLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getGiteeAuthRequest();
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/gitee/callback")
    public void giteeCallback(AuthCallback callback, HttpServletResponse request) throws IOException {
        AuthRequest authRequest = getGiteeAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        String url = oauthService.login(authResponse, request, RegisterOrLoginTypeEnum.GITEE.getRegisterType());
        request.sendRedirect(frontendHost + url);
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
        String url = oauthService.login(authResponse, request, RegisterOrLoginTypeEnum.GITHUB.getRegisterType());
        request.sendRedirect(frontendHost + url);
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
}
