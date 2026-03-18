package com.sidifensen.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisComponent 热门搜索功能测试
 * 测试 Redis ZSet 存储搜索关键词的功能
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
class RedisComponentHotSearchTest {

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
    @DisplayName("记录搜索关键词 - 空关键词应该被忽略")
    void recordSearchKeyword_emptyKeyword_shouldBeIgnored() {
        // Given: 空的搜索关键词

        // When: 记录空关键词
        redisComponent.recordSearchKeyword("");
        redisComponent.recordSearchKeyword(null);
        redisComponent.recordSearchKeyword("   ");

        // Then: Redis 中不应该有任何记录
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(0, size, "空关键词不应该被记录");
    }

    @Test
    @DisplayName("记录搜索关键词 - 正常记录应该成功")
    void recordSearchKeyword_validKeyword_shouldRecordSuccessfully() {
        // Given: 一个有效的搜索关键词

        // When: 记录搜索关键词
        redisComponent.recordSearchKeyword("Spring Boot");

        // Then: Redis 中应该有 1 条记录
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(1, size, "应该记录 1 条搜索关键词");
    }

    @Test
    @DisplayName("记录搜索关键词 - 相同关键词应该累加分数")
    void recordSearchKeyword_sameKeyword_shouldIncrementScore() {
        // Given: 多次搜索同一个关键词

        // When: 记录 3 次相同的关键词
        redisComponent.recordSearchKeyword("Java");
        redisComponent.recordSearchKeyword("Java");
        redisComponent.recordSearchKeyword("Java");

        // Then: 该关键词的分数应该是 3
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "Java");
        assertNotNull(score, "分数不应该为空");
        assertEquals(3.0, score, "相同关键词应该累加分数");
    }

    @Test
    @DisplayName("记录搜索关键词 - 不同关键词应该独立记录")
    void recordSearchKeyword_differentKeywords_shouldRecordIndependently() {
        // Given: 多个不同的搜索关键词

        // When: 记录 3 个不同的关键词
        redisComponent.recordSearchKeyword("Spring Boot");
        redisComponent.recordSearchKeyword("Vue.js");
        redisComponent.recordSearchKeyword("Redis");

        // Then: Redis 中应该有 3 条独立的记录
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(3, size, "应该记录 3 条不同的关键词");
    }

    @Test
    @DisplayName("记录搜索关键词 - 关键词应该去除首尾空格")
    void recordSearchKeyword_keywordWithSpaces_shouldTrim() {
        // Given: 带首尾空格的关键词

        // When: 记录带空格的关键词
        redisComponent.recordSearchKeyword("  Java  ");
        redisComponent.recordSearchKeyword("Java");

        // Then: 应该视为同一个关键词，分数为 2
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "Java");
        assertNotNull(score, "分数不应该为空");
        assertEquals(2.0, score, "应该去除首尾空格后累加");
    }

    @Test
    @DisplayName("获取热门搜索列表 - 空数据应该返回空列表")
    void getHotSearches_emptyData_shouldReturnEmptyList() {
        // Given: Redis 中没有数据

        // When: 获取热门搜索列表
        List<Map<String, Object>> hotSearches = redisComponent.getHotSearches(10);

        // Then: 应该返回空列表
        assertTrue(hotSearches.isEmpty(), "空数据应该返回空列表");
    }

    @Test
    @DisplayName("获取热门搜索列表 - 应该按分数降序返回")
    void getHotSearches_shouldReturnOrderByScoreDesc() {
        // Given: 有不同分数的搜索关键词
        redisComponent.recordSearchKeyword("低热度");
        redisComponent.recordSearchKeyword("高热度");
        redisComponent.recordSearchKeyword("高热度");
        redisComponent.recordSearchKeyword("高热度");
        redisComponent.recordSearchKeyword("中热度");
        redisComponent.recordSearchKeyword("中热度");

        // When: 获取热门搜索列表
        List<Map<String, Object>> hotSearches = redisComponent.getHotSearches(10);

        // Then: 应该按分数从高到低排序
        assertEquals(3, hotSearches.size(), "应该返回 3 条记录");
        assertEquals("高热度", hotSearches.get(0).get("keyword"), "第一名应该是高热度");
        assertEquals(3L, hotSearches.get(0).get("count"), "高热度的分数应该是 3");
        assertEquals("中热度", hotSearches.get(1).get("keyword"), "第二名应该是中热度");
        assertEquals(2L, hotSearches.get(1).get("count"), "中热度的分数应该是 2");
        assertEquals("低热度", hotSearches.get(2).get("keyword"), "第三名应该是低热度");
        assertEquals(1L, hotSearches.get(2).get("count"), "低热度的分数应该是 1");
    }

    @Test
    @DisplayName("获取热门搜索列表 - 应该限制返回数量")
    void getHotSearches_withLimit_shouldLimitResults() {
        // Given: 有 5 条搜索记录
        for (int i = 0; i < 5; i++) {
            redisComponent.recordSearchKeyword("关键词" + i);
        }

        // When: 限制返回 3 条
        List<Map<String, Object>> hotSearches = redisComponent.getHotSearches(3);

        // Then: 应该只返回 3 条记录
        assertEquals(3, hotSearches.size(), "应该限制返回 3 条记录");
    }

    @Test
    @DisplayName("获取热门搜索列表 - 返回结果应该包含 keyword 和 count 字段")
    void getHotSearches_resultShouldContainKeywordAndCount() {
        // Given: 有一条搜索记录
        redisComponent.recordSearchKeyword("测试关键词");
        redisComponent.recordSearchKeyword("测试关键词");

        // When: 获取热门搜索列表
        List<Map<String, Object>> hotSearches = redisComponent.getHotSearches(10);

        // Then: 结果应该包含 keyword 和 count 字段
        assertEquals(1, hotSearches.size(), "应该有 1 条记录");
        Map<String, Object> firstResult = hotSearches.get(0);
        assertTrue(firstResult.containsKey("keyword"), "结果应该包含 keyword 字段");
        assertTrue(firstResult.containsKey("count"), "结果应该包含 count 字段");
        assertEquals("测试关键词", firstResult.get("keyword"), "keyword 值应该正确");
        assertEquals(2L, firstResult.get("count"), "count 值应该正确");
    }

    @Test
    @DisplayName("清除指定搜索关键词 - 应该删除指定关键词")
    void clearSearchKeyword_shouldDeleteSpecifiedKeyword() {
        // Given: 有多条搜索记录
        redisComponent.recordSearchKeyword("要删除的关键词");
        redisComponent.recordSearchKeyword("要删除的关键词");
        redisComponent.recordSearchKeyword("保留的关键词");

        // When: 清除指定关键词
        redisComponent.clearSearchKeyword("要删除的关键词");

        // Then: 只删除指定的关键词
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(1, size, "应该只剩 1 条记录");
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "保留的关键词");
        assertNotNull(score, "保留的关键词应该存在");
    }

    @Test
    @DisplayName("清除指定搜索关键词 - 清除不存在的关键词不应该报错")
    void clearSearchKeyword_nonExistentKeyword_shouldNotError() {
        // Given: Redis 中没有数据

        // When: 清除不存在的关键词
        assertDoesNotThrow(() -> {
            redisComponent.clearSearchKeyword("不存在的关键词");
        }, "清除不存在的关键词不应该报错");
    }

    @Test
    @DisplayName("清除指定搜索关键词 - 空关键词应该被忽略")
    void clearSearchKeyword_emptyKeyword_shouldBeIgnored() {
        // Given: 有一条搜索记录
        redisComponent.recordSearchKeyword("有效关键词");

        // When: 清除空关键词
        redisComponent.clearSearchKeyword("");
        redisComponent.clearSearchKeyword(null);
        redisComponent.clearSearchKeyword("   ");

        // Then: 有效关键词应该仍然存在
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(1, size, "有效关键词应该仍然存在");
    }

    @Test
    @DisplayName("清除所有热门搜索 - 应该删除所有记录")
    void clearAllHotSearches_shouldDeleteAllRecords() {
        // Given: 有多条搜索记录
        redisComponent.recordSearchKeyword("关键词 1");
        redisComponent.recordSearchKeyword("关键词 2");
        redisComponent.recordSearchKeyword("关键词 3");

        // When: 清除所有记录
        redisComponent.clearAllHotSearches();

        // Then: 所有记录都应该被删除
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(0, size, "所有记录都应该被删除");
    }

    @Test
    @DisplayName("清除所有热门搜索 - 空数据时调用不应该报错")
    void clearAllHotSearches_emptyData_shouldNotError() {
        // Given: Redis 中没有数据

        // When: 清除所有记录
        assertDoesNotThrow(() -> {
            redisComponent.clearAllHotSearches();
        }, "空数据时清除不应该报错");
    }

}
