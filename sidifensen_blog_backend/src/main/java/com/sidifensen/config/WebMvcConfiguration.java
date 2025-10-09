package com.sidifensen.config;

import com.sidifensen.aspect.SysBlacklistInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 注册自定义拦截器
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private SysBlacklistInterceptor sysBlacklistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册黑名单拦截器
        registry.addInterceptor(sysBlacklistInterceptor)
                .addPathPatterns("/**");  // 拦截所有请求
    }
}

