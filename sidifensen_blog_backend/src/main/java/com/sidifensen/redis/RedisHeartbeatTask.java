package com.sidifensen.redis;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Redis心跳任务类
 * 定期向Redis服务器发送心跳，保持连接活跃
 */
@Component
public class RedisHeartbeatTask {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisHeartbeatTask.class);
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 每60秒执行一次心跳检测
     * 通过向Redis写入一个临时key来保持连接活跃
     */
    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    public void sendHeartbeat() {
        try {
            String heartbeatKey = "heartbeat:" + UUID.randomUUID();
            redisTemplate.opsForValue().set(heartbeatKey, System.currentTimeMillis(), 120);
            logger.info("Redis心跳包发送成功: {}", heartbeatKey);
            
            // 可选：删除临时key
            redisTemplate.delete(heartbeatKey);
        } catch (Exception e) {
            logger.error("Redis心跳包发送失败", e);
        }
    }
}