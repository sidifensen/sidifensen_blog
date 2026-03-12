package com.sidifensen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * VIP 模块配置，目前只保留支付接入所需的支付宝参数。
 */
@Component
@ConfigurationProperties(prefix = "vip")
@Data
public class VipProperties {

    private Alipay alipay = new Alipay();

    /**
     * 支付宝接入配置项。
     */
    @Data
    public static class Alipay {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String gatewayUrl;
        private String notifyUrl;
        private String returnUrlPc;
        private String returnUrlH5;
        private Boolean sandbox = Boolean.TRUE;
    }
}
