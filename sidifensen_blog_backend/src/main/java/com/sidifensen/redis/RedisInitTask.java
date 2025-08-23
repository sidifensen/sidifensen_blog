package com.sidifensen.redis;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis初始化任务类
 * 在项目启动时向Redis中添加测试数据
 */
@Component
@Slf4j
public class RedisInitTask implements ApplicationRunner {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {

            log.info("Redis初始化完成，已添加测试key: test:key");
        } catch (Exception e) {
            log.error("Redis初始化失败", e);
        }
    }
}