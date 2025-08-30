package com.sidifensen.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sidifensen")
@Setter
public class SidifensenConfig {

    private List<String> allowOrigins;
    private boolean photoAutoAudit;
    private boolean articleAutoAudit;

    public List<String> getAllowOrigins() {
        return this.allowOrigins;
    }

    public boolean isPhotoAutoAudit() {
        return this.photoAutoAudit;
    }

    public boolean isArticleAutoAudit() {
        return this.articleAutoAudit;
    }

}