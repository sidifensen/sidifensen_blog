package com.sidifensen.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 设备检测工具类
 * 通过 User-Agent 判断设备类型
 *
 * @author sidifensen
 * @since 2025-10-06
 */
public class DeviceUtils {

    /**
     * 移动设备关键字
     */
    private static final String[] MOBILE_KEYWORDS = {
        "mobile", "android", "iphone", "ipod", 
        "blackberry", "windows phone", "webos"
    };

    /**
     * 获取设备类型
     * @param request HTTP请求
     * @return PC 或 Mobile
     */
    public static String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        
        if (userAgent == null) {
            return "Unknown";
        }
        
        userAgent = userAgent.toLowerCase();
        
        // 检测移动设备关键字
        for (String keyword : MOBILE_KEYWORDS) {
            if (userAgent.contains(keyword)) {
                return "Mobile";
            }
        }
        
        // 默认为PC
        return "PC";
    }
}

