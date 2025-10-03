package com.sidifensen.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 限流异常类
 * 当用户触发限流时抛出此异常
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RateLimitException extends RuntimeException {

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public RateLimitException(String message) {
        super(message);
    }
}

