package com.sidifensen.aspect;

import com.sidifensen.domain.enums.OperationTypeEnum;
import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 功能模块
     */
    String module() default "";

    /**
     * 操作类型（推荐使用枚举 type）
     */
    String operation() default "";

    /**
     * 操作类型（枚举）
     */
    OperationTypeEnum type() default OperationTypeEnum.OTHER;

    /**
     * 操作描述
     */
    String description() default "";
}
