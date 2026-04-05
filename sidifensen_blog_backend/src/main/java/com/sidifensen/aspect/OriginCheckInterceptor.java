package com.sidifensen.aspect;

import com.sidifensen.config.SidifensenConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 请求来源校验拦截器
 * 防止跨站请求伪造和非法爬虫/滥用
 * 仅允许来自已注册域名的请求通过
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Slf4j
@Component
public class OriginCheckInterceptor implements HandlerInterceptor {

    @Resource
    private SidifensenConfig sidifensenConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求来源
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // 预检请求(OPTIONS)放行，由CORS处理器管理
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取允许的域名列表
        List<String> allowOrigins = sidifensenConfig.getAllowOrigins();

        // 优先验证 Origin
        if (origin != null && !origin.isEmpty()) {
            if (isOriginAllowed(origin, allowOrigins)) {
                return true;
            }
        }

        // 如果没有 Origin，验证 Referer
        if (referer != null && !referer.isEmpty()) {
            if (isRefererAllowed(referer, allowOrigins)) {
                return true;
            }
        }

        // 如果都没有（可能是直接访问），记录警告并拒绝
        log.warn("拒绝非法请求: Origin={}, Referer={}, URI={}, IP={}",
                origin, referer, request.getRequestURI(), getClientIp(request));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"message\":\"访问被拒绝\"}");
        return false;
    }

    /**
     * 验证 Origin 是否在允许列表中
     */
    private boolean isOriginAllowed(String origin, List<String> allowOrigins) {
        if (allowOrigins == null || allowOrigins.isEmpty()) {
            return false;
        }
        for (String allowed : allowOrigins) {
            if (origin.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证 Referer 是否在允许列表中
     * Referer 可能包含路径，需要提取主机部分进行比较
     */
    private boolean isRefererAllowed(String referer, List<String> allowOrigins) {
        if (allowOrigins == null || allowOrigins.isEmpty()) {
            return false;
        }
        // 提取 Referer 中的 origin（协议+域名+端口）
        String refererOrigin = extractOrigin(referer);
        if (refererOrigin == null) {
            return false;
        }
        for (String allowed : allowOrigins) {
            if (refererOrigin.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 URL 中提取 origin（协议+域名+端口）
     */
    private String extractOrigin(String url) {
        try {
            if (url.startsWith("//")) {
                url = "https:" + url;
            }
            java.net.URL parsed = new java.net.URL(url);
            int port = parsed.getPort();
            String origin = parsed.getProtocol() + "://" + parsed.getHost();
            if (port != -1 && port != 443 && port != 80) {
                origin += ":" + port;
            }
            return origin;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
