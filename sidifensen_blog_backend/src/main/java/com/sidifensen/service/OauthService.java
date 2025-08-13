package com.sidifensen.service;

import jakarta.servlet.http.HttpServletResponse;
import me.zhyd.oauth.model.AuthResponse;

/**
 * @author sidifensen
 * @since 2025-08-13
 */
public interface OauthService {

    String login(AuthResponse authResponse, HttpServletResponse request, Integer registerType);

}
