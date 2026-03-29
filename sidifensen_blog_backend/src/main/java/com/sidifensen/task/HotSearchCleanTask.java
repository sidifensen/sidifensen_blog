package com.sidifensen.task;

import com.sidifensen.redis.RedisComponent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 热门搜索清理定时任务
 * 每天凌晨 2 点执行，清理热度较低的搜索记录，保留前 100 条
 *
 * @author sidifensen
 * @since 2025-10-07
 */
@Component
@Slf4j
public class HotSearchCleanTask {

    @Resource
    private RedisComponent redisComponent;

    /**
     * 每天凌晨 2 点执行
     * 保留热度最高的 100 条搜索记录，删除其余记录
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredHotSearches() {
        long startTime = System.currentTimeMillis();
        try {
            Set<String> keywordsToDelete = redisComponent.getStringRedisTemplate()
                    .opsForZSet().reverseRange("hot_searches", 100, -1);

            if (keywordsToDelete != null && !keywordsToDelete.isEmpty()) {
                redisComponent.getStringRedisTemplate()
                        .opsForZSet().remove("hot_searches", keywordsToDelete.toArray());
                
            } else {
                long currentSize = redisComponent.getStringRedisTemplate()
                        .opsForZSet().size("hot_searches");
                
            }
        } catch (Exception e) {
            log.error("清理热门搜索失败，耗时={}ms", System.currentTimeMillis() - startTime, e);
        }
    }

}
