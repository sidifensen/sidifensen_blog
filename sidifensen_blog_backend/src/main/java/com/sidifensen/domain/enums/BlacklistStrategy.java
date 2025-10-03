package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 黑名单策略枚举
 * 根据不同的访问次数设置不同的封禁时间
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Getter
@AllArgsConstructor
public enum BlacklistStrategy {

    /**
     * 轻度违规：访问超过30次，封禁1小时
     */
    LOW(30, 60 * 60, "轻度违规,封禁1小时"),

    /**
     * 中度违规：访问超过100次，封禁6小时
     */
    MEDIUM(100, 6 * 60 * 60, "中度违规,封禁6小时"),

    /**
     * 重度违规：访问超过200次，封禁7天
     */
    HIGH(200, 7 * 24 * 60 * 60, "重度违规,封禁7天");

    /**
     * 访问次数阈值
     */
    private final Integer accessCount;

    /**
     * 封禁时长（秒）
     */
    private final Integer banDuration;

    /**
     * 违规等级描述
     */
    private final String description;

    /**
     * 根据访问次数获取对应的黑名单策略
     *
     * @param accessCount 访问次数
     * @return 黑名单策略，如果没有匹配则返回null
     */
    public static BlacklistStrategy getStrategyByAccessCount(int accessCount) {
        // 从高到低检查，返回第一个满足条件的策略
        if (accessCount >= HIGH.accessCount) {
            return HIGH;
        } else if (accessCount >= MEDIUM.accessCount) {
            return MEDIUM;
        } else if (accessCount >= LOW.accessCount) {
            return LOW;
        }
        return null;
    }
}

