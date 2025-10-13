package com.sidifensen.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.service.AiUsageService;
import com.sidifensen.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * AI使用配额管理服务实现类
 *
 * @author sidifensen
 * @since 2025-10-12
 */
@Service
@Slf4j
public class AiUsageServiceImpl implements AiUsageService {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 每个用户每天最多调用AI接口的次数
     */
    private static final int DAILY_LIMIT = 10;

    /**
     * 检查用户今日AI调用次数是否已达上限
     *
     * @param userId 用户ID
     * @return true-未达上限可继续调用，false-已达上限无法调用
     */
    @Override
    public boolean checkDailyLimit(Integer userId) {
        String key = RedisConstants.AiUsage + userId;
        Object countObj = redisUtils.get(key);

        if (countObj == null) {
            return true;
        }

        Long count = Long.valueOf(countObj.toString());
        boolean allowed = count < DAILY_LIMIT;
        if (!allowed) {
            log.warn("用户 [ID: {}] 今日AI调用次数已达上限 ({}/{})", userId, count, DAILY_LIMIT);
        }

        return allowed;
    }

    /**
     * 记录用户AI使用次数
     * 首次调用时会设置过期时间到第二天凌晨，实现每日计数重置
     *
     * @param userId 用户ID
     */
    @Override
    public void recordUsage(Integer userId) {
        String key = RedisConstants.AiUsage + userId;
        Long count = redisUtils.incr(key, 1);

        // 首次调用时设置过期时间为当天剩余时间（到第二天凌晨过期）
        if (count == 1) {
            long secondsUntilMidnight = getSecondsUntilMidnight();
            redisUtils.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取用户今日剩余AI调用配额
     *
     * @param userId 用户ID
     * @return 剩余可调用次数（0表示已用完）
     */
    @Override
    public int getRemainingQuota(Integer userId) {
        String key = RedisConstants.AiUsage + userId;
        Object countObj = redisUtils.get(key);

        if (countObj == null) {
            return DAILY_LIMIT;
        }

        Long count = Long.valueOf(countObj.toString());
        return Math.max(0, DAILY_LIMIT - count.intValue());
    }

    /**
     * 检查用户是否在短时间内重复提交相同内容
     * 通过MD5哈希值判断内容是否相同，防止重复请求
     *
     * @param userId  用户ID
     * @param content 提交的内容
     * @return true-内容重复，false-内容不重复
     */
    @Override
    public boolean isDuplicateContent(Integer userId, String content) {
        // 计算内容的MD5哈希值
        String contentHash = DigestUtil.md5Hex(content);
        String key = RedisConstants.AiContentHash + userId + ":" + contentHash;

        // 检查Redis中是否存在该key
        boolean exists = redisUtils.hasKey(key);

        if (exists) {
            log.warn("用户 [ID: {}] 重复提交相同内容（Hash: {}）", userId, contentHash.substring(0, 8));
        }

        return exists;
    }

    /**
     * 记录用户提交内容的哈希值
     * 用于在一定时间内（5分钟）防止重复提交相同内容
     *
     * @param userId  用户ID
     * @param content 提交的内容
     */
    @Override
    public void recordContentHash(Integer userId, String content) {
        // 计算内容的MD5哈希值
        String contentHash = DigestUtil.md5Hex(content);
        String key = RedisConstants.AiContentHash + userId + ":" + contentHash;

        // 记录hash，5分钟内不允许重复提交相同内容
        redisUtils.set(key, "1", RedisConstants.AI_CONTENT_HASH_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 计算到第二天凌晨0点的秒数
     *
     * @return 秒数
     */
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight).getSeconds();
    }
}
