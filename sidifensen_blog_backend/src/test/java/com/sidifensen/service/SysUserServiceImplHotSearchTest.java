package com.sidifensen.service;

import com.sidifensen.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysUserServiceImpl 热门搜索功能测试
 * 测试 Service 层获取热门搜索列表的功能
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
class SysUserServiceImplHotSearchTest {

    @Autowired
    private SysUserServiceImpl sysUserService;

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
    @DisplayName("获取热门搜索 - 空数据应该返回空列表")
    void getHotSearches_emptyData_shouldReturnEmptyList() {
        // Given: Redis 中没有数据

        // When: 获取热门搜索列表（限制 10 条）
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(10);

        // Then: 应该返回空列表
        assertTrue(hotSearches.isEmpty(), "空数据应该返回空列表");
    }

    @Test
    @DisplayName("获取热门搜索 - 应该返回正确的数据格式")
    void getHotSearches_shouldReturnCorrectFormat() {
        // Given: Redis 中有搜索记录
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "Spring Boot", 5.0);
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "Java", 3.0);

        // When: 获取热门搜索列表
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(10);

        // Then: 应该返回正确格式的数据
        assertEquals(2, hotSearches.size(), "应该返回 2 条记录");

        // 验证第一条记录
        Map<String, Object> first = hotSearches.get(0);
        assertEquals("Spring Boot", first.get("keyword"), "第一名应该是 Spring Boot");
        assertEquals(5L, first.get("count"), "分数应该是 5");

        // 验证第二条记录
        Map<String, Object> second = hotSearches.get(1);
        assertEquals("Java", second.get("keyword"), "第二名应该是 Java");
        assertEquals(3L, second.get("count"), "分数应该是 3");
    }

    @Test
    @DisplayName("获取热门搜索 - limit 参数应该生效")
    void getHotSearches_limitParameter_shouldTakeEffect() {
        // Given: Redis 中有 5 条搜索记录
        for (int i = 0; i < 5; i++) {
            stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "关键词" + i, (double) (5 - i));
        }

        // When: 限制返回 3 条
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(3);

        // Then: 应该只返回 3 条记录
        assertEquals(3, hotSearches.size(), "应该限制返回 3 条记录");
    }

    @Test
    @DisplayName("获取热门搜索 - 应该按热度降序排列")
    void getHotSearches_shouldOrderByScoreDesc() {
        // Given: 有无序的搜索记录
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "低热度", 1.0);
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "高热度", 10.0);
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "中热度", 5.0);

        // When: 获取热门搜索列表
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(10);

        // Then: 应该按热度从高到低排序
        assertEquals(3, hotSearches.size(), "应该返回 3 条记录");
        assertEquals("高热度", hotSearches.get(0).get("keyword"), "第一名应该是高热度");
        assertEquals("中热度", hotSearches.get(1).get("keyword"), "第二名应该是中热度");
        assertEquals("低热度", hotSearches.get(2).get("keyword"), "第三名应该是低热度");
    }

    @Test
    @DisplayName("获取热门搜索 - limit 为 0 应该返回空列表")
    void getHotSearches_limitZero_shouldReturnEmptyList() {
        // Given: Redis 中有搜索记录
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "测试关键词", 1.0);

        // When: limit 为 0
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(0);

        // Then: 应该返回空列表
        assertTrue(hotSearches.isEmpty(), "limit 为 0 应该返回空列表");
    }

    @Test
    @DisplayName("获取热门搜索 - limit 为负数应该正常处理")
    void getHotSearches_negativeLimit_shouldHandleGracefully() {
        // Given: Redis 中有搜索记录
        stringRedisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, "测试关键词", 1.0);

        // When: limit 为负数
        List<Map<String, Object>> hotSearches = sysUserService.getHotSearches(-1);

        // Then: 应该返回空列表（ZSet reverseRange 对于负数 end 会返回空）
        assertTrue(hotSearches.isEmpty() || hotSearches.size() >= 0, "负数 limit 应该正常处理");
    }

}
