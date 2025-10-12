package com.sidifensen.service;

/**
 * AI使用配额管理服务
 * 用于控制用户对AI接口的调用频率和次数，防止滥用
 *
 * @author sidifensen
 * @since 2025-10-12
 */
public interface AiUsageService {

    /**
     * 检查用户今日AI调用次数是否已达上限
     *
     * @param userId 用户ID
     * @return true=可以调用, false=已达上限
     */
    boolean checkDailyLimit(Integer userId);

    /**
     * 记录用户AI调用次数
     * 调用成功后应该调用此方法记录使用次数
     *
     * @param userId 用户ID
     */
    void recordUsage(Integer userId);

    /**
     * 获取用户今日剩余调用次数
     *
     * @param userId 用户ID
     * @return 剩余次数
     */
    int getRemainingQuota(Integer userId);

    /**
     * 检查是否为重复内容（基于内容hash）
     * 防止短时间内重复提交相同内容消耗token
     *
     * @param userId  用户ID
     * @param content 内容
     * @return true=重复内容, false=非重复内容
     */
    boolean isDuplicateContent(Integer userId, String content);

    /**
     * 记录内容hash（防重复）
     *
     * @param userId  用户ID
     * @param content 内容
     */
    void recordContentHash(Integer userId, String content);
}
