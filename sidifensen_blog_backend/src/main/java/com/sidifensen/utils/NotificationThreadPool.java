package com.sidifensen.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通知线程池管理组件
 * 统一管理项目中所有通知相关的异步线程池
 */
@Component
@Slf4j
public class NotificationThreadPool {

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int QUEUE_SIZE = 500;

    private final ConcurrentHashMap<String, ThreadPoolExecutor> threadPoolMap = new ConcurrentHashMap<>();
    private final AtomicInteger threadCounter = new AtomicInteger(1);

    /**
     * 获取指定名称的通知线程池
     */
    public ThreadPoolExecutor getNotificationPool(String poolName) {
        return threadPoolMap.computeIfAbsent(poolName, name -> {


            ThreadFactory threadFactory = runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName(name + "-demon-thread-" + threadCounter.getAndIncrement());
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    log.error("通知线程池中的线程发生未捕获异常: threadName={}, exception={}", t.getName(), e.getMessage());
                });
                return thread;
            };

            return new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(QUEUE_SIZE),
                    threadFactory,
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        });
    }
}
