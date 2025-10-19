package com.sidifensen.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 浏览器指纹生成工具类
 * 通过收集浏览器的多个特征生成唯一指纹，用于区分不同访客
 *
 * @author sidifensen
 * @since 2025-09-19
 */
@Component
@Slf4j
public class FingerprintUtils {

    /**
     * 生成浏览器指纹
     *
     * @param request HTTP请求对象
     * @return 浏览器指纹字符串
     */
    public String generateFingerprint(HttpServletRequest request) {
        List<String> fingerprints = new ArrayList<>();

        // 1. User-Agent
        String userAgent = request.getHeader("User-Agent");
        fingerprints.add("ua:" + StrUtil.nullToEmpty(userAgent));

        // 2. Accept语言
        String acceptLanguage = request.getHeader("Accept-Language");
        fingerprints.add("lang:" + StrUtil.nullToEmpty(acceptLanguage));

        // 3. 客户端提示
        String chUa = request.getHeader("Sec-CH-UA");
        String chUaPlatform = request.getHeader("Sec-CH-UA-Platform");
        String chUaMobile = request.getHeader("Sec-CH-UA-Mobile");
        fingerprints.add("ch:" + StrUtil.nullToEmpty(chUa) + "|" +
                StrUtil.nullToEmpty(chUaPlatform) + "|" +
                StrUtil.nullToEmpty(chUaMobile));

        // 4. IP地址（作为辅助特征）
        String clientIp = getClientIp(request);
        fingerprints.add("ip:" + clientIp);

        // 排序确保一致性
        Collections.sort(fingerprints);

        // 生成MD5指纹
        String fingerprintStr = String.join("|", fingerprints);
        String fingerprint = MD5.create().digestHex(fingerprintStr);

        return fingerprint;
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，取第一个
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

}
