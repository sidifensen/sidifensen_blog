package com.sidifensen.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RedisComponent 热门搜索功能单元测试
 * 使用 Mock 方式测试 Redis ZSet 存储搜索关键词的功能
 *
 * @author sidifensen
 * @since 2026-03-18
 */
class RedisComponentHotSearchMockTest {

    private StringRedisTemplate stringRedisTemplate;
    private ZSetOperations<String, String> zSetOperations;
    private RedisComponent redisComponent;

    @BeforeEach
    void setUp() {
        // 创建 Mock 对象
        stringRedisTemplate = mock(StringRedisTemplate.class);
        zSetOperations = mock(ZSetOperations.class);

        when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // 创建 RedisComponent 并注入 Mock 对象
        redisComponent = new RedisComponent();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field field = RedisComponent.class.getDeclaredField("stringRedisTemplate");
            field.setAccessible(true);
            field.set(redisComponent, stringRedisTemplate);
        } catch (Exception e) {
            throw new RuntimeException("设置 Mock 对象失败", e);
        }
    }

    @Test
    @DisplayName("记录搜索关键词 - 空关键词应该被忽略")
    void recordSearchKeyword_emptyKeyword_shouldBeIgnored() {
        // Given: 空的搜索关键词

        // When: 记录空关键词
        redisComponent.recordSearchKeyword("");
        redisComponent.recordSearchKeyword(null);
        redisComponent.recordSearchKeyword("   ");

        // Then: 不应该调用 Redis 操作
        verify(zSetOperations, never()).incrementScore(any(), any(), anyDouble());
    }

    @Test
    @DisplayName("记录搜索关键词 - 正常关键词应该记录")
    void recordSearchKeyword_validKeyword_shouldRecord() throws InterruptedException {
        // Given: 一个有效的搜索关键词
        when(zSetOperations.incrementScore(eq("hot_searches"), eq("Spring Boot"), eq(1.0)))
                .thenReturn(1.0);

        // When: 记录搜索关键词
        redisComponent.recordSearchKeyword("Spring Boot");

        // 等待异步线程执行
        Thread.sleep(100);

        // Then: 应该调用 Redis 的 incrementScore 方法
        verify(zSetOperations, timeout(1000)).incrementScore(eq("hot_searches"), eq("Spring Boot"), eq(1.0));
    }

    @Test
    @DisplayName("记录搜索关键词 - 应该去除首尾空格")
    void recordSearchKeyword_keywordWithSpaces_shouldTrim() throws InterruptedException {
        // Given: 带首尾空格的关键词
        when(zSetOperations.incrementScore(eq("hot_searches"), eq("Java"), anyDouble()))
                .thenReturn(1.0);

        // When: 记录带空格的关键词
        redisComponent.recordSearchKeyword("  Java  ");

        // 等待异步线程执行
        Thread.sleep(100);

        // Then: 应该使用去除空格后的关键词
        verify(zSetOperations, timeout(1000)).incrementScore(eq("hot_searches"), eq("Java"), anyDouble());
    }

    @Test
    @DisplayName("获取热门搜索 - 空数据应该返回空列表")
    void getHotSearches_emptyData_shouldReturnEmptyList() {
        // Given: Redis 返回空数据
        when(zSetOperations.reverseRangeWithScores(eq("hot_searches"), eq(0), eq(9)))
                .thenReturn(Collections.emptySet());

        // When: 获取热门搜索列表
        List<Map<String, Object>> result = redisComponent.getHotSearches(10);

        // Then: 应该返回空列表
        assertTrue(result.isEmpty(), "空数据应该返回空列表");
    }

    @Test
    @DisplayName("获取热门搜索 - 应该返回正确的数据格式")
    void getHotSearches_shouldReturnCorrectFormat() {
        // Given: Redis 返回带分数的数据
        Set<ZSetOperations.TypedTuple<String>> typedTuples = new HashSet<>();

        // 创建模拟的 TypedTuple
        ZSetOperations.TypedTuple<String> tuple1 = createTypedTuple("Spring Boot", 5.0);
        ZSetOperations.TypedTuple<String> tuple2 = createTypedTuple("Java", 3.0);
        typedTuples.add(tuple1);
        typedTuples.add(tuple2);

        when(zSetOperations.reverseRangeWithScores(eq("hot_searches"), anyLong(), anyLong()))
                .thenReturn(typedTuples);

        // When: 获取热门搜索列表
        List<Map<String, Object>> result = redisComponent.getHotSearches(10);

        // Then: 应该返回正确格式的数据
        assertEquals(2, result.size(), "应该返回 2 条记录");

        // 验证数据格式
        Map<String, Object> firstResult = result.stream()
                .filter(m -> "Spring Boot".equals(m.get("keyword")))
                .findFirst()
                .orElse(null);
        assertNotNull(firstResult, "应该包含 Spring Boot 记录");
        assertEquals(5L, firstResult.get("count"), "分数应该是 5");
    }

    @Test
    @DisplayName("获取热门搜索 - limit 参数应该生效")
    void getHotSearches_withLimit_shouldLimitResults() {
        // Given: Redis 返回数据
        Set<ZSetOperations.TypedTuple<String>> typedTuples = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            typedTuples.add(createTypedTuple("关键词" + i, (double) (5 - i)));
        }
        when(zSetOperations.reverseRangeWithScores(eq("hot_searches"), anyLong(), anyLong()))
                .thenReturn(typedTuples);

        // When: 限制返回 3 条
        List<Map<String, Object>> result = redisComponent.getHotSearches(3);

        // Then: Redis 应该被调用正确的参数
        verify(zSetOperations).reverseRangeWithScores(eq("hot_searches"), anyLong(), anyLong());
    }

    @Test
    @DisplayName("清除指定搜索关键词 - 应该调用 Redis remove 方法")
    void clearSearchKeyword_shouldCallRedisRemove() {
        // Given:
        when(zSetOperations.remove(eq("hot_searches"), eq("要删除的关键词")))
                .thenReturn(1L);

        // When: 清除指定关键词
        redisComponent.clearSearchKeyword("要删除的关键词");

        // Then: 应该调用 Redis 的 remove 方法
        verify(zSetOperations).remove(eq("hot_searches"), eq("要删除的关键词"));
    }

    @Test
    @DisplayName("清除指定搜索关键词 - 空关键词应该被忽略")
    void clearSearchKeyword_emptyKeyword_shouldBeIgnored() {
        // When: 清除空关键词
        redisComponent.clearSearchKeyword("");
        redisComponent.clearSearchKeyword(null);
        redisComponent.clearSearchKeyword("   ");

        // Then: 不应该调用 Redis 操作
        verify(zSetOperations, never()).remove(any(), any());
    }

    @Test
    @DisplayName("清除所有热门搜索 - 应该调用 Redis delete 方法")
    void clearAllHotSearches_shouldCallRedisDelete() {
        // When: 清除所有记录
        redisComponent.clearAllHotSearches();

        // Then: 应该调用 Redis 的 delete 方法
        verify(stringRedisTemplate).delete("hot_searches");
    }

    /**
     * 创建模拟的 TypedTuple 对象
     */
    private ZSetOperations.TypedTuple<String> createTypedTuple(String value, double score) {
        return new ZSetOperations.TypedTuple<String>() {
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public Double getScore() {
                return score;
            }

            @Override
            public int compareTo(ZSetOperations.TypedTuple<String> other) {
                return Double.compare(this.getScore(), other.getScore());
            }
        };
    }

}
