package com.sidifensen.aspect;

import cn.hutool.core.util.StrUtil;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.enums.BlacklistStrategy;
import com.sidifensen.exception.RateLimitException;
import com.sidifensen.service.SysBlacklistService;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.RedisUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于Redis实现分布式限流，支持黑名单机制
 * 支持类级别和方法级别的限流配置，方法级别优先级更高
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Value("${sidifensen.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private SysBlacklistService sysBlacklistService;

    /**
     * 环绕通知，拦截带有 @RateLimit 注解的类或方法
     * 方法级别注解优先级高于类级别注解
     */
    @Around("@annotation(com.sidifensen.aspect.RateLimit) || @within(com.sidifensen.aspect.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 优先获取方法级别的注解，如果没有则获取类级别的注解
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            rateLimit = method.getDeclaringClass().getAnnotation(RateLimit.class);
        }

        // 如果既没有方法级别也没有类级别的注解，则直接放行
        if (rateLimit == null) {
            return joinPoint.proceed();
        }

        // 限流开关，关闭时直接放行（用于压力测试）
        if (!rateLimitEnabled) {
            return joinPoint.proceed();
        }

        // 获取用户标识（登录用户ID或IP地址）
        String identifier;
        Integer userId = SecurityUtils.getUserId();
        if (!Objects.equals(userId, 0)) {
            identifier = "user:" + userId;
        } else {
            identifier = "ip:" + ipUtils.getIp();
        }

        // 获取 Controller 类名
        String className = signature.getDeclaringType().getSimpleName();

        // 构建限流缓存键：类名:方法名:用户标识
        String rateLimitKey = RedisConstants.RateLimit + className + ":" + method.getName() + ":" + identifier;

        // 执行限流检查并记录访问次数
        boolean allowed;
        Long currentCount;
        long rateLimitExpireSeconds = rateLimit.period(); // 默认使用注解指定的过期时间
        try {
            // 访问次数自增
            currentCount = redisUtils.incr(rateLimitKey, 1);

            // 首次访问时设置过期时间
            if (currentCount == 1) {
                // 优先使用黑名单策略的封禁时长作为过期时间，避免限流key早于黑名单过期
                // 导致计数器重置后用户在黑名单有效期内再次触发限流
                BlacklistStrategy firstStrategy = BlacklistStrategy.getStrategyByAccessCount(rateLimit.value());
                if (firstStrategy != null) {
                    rateLimitExpireSeconds = firstStrategy.getBanDuration();
                }
                redisUtils.expire(rateLimitKey, rateLimitExpireSeconds, TimeUnit.SECONDS);
            } else {
                // 非首次访问，确保过期时间与黑名单保持一致
                // 如果之前设置的过期时间较短，需要延长
                redisUtils.expire(rateLimitKey, rateLimitExpireSeconds, TimeUnit.SECONDS);
            }

            // 检查是否超过限流限制
            allowed = currentCount <= rateLimit.value();

            // 根据访问次数检查是否需要加入黑名单或升级黑名单等级
            BlacklistStrategy newStrategy = BlacklistStrategy.getStrategyByAccessCount(currentCount.intValue());
            if (newStrategy != null) {
                // 构建黑名单key
                String blacklistKey = RedisConstants.Blacklist + identifier;
                // 构建接口路径
                String apiPath = className + ":" + method.getName();
                // 生成详细违规原因
                String detailedReason = newStrategy.getDetailedReason(apiPath, currentCount.intValue());

                // 使用 setIfAbsent 原子操作，防止并发竞态条件导致重复加入黑名单
                // setIfAbsent 返回 true 表示 key 不存在且设置成功（即首次加入黑名单）
                // 返回 false 表示 key 已存在（已在黑名单中）
                boolean isFirstAdd = redisUtils.setIfAbsent(blacklistKey, detailedReason, newStrategy.getBanDuration(), TimeUnit.SECONDS);

                if (isFirstAdd) {
                    // 首次加入黑名单成功，发送通知
                    sysBlacklistService.addToBlacklist(identifier, detailedReason, newStrategy.getBanDuration());
                    log.warn("用户加入黑名单 - 用户标识: {}, 访问接口: {}, 访问次数: {}, 策略: {}, 封禁时长: {}秒",
                            identifier, apiPath, currentCount, newStrategy.getDescription(), newStrategy.getBanDuration());
                } else {
                    // 用户已在黑名单中，检查是否需要升级策略
                    String currentReasonInRedis = (String) redisUtils.get(blacklistKey);
                    BlacklistStrategy currentStrategy = BlacklistStrategy.fromDetailedReason(currentReasonInRedis);

                    // 如果新策略级别更高，则升级封禁
                    if (newStrategy.isHigherThan(currentStrategy)) {
                        // 生成新的详细违规原因
                        String newDetailedReason = newStrategy.getDetailedReason(apiPath, currentCount.intValue());

                        // 更新Redis黑名单
                        redisUtils.set(blacklistKey, newDetailedReason, newStrategy.getBanDuration(), TimeUnit.SECONDS);

                        // 更新数据库记录
                        sysBlacklistService.updateBlacklist(identifier, newDetailedReason, newStrategy.getBanDuration());

                        // 延长计数器过期时间，与新的封禁时长保持一致
                        redisUtils.expire(rateLimitKey, newStrategy.getBanDuration(), TimeUnit.SECONDS);

                        log.warn("黑名单等级升级 - 用户标识: {}, 访问接口: {}, 访问次数: {}, 原策略: {}, 新策略: {}, 新封禁时长: {}秒",
                                identifier, apiPath, currentCount,
                                currentStrategy != null ? currentStrategy.getDescription() : "未知",
                                newStrategy.getDescription(), newStrategy.getBanDuration());
                    }
                }
                // 延长计数器过期时间，与封禁时长保持一致
                redisUtils.expire(rateLimitKey, newStrategy.getBanDuration(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("限流检查异常，允许访问 - key: {}", rateLimitKey, e);
            allowed = true;
        }

        if (!allowed) {
            // 抛出限流异常
            String message = StrUtil.isNotBlank(rateLimit.message()) ? rateLimit.message(): BlogConstants.RateLimitExceeded;
            log.warn("限流触发 - 用户标识: {}, 类: {}, 方法: {}, 限制: {}次/{}秒",
                    identifier, className, method.getName(), rateLimit.value(), rateLimit.period());
            throw new RateLimitException(message);
        }

        // 通过限流检查，执行目标方法
        return joinPoint.proceed();
    }
}
