package com.sidifensen.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于统计方法执行耗时
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeConsuming {
    
    /**
     * 方法描述信息
     * @return 描述信息
     */
    String value() default "";
}