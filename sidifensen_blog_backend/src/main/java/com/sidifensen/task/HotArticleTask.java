package com.sidifensen.task;

import com.sidifensen.mapper.HistoryMapper;
import com.sidifensen.redis.RedisComponent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 热门文章统计定时任务
 * 定期从数据库同步近7天的文章访问量数据到Redis
 *
 * @author sidifensen
 * @since 2025-10-07
 */
@Component
@Slf4j
public class HotArticleTask {

    @Resource
    private HistoryMapper historyMapper;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 项目启动时初始化热门文章数据
     * 确保项目启动后立即有热门文章数据可用
     * 
     * 注意：使用延迟初始化策略，避免在Docker环境中数据库未完全就绪的问题
     * 如果初始化失败，不影响应用启动，等待定时任务重新同步
     */
    @PostConstruct
    public void initHotArticles() {
        log.info("项目启动，准备初始化热门文章数据...");
        
        // 异步延迟执行，避免阻塞应用启动
        // 等待3秒，给数据库更多的初始化时间
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                log.info("开始初始化热门文章数据...");
                syncHotArticles();
            } catch (InterruptedException e) {
                log.warn("热门文章初始化线程被中断: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.warn("初始化热门文章数据失败（将在定时任务中重试）: {}", e.getMessage());
            }
        }, "HotArticleInit").start();
    }

    /**
     * 同步近7天的文章访问量到Redis
     * 每2小时执行一次（降低数据库压力）
     * cron表达式示例：0 0 0,2,4,6,8,10,12,14,16,18,20,22 * * ? 表示每2小时执行
     * <p>
     * 优化策略：
     * 1. 降低执行频率为每2小时，减少数据库压力
     * 2. 配合实时热度更新，数据依然保持较好的实时性
     * 3. 使用临时key避免并发读取问题
     */
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void syncHotArticles() {
        long startTime = System.currentTimeMillis();

        try {
            log.info("开始同步热门文章数据...");

            // 从数据库查询近7天的文章访问量统计
            List<Map<String, Object>> articleStats = historyMapper.getArticleViewCountLast7Days();

            if (articleStats == null || articleStats.isEmpty()) {
                log.info("近7天暂无文章访问数据");
                return;
            }

            // 转换为 Map<文章ID, 访问量>
            Map<Integer, Double> articleScores = new HashMap<>();
            for (Map<String, Object> stat : articleStats) {
                Integer articleId = (Integer) stat.get("articleId");
                Long viewCount = (Long) stat.get("viewCount");
                articleScores.put(articleId, viewCount.doubleValue());
            }

            // 批量更新到Redis（使用原子操作，避免并发读取问题）
            redisComponent.batchSetArticleHotScore(articleScores);

            long endTime = System.currentTimeMillis();
            log.info("热门文章数据同步完成，共同步 {} 篇文章，耗时 {}ms",
                    articleScores.size(), (endTime - startTime));

        } catch (Exception e) {
            log.error("同步热门文章数据失败: {}", e.getMessage(), e);
        }
    }

}

