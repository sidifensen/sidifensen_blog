package com.sidifensen.domain.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sidifensen
 * @since 2025-08-12
 */
@Data
@Component
@ConfigurationProperties(value = "oauth.github")
public class Github {
    private String clientId; // 客户端id
    private String clientSecret; // 密钥
    private String redirectUri; // 登陆后后的回调地址
}
