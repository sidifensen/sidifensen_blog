package com.sidifensen.redis;

import com.sidifensen.utils.MyThreadFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sidifensen
 * @since 2025-08-23
 */
@Service
public class RedisService implements ApplicationRunner {

    @Resource
    private RedisComponent redisComponent;

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("RedisService")
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 相册和图片相关Redis缓存已删除，不再需要初始化
    }

}
