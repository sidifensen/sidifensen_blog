package com.sidifensen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sidifensen")
public class SidifensenConfig {

    private List<String> allowOrigins;
    private boolean photoAutoAudit;

    public List<String> getAllowOrigins() {
        return this.allowOrigins;
    }

    public boolean isPhotoAutoAudit() {
        return this.photoAutoAudit;
    }

    public void setAllowOrigins(List<String> allowOrigins) {
        this.allowOrigins = allowOrigins;
    }

    public void setPhotoAutoAudit(boolean photoAutoAudit) {
        this.photoAutoAudit = photoAutoAudit;
    }

}