package com.sidifensen.utils;

import com.sidifensen.exception.ThreadPoolException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 * 用于创建具有统一命名规则和异常处理机制的线程
 */
@Slf4j
public class MyThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * 构造函数
     *
     * @param namePrefix 线程名称前缀
     */
    public MyThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setUncaughtExceptionHandler((t, e) -> {
            log.error("线程池中的线程发生未捕获异常: threadName={}, exception={} , cause={}", t.getName(), e.getMessage(), e);
            throw new ThreadPoolException(e.getMessage(), e);
        });
        return thread;
    }
}