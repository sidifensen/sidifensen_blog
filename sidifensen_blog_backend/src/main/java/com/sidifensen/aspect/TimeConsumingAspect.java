package com.sidifensen.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 耗时统计切面类
 */
@Slf4j
@Aspect
@Component
public class TimeConsumingAspect {

    /**
     * 定义切点，匹配所有使用@TimeConsuming注解的方法
     */
    @Pointcut("@annotation(com.sidifensen.aspect.TimeConsuming)")
    public void timeConsumingPointcut() {
    }

    /**
     * 环绕通知，统计方法执行耗时
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("timeConsumingPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        // 获取注解信息
        TimeConsuming timeConsuming = method.getAnnotation(TimeConsuming.class);
        String description = timeConsuming.value();
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录结束时间
            long endTime = System.currentTimeMillis();
            long consumeTime = endTime - startTime;
            
            // 异步输出耗时信息
            CompletableFuture.runAsync(() -> {
                if (description.isEmpty()) {
                    log.info("方法 {}.{} 执行耗时: {} ms", className, methodName, consumeTime);
                } else {
                    log.info("方法 {}.{}(\"{}\") 执行耗时: {} ms", className, methodName, description, consumeTime);
                }
            });
            
            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long consumeTime = endTime - startTime;
            
            // 异步输出异常信息和耗时
            CompletableFuture.runAsync(() -> {
                if (description.isEmpty()) {
                    log.error("方法 {}.{} 执行异常，耗时: {} ms", className, methodName, consumeTime, throwable);
                } else {
                    log.error("方法 {}.{}(\"{}\") 执行异常，耗时: {} ms", className, methodName, description, consumeTime, throwable);
                }
            });
            
            throw throwable;
        }
    }
}