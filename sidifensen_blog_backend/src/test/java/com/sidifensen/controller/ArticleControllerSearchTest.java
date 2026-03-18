package com.sidifensen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidifensen.redis.RedisComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ArticleController 搜索接口测试
 * 测试搜索接口是否正确记录关键词到 Redis
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@SpringBootTest(properties = {
    "spring.rabbitmq.enabled=false",
    "spring.amqp.enabled=false",
    "spring.mail.enabled=false",
    "minio.enabled=false",
    "aliyun.imageaudit.enabled=false",
    "spring.websocket.enabled=false",
    "alipay.enabled=false"
})
@AutoConfigureMockMvc
class ArticleControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("按标题搜索 - 应该记录搜索关键词")
    void searchArticleByTitle_shouldRecordKeyword() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行搜索请求
        mockMvc.perform(get("/article/search")
                .param("title", "Spring Boot 教程")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // Then: Redis 中应该记录了搜索关键词
        Thread.sleep(100); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "Spring Boot 教程");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(1.0, score, "分数应该是 1");
    }

    @Test
    @DisplayName("按标题搜索 - 相同关键词多次搜索应该累加分数")
    void searchArticleByTitle_sameKeyword_shouldIncrementScore() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行 3 次相同的搜索
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/article/search")
                    .param("title", "Java 教程")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }

        // Then: 分数应该累加到 3
        Thread.sleep(200); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "Java 教程");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(3.0, score, "分数应该是 3");
    }

    @Test
    @DisplayName("按标签搜索 - 应该记录搜索关键词")
    void searchArticleByTag_shouldRecordKeyword() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行标签搜索
        mockMvc.perform(get("/article/search/tag")
                .param("tag", "后端开发")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // Then: Redis 中应该记录了搜索关键词
        Thread.sleep(100); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "后端开发");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(1.0, score, "分数应该是 1");
    }

    @Test
    @DisplayName("按标签搜索 - 相同标签多次搜索应该累加分数")
    void searchArticleByTag_sameKeyword_shouldIncrementScore() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行 2 次相同的标签搜索
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(get("/article/search/tag")
                    .param("tag", "前端开发")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }

        // Then: 分数应该累加到 2
        Thread.sleep(200); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "前端开发");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(2.0, score, "分数应该是 2");
    }

    @Test
    @DisplayName("按作者搜索 - 应该记录搜索关键词")
    void searchArticleByAuthor_shouldRecordKeyword() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行作者搜索
        mockMvc.perform(get("/article/search/author")
                .param("author", "sidifensen")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // Then: Redis 中应该记录了搜索关键词
        Thread.sleep(100); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "sidifensen");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(1.0, score, "分数应该是 1");
    }

    @Test
    @DisplayName("按作者搜索 - 相同作者多次搜索应该累加分数")
    void searchArticleByAuthor_sameKeyword_shouldIncrementScore() throws Exception {
        // Given: Redis 中没有数据

        // When: 执行 5 次相同的作者搜索
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/article/search/author")
                    .param("author", "张三")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }

        // Then: 分数应该累加到 5
        Thread.sleep(300); // 等待异步线程执行完成
        Double score = stringRedisTemplate.opsForZSet().score(HOT_SEARCH_KEY, "张三");
        assertNotNull(score, "搜索关键词应该被记录");
        assertEquals(5.0, score, "分数应该是 5");
    }

    @Test
    @DisplayName("三种搜索方式 - 不同关键词应该独立记录")
    void threeSearchTypes_differentKeywords_shouldRecordIndependently() throws Exception {
        // Given: Redis 中没有数据

        // When: 分别使用三种方式搜索不同的关键词
        mockMvc.perform(get("/article/search")
                .param("title", "标题关键词")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/article/search/tag")
                .param("tag", "标签关键词")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/article/search/author")
                .param("author", "作者关键词")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk());

        // Then: Redis 中应该有 3 条独立的记录
        Thread.sleep(200); // 等待异步线程执行完成
        Long size = stringRedisTemplate.opsForZSet().size(HOT_SEARCH_KEY);
        assertEquals(3, size, "应该有 3 条独立的记录");
    }

    @Test
    @DisplayName("搜索接口 - 空关键词应该返回错误")
    void searchArticle_emptyKeyword_shouldReturnError() throws Exception {
        // When: 搜索关键词为空
        mockMvc.perform(get("/article/search")
                .param("title", "")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                // Then: 应该返回参数校验错误
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("搜索接口 - 页码不能为空")
    void searchArticle_emptyPageNum_shouldReturnError() throws Exception {
        // When: 页码为空
        mockMvc.perform(get("/article/search")
                .param("title", "测试关键词"))
                // Then: 应该返回参数校验错误
                .andExpect(status().isBadRequest());
    }

}
