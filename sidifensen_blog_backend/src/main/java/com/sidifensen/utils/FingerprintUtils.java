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

        // 1. User-Agent（虽然相似，但仍是重要特征）
        String userAgent = request.getHeader("User-Agent");
        fingerprints.add("ua:" + StrUtil.nullToEmpty(userAgent));

        // 2. Accept语言
        String acceptLanguage = request.getHeader("Accept-Language");
        fingerprints.add("lang:" + StrUtil.nullToEmpty(acceptLanguage));

        // 3. Accept编码
        String acceptEncoding = request.getHeader("Accept-Encoding");
        fingerprints.add("enc:" + StrUtil.nullToEmpty(acceptEncoding));

        // 4. Accept类型
        String accept = request.getHeader("Accept");
        fingerprints.add("accept:" + StrUtil.nullToEmpty(accept));

        // 5. 连接类型
        String connection = request.getHeader("Connection");
        fingerprints.add("conn:" + StrUtil.nullToEmpty(connection));

        // 6. DNT (Do Not Track)
        String dnt = request.getHeader("DNT");
        fingerprints.add("dnt:" + StrUtil.nullToEmpty(dnt));

        // 7. Upgrade-Insecure-Requests
        String upgradeInsecure = request.getHeader("Upgrade-Insecure-Requests");
        fingerprints.add("upgrade:" + StrUtil.nullToEmpty(upgradeInsecure));

        // 8. Sec-Fetch-* 系列头部（现代浏览器特有）
        String secFetchDest = request.getHeader("Sec-Fetch-Dest");
        String secFetchMode = request.getHeader("Sec-Fetch-Mode");
        String secFetchSite = request.getHeader("Sec-Fetch-Site");
        String secFetchUser = request.getHeader("Sec-Fetch-User");
        fingerprints.add("sec:" + StrUtil.nullToEmpty(secFetchDest) + "|" +
                StrUtil.nullToEmpty(secFetchMode) + "|" +
                StrUtil.nullToEmpty(secFetchSite) + "|" +
                StrUtil.nullToEmpty(secFetchUser));

        // 9. Cache-Control
        String cacheControl = request.getHeader("Cache-Control");
        fingerprints.add("cache:" + StrUtil.nullToEmpty(cacheControl));

        // 10. IP地址（作为辅助特征）
        String clientIp = getClientIp(request);
        fingerprints.add("ip:" + clientIp);

        // 11. 时区偏移（如果前端能提供）
        String timezone = request.getHeader("X-Timezone-Offset");
        fingerprints.add("tz:" + StrUtil.nullToEmpty(timezone));

        // 12. 屏幕分辨率（如果前端能提供）
        String screenResolution = request.getHeader("X-Screen-Resolution");
        fingerprints.add("screen:" + StrUtil.nullToEmpty(screenResolution));

        // 13. 客户端提示（Client Hints）
        String chUa = request.getHeader("Sec-CH-UA");
        String chUaPlatform = request.getHeader("Sec-CH-UA-Platform");
        String chUaMobile = request.getHeader("Sec-CH-UA-Mobile");
        fingerprints.add("ch:" + StrUtil.nullToEmpty(chUa) + "|" +
                StrUtil.nullToEmpty(chUaPlatform) + "|" +
                StrUtil.nullToEmpty(chUaMobile));

        // 排序确保一致性
        Collections.sort(fingerprints);

        // 生成MD5指纹
        String fingerprintStr = String.join("|", fingerprints);
        String fingerprint = MD5.create().digestHex(fingerprintStr);

        log.debug("生成浏览器指纹: {} 个特征 -> {}", fingerprints.size(), fingerprint);
        return fingerprint;
    }

    /**
     * 生成简化指纹（当完整指纹生成失败时的备选方案）
     * 
     * @param request HTTP请求对象
     * @return 简化指纹字符串
     */
    public String generateSimpleFingerprint(HttpServletRequest request) {
        try {
            String userAgent = StrUtil.nullToEmpty(request.getHeader("User-Agent"));
            String acceptLanguage = StrUtil.nullToEmpty(request.getHeader("Accept-Language"));
            String ip = getClientIp(request);

            String simpleFingerprint = userAgent + "|" + acceptLanguage + "|" + ip;
            return MD5.create().digestHex(simpleFingerprint);
        } catch (Exception e) {
            log.warn("生成简化指纹失败，使用IP作为最后备选: {}", e.getMessage());
            return "ip_" + getClientIp(request);
        }
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

    /**
     * 验证指纹有效性
     * 
     * @param fingerprint 指纹字符串
     * @return 是否有效
     */
    public boolean isValidFingerprint(String fingerprint) {
        return StrUtil.isNotBlank(fingerprint) && fingerprint.length() == 32;
    }
}
