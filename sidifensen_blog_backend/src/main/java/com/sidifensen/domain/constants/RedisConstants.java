package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-06-30
 */
public class RedisConstants {

    public static final String RedisKeyPrefix = "sidifensen_blog:";

    public static final String CheckCode = RedisKeyPrefix + "CheckCode:";

    public static final String EmailCheckCode = RedisKeyPrefix + "EmailCheckCode:";

    /**
     * 登录验证码过期时间（5分钟，单位：秒）
     */
    public static final long CHECK_CODE_EXPIRE_TIME = 5 * 60;

    /**
     * 邮箱验证码过期时间（5分钟，单位：秒）
     */
    public static final long EMAIL_CHECK_CODE_EXPIRE_TIME = 5 * 60;

    /**
     * 浏览历史缓存键
     * 格式：sidifensen_blog:History:文章ID
     * 存储结构：Set，存储已浏览过该文章的用户ID或IP地址
     */
    public static final String History = RedisKeyPrefix + "History:";

    /**
     * 浏览记录Redis缓存过期时间（24小时，单位：秒）
     */
    public static final long HISTORY_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 限流缓存键前缀
     * 格式：sidifensen_blog:RateLimit:方法名:用户标识
     */
    public static final String RateLimit = RedisKeyPrefix + "RateLimit:";

    /**
     * 黑名单缓存键前缀
     * 格式：sidifensen_blog:Blacklist:用户标识
     */
    public static final String Blacklist = RedisKeyPrefix + "Blacklist:";

    /**
     * 黑名单日志限流缓存键前缀
     * 格式：sidifensen_blog:BlacklistLog:用户标识
     * 用于防止黑名单用户频繁访问导致日志刷屏，5分钟内同一用户只打印一次警告日志
     */
    public static final String BlacklistLog = RedisKeyPrefix + "BlacklistLog:";

    /**
     * 访客记录缓存键前缀
     * 格式：sidifensen_blog:Visitor:Set:日期
     * 存储结构：Set，存储当天的访客唯一标识（userId:ip:device 或 ip:device）
     * 示例：sidifensen_blog:Visitor:Set:2025-10-07
     */
    public static final String VisitorSet = RedisKeyPrefix + "Visitor:Set:";

    /**
     * 热门文章缓存键（近7天访问量排行）
     * 格式：sidifensen_blog:HotArticles:7Days
     * 存储结构：ZSet，score为访问量，member为文章ID
     * 用于快速查询热门文章列表
     */
    public static final String HotArticles7Days = RedisKeyPrefix + "HotArticles:7Days";

    /**
     * 热门文章缓存过期时间（7天，单位：秒）
     */
    public static final long HOT_ARTICLES_EXPIRE_TIME = 7 * 24 * 60 * 60;

    /**
     * AI使用次数缓存键前缀
     * 格式：sidifensen_blog:AiUsage:用户ID
     * 存储结构：计数器，记录用户当天的AI调用次数
     * 过期时间：到第二天凌晨自动过期
     */
    public static final String AiUsage = RedisKeyPrefix + "AiUsage:";

    /**
     * AI内容哈希缓存键前缀（防重复提交）
     * 格式：sidifensen_blog:AiContentHash:用户ID:内容Hash
     * 存储结构：标记值，防止短时间内重复提交相同内容
     * 过期时间：5分钟
     */
    public static final String AiContentHash = RedisKeyPrefix + "AiContentHash:";

    /**
     * AI内容哈希缓存过期时间（5分钟，单位：秒）
     */
    public static final long AI_CONTENT_HASH_EXPIRE_TIME = 5 * 60;

}
