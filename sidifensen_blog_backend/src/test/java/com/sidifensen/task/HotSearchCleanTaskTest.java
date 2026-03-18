package com.sidifensen.task;

import com.sidifensen.redis.RedisComponent;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HotSearchCleanTask 定时任务测试
 * 测试热门搜索清理任务的功能
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@SpringBootTest(properties = {
    "spring.main.web-application-type=none",
    "spring.rabbitmq.enabled=false",
    "spring.amqp.enabled=false",
    "spring.mail.enabled=false",
    "minio.enabled=false",
    "aliyun.imageaudit.enabled=false",
    "spring.websocket.enabled=false",
    "alipay.enabled=false"
})
class HotSearchCleanTaskTest {

    @Autowired
    private HotSearchCleanTask hotSearchCleanTask;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String HOT_SEARCH_KEY = "hot_searches";

    @BeforeEach
    void setUp() {
        // 清空测试数据
        stringRedisTemplate.delete(HOT_SEARCH_KEY);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        stringRedisTemplate.delete(HOT_SEARCH_KEY);
    }

    @Test
    @DisplayName("清理任务 - 少于 100 条记录时不应该清理")
    void cleanExpiredHotSearches_lessThan100_shouldNotClean() {
        // Given: Redis 中有 50 条搜索记录
        for (int i = 0; i < 50; i++) {
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, 1.0);
        }

        Long beforeSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);

        // When: 执行清理任务
        hotSearchCleanTask.cleanExpiredHotSearches();

        // Then: 记录数量不应该变化
        Long afterSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(beforeSize, afterSize, "少于 100 条记录时不应该清理");
        assertEquals(50, afterSize, "应该保留所有 50 条记录");
    }

    @Test
    @DisplayName("清理任务 - 超过 100 条记录时应该只保留前 100 条")
    void cleanExpiredHotSearches_moreThan100_shouldKeepTop100() {
        // Given: Redis 中有 150 条搜索记录
        for (int i = 0; i < 150; i++) {
            // 设置不同的分数，便于验证保留的是热度最高的
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, (double) (150 - i));
        }

        Long beforeSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(150, beforeSize, "应该创建 150 条记录");

        // When: 执行清理任务
        hotSearchCleanTask.cleanExpiredHotSearches();

        // Then: 应该只保留 100 条记录
        Long afterSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(100, afterSize, "应该只保留 100 条记录");
    }

    @Test
    @DisplayName("清理任务 - 应该保留热度最高的记录")
    void cleanExpiredHotSearches_shouldKeepHighestScore() {
        // Given: Redis 中有 110 条搜索记录，热度各不相同
        // 创建 110 条记录，分数从 1 到 110
        for (int i = 1; i <= 110; i++) {
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, (double) i);
        }

        // When: 执行清理任务
        hotSearchCleanTask.cleanExpiredHotSearches();

        // Then: 应该保留分数最高的 100 条（关键词 11 到关键词 110）
        Set<String> remaining = stringRedisTemplate.opsForZSet().reverseRange(HOT_SEARCH_KEY, 0, -1);
        assertNotNull(remaining, "剩余记录不应该为空");
        assertEquals(100, remaining.size(), "应该保留 100 条记录");

        // 验证最低分数的记录（关键词 11，分数 11）
        Double minScore = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "关键词 11");
        assertNotNull(minScore, "最低分数的记录应该存在");
        assertEquals(11.0, minScore, "最低分数应该是 11");

        // 验证最低分数的记录已经被删除（关键词 10，分数 10）
        Double deletedScore = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "关键词 10");
        assertNull(deletedScore, "分数最低的记录应该被删除");
    }

    @Test
    @DisplayName("清理任务 - 空数据时执行不应该报错")
    void cleanExpiredHotSearches_emptyData_shouldNotError() {
        // Given: Redis 中没有数据

        // When: 执行清理任务
        assertDoesNotThrow(() -> {
            hotSearchCleanTask.cleanExpiredHotSearches();
        }, "空数据时执行清理任务不应该报错");
    }

    @Test
    @DisplayName("清理任务 - 恰好 100 条记录时不应该清理")
    void cleanExpiredHotSearches_exactly100_shouldNotClean() {
        // Given: Redis 中恰好有 100 条搜索记录
        for (int i = 0; i < 100; i++) {
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, 1.0);
        }

        Long beforeSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(100, beforeSize, "应该创建 100 条记录");

        // When: 执行清理任务
        hotSearchCleanTask.cleanExpiredHotSearches();

        // Then: 记录数量不应该变化
        Long afterSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(100, afterSize, "恰好 100 条记录时不应该清理");
    }

    @Test
    @DisplayName("清理任务 - 超过 100 条但分数相同时应该正确清理")
    void cleanExpiredHotSearches_sameScore_shouldCleanCorrectly() {
        // Given: Redis 中有 120 条搜索记录，分数都相同
        for (int i = 0; i < 120; i++) {
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, 1.0);
        }

        Long beforeSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(120, beforeSize, "应该创建 120 条记录");

        // When: 执行清理任务
        hotSearchCleanTask.cleanExpiredHotSearches();

        // Then: 应该保留 100 条记录
        Long afterSize = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(100, afterSize, "应该保留 100 条记录");
    }

}
