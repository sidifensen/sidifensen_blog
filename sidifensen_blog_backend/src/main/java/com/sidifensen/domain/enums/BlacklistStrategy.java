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

    /**
     * 生成包含接口和访问次数的详细违规原因
     *
     * @param apiPath     接口路径（格式：ClassName:methodName）
     * @param accessCount 访问次数
     * @return 详细的违规原因
     */
    public String getDetailedReason(String apiPath, int accessCount) {
        return String.format("访问接口: %s, 访问次数: %d次, %s", apiPath, accessCount, this.description);
    }

    /**
     * 比较策略级别，判断当前策略是否比另一个策略级别更高
     *
     * @param other 另一个策略
     * @return 如果当前策略级别更高返回true
     */
    public boolean isHigherThan(BlacklistStrategy other) {
        if (other == null) {
            return true;
        }
        return this.accessCount > other.accessCount;
    }

    /**
     * 从详细原因字符串中提取策略
     * 格式：访问接口: xxx, 访问次数: xxx次, 轻度/中度/重度违规,封禁xxx
     *
     * @param detailedReason 详细原因
     * @return 对应的策略，如果无法解析则返回null
     */
    public static BlacklistStrategy fromDetailedReason(String detailedReason) {
        if (detailedReason == null || detailedReason.isEmpty()) {
            return null;
        }

        // 根据描述关键字判断策略
        if (detailedReason.contains(HIGH.description)) {
            return HIGH;
        } else if (detailedReason.contains(MEDIUM.description)) {
            return MEDIUM;
        } else if (detailedReason.contains(LOW.description)) {
            return LOW;
        }
        return null;
    }
}
