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

            // 格式化封禁时间显示
            String timeMessage = formatBanTime(ttl);
            throw new RateLimitException(BlogConstants.BlacklistedUser + "，" + timeMessage);
        }

        return true;
    }

    /**
     * 格式化封禁时间显示
     * @param minutes 剩余封禁分钟数
     * @return 格式化后的时间字符串
     */
    private String formatBanTime(long minutes) {
        if (minutes < 0) {
            return "封禁即将解除";
        } else if (minutes == 0) {
            return "封禁即将解除";
        } else if (minutes < 60) {
            return "剩余封禁时间：" + minutes + "分钟";
        } else if (minutes < 1440) {
            // 小于24小时，显示小时和分钟
            long hours = minutes / 60;
            long remainMinutes = minutes % 60;
            if (remainMinutes == 0) {
                return "剩余封禁时间：" + hours + "小时";
            } else {
                return "剩余封禁时间：" + hours + "小时" + remainMinutes + "分钟";
            }
        } else {
            // 大于等于24小时，显示天数和小时
            long days = minutes / 1440;
            long remainHours = (minutes % 1440) / 60;
            if (remainHours == 0) {
                return "剩余封禁时间：" + days + "天";
            } else {
                return "剩余封禁时间：" + days + "天" + remainHours + "小时";
            }
        }
    }
}

