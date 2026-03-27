package com.sidifensen.domain.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 *
 * @author sidifensen
 * @since 2026-03-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth.wechat")
public class Wechat {

    /**
     * 微信小程序 AppID
     */
    private String appId;

    /**
     * 微信小程序 AppSecret
     */
    private String appSecret;

    /**
     * 登录凭证校验接口地址
     */
    private String jscode2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
}
