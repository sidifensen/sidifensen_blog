package com.sidifensen.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * WebSocket 线程池配置
 * 用于异步处理 WebSocket 消息发送，避免阻塞主线程
 */
@Configuration
@EnableAsync
public class WebSocketThreadPoolConfig {

    /**
     * WebSocket 消息发送专用线程池
     * 
     * 配置说明：
     * - 核心线程数：根据 CPU 核心数动态设置
     * - 最大线程数：核心线程数的 2 倍
     * - 队列容量：使用有界队列，避免内存溢出
     * - 拒绝策略：使用 CallerRunsPolicy，当队列满时由调用线程执行
     */
    @Bean("webSocketExecutor")
    public Executor webSocketExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize * 2;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(1000); // 有界队列，避免内存溢出
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("websocket-msg-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        return executor;
    }
}
