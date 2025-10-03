package com.sidifensen.config;

import com.sidifensen.aspect.BlacklistInterceptor;
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
    private BlacklistInterceptor blacklistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册黑名单拦截器
        registry.addInterceptor(blacklistInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                        "/error",                    // 排除错误页面
                        "/swagger-ui/**",            // 排除Swagger文档
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",              // 排除网站图标
                        "/static/**",                // 排除静态资源
                        "/public/**"                 // 排除公开资源
                );
    }
}

