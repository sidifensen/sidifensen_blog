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
        log.info("开始清理过期热门搜索记录...");

        try {
            // 获取所有热门搜索记录中排名在 100 之后的关键词
            Set<String> keywordsToDelete = redisComponent.getStringRedisTemplate()
                    .opsForZSet().reverseRange("hot_searches", 100, -1);

            if (keywordsToDelete != null && !keywordsToDelete.isEmpty()) {
                // 删除排名在 100 之后的记录
                redisComponent.getStringRedisTemplate()
                        .opsForZSet().remove("hot_searches", keywordsToDelete.toArray());
                log.info("清理完成，共删除 {} 条过期记录", keywordsToDelete.size());
            } else {
                long currentSize = redisComponent.getStringRedisTemplate()
                        .opsForZSet().size("hot_searches");
                log.info("无需清理，当前热门记录数：{}", currentSize);
            }
        } catch (Exception e) {
            log.error("清理过期热门搜索记录失败：{}", e.getMessage(), e);
        }
    }

}
