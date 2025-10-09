package com.sidifensen.aspect;

import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.exception.RateLimitException;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.RedisUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 系统黑名单拦截器
 * 在请求进入Controller前检查用户是否在黑名单中
 * 比切面更早拦截，提高安全性和性能
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Component
@Slf4j
public class SysBlacklistInterceptor implements HandlerInterceptor {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private IpUtils ipUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取用户标识：优先使用登录用户ID，如果未登录则使用IP地址
        String identifier;
        Integer userId = SecurityUtils.getUserId();
        if (userId != 0) {
            identifier = "user:" + userId;
        } else {
            // 未登录，使用IP地址
            identifier = "ip:" + ipUtils.getIp();
        }

        // 检查是否在黑名单中
        String blacklistKey = RedisConstants.Blacklist + identifier;
        if (redisUtils.hasKey(blacklistKey)) {
            long ttl = redisUtils.getExpire(blacklistKey, TimeUnit.MINUTES);
            String violationType = (String) redisUtils.get(blacklistKey);

            // 日志限流：同一标识5分钟内只打印一次警告日志，避免日志刷屏
            if (redisComponent.shouldLogBlacklist(identifier, 5L, TimeUnit.MINUTES)) {
                log.warn("黑名单用户尝试访问 - 用户标识: {}, 违规类型: {}, 剩余封禁时间: {}分钟",
                        identifier, violationType, ttl);
            }

            throw new RateLimitException(BlogConstants.BlacklistedUser);
        }

        return true;
    }
}

