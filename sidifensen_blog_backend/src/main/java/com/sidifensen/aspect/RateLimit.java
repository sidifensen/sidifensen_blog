package com.sidifensen.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 支持类级别和方法级别的访问频率限制
 * 方法级别的注解优先级高于类级别
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流次数
     * 支持简写：@RateLimit(10) 等同于 @RateLimit(value = 10)
     *
     * @return 在指定时间周期内允许的最大访问次数
     */
    int value() default 60;

    /**
     * 限流周期（秒）
     *
     * @return 时间窗口长度，单位：秒
     */
    int period() default 60;

    /**
     * 触发限流时的提示信息
     *
     * @return 错误提示消息
     */
    String message() default "操作繁忙，请稍候再试";
}

