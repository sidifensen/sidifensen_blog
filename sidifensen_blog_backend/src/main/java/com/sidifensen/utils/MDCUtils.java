package com.sidifensen.utils;

import com.sidifensen.domain.constants.MDCConstants;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.Map;

/**
 * MDC 工具类
 * 用于管理日志上下文信息
 * @author sidifensen
 */
public class MDCUtils {
    
    /**
     * 生成追踪ID
     * @return 追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 设置追踪ID
     * @param traceId 追踪ID，如果为空则自动生成
     */
    public static void setTraceId(String traceId) {
        if (!StringUtils.hasText(traceId)) {
            traceId = generateTraceId();
        }
        MDC.put(MDCConstants.TRACE_ID, traceId);
    }
    
    /**
     * 获取追踪ID
     * @return 追踪ID
     */
    public static String getTraceId() {
        return MDC.get(MDCConstants.TRACE_ID);
    }
    
    /**
     * 设置用户信息
     * @param userId 用户ID
     * @param username 用户名
     */
    public static void setUserInfo(Integer userId, String username) {
        if (userId != null) {
            MDC.put(MDCConstants.USER_ID, userId.toString());
        }
        if (StringUtils.hasText(username)) {
            MDC.put(MDCConstants.USERNAME, username);
        }
    }
    
    /**
     * 设置请求信息
     * @param requestUri 请求URI
     * @param requestMethod 请求方法
     * @param clientIp 客户端IP
     */
    public static void setRequestInfo(String requestUri, String requestMethod, String clientIp) {
        if (StringUtils.hasText(requestUri)) {
            MDC.put(MDCConstants.REQUEST_URI, requestUri);
        }
        if (StringUtils.hasText(requestMethod)) {
            MDC.put(MDCConstants.REQUEST_METHOD, requestMethod);
        }
        if (StringUtils.hasText(clientIp)) {
            MDC.put(MDCConstants.CLIENT_IP, clientIp);
        }
    }
    
    /**
     * 设置业务信息
     * @param moduleName 模块名称
     * @param operationType 操作类型
     * @param businessId 业务ID
     */
    public static void setBusinessInfo(String moduleName, String operationType, String businessId) {
        if (StringUtils.hasText(moduleName)) {
            MDC.put(MDCConstants.MODULE_NAME, moduleName);
        }
        if (StringUtils.hasText(operationType)) {
            MDC.put(MDCConstants.OPERATION_TYPE, operationType);
        }
        if (StringUtils.hasText(businessId)) {
            MDC.put(MDCConstants.BUSINESS_ID, businessId);
        }
    }
    
    /**
     * 设置开始时间
     */
    public static void setStartTime() {
        MDC.put(MDCConstants.START_TIME, String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * 获取开始时间
     * @return 开始时间（毫秒）
     */
    public static Long getStartTime() {
        String startTime = MDC.get(MDCConstants.START_TIME);
        return StringUtils.hasText(startTime) ? Long.valueOf(startTime) : null;
    }
    
    /**
     * 计算执行时间
     * @return 执行时间（毫秒）
     */
    public static Long getExecutionTime() {
        Long startTime = getStartTime();
        return startTime != null ? System.currentTimeMillis() - startTime : null;
    }
    
    /**
     * 获取所有MDC信息
     * @return MDC映射
     */
    public static Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }
    
    /**
     * 设置MDC上下文
     * @param contextMap MDC映射
     */
    public static void setContextMap(Map<String, String> contextMap) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }
    
    /**
     * 清除所有MDC信息
     */
    public static void clear() {
        MDC.clear();
    }
    
    /**
     * 清除指定键的MDC信息
     * @param key 键名
     */
    public static void remove(String key) {
        if (StringUtils.hasText(key)) {
            MDC.remove(key);
        }
    }
}
