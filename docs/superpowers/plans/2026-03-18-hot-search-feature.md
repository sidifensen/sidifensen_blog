# 热门搜索功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现完整的热门搜索功能，包括后端 Redis 存储搜索记录和前端展示热门搜索列表

**Architecture:**
- 后端使用 Redis ZSet 存储搜索关键词及其热度（搜索次数）
- 每次用户执行搜索时，异步更新 Redis 中的搜索记录
- 前端在搜索页面加载时调用后端接口获取热门搜索列表并展示

**Tech Stack:** Spring Boot 3.4.0, Redis 7, Vue 3.5.13, Element Plus 2.10

---

### Task 1: 后端 - 在 RedisComponent 中添加热门搜索相关方法

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisComponent.java`
- Test: 无单独测试文件，通过 Service 层单元测试验证

- [ ] **Step 1: 读取 RedisComponent 文件了解现有结构**

读取 `sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisComponent.java` 文件

- [ ] **Step 2: 添加记录搜索历史的方法**

在 RedisComponent 中添加以下方法：

```java
/**
 * 记录搜索关键词到 Redis ZSet
 * 格式：key = "hot_searches", member = 关键词，score = 搜索次数
 * @param keyword 搜索关键词
 */
public void recordSearchKeyword(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return;
    }
    String redisKey = "hot_searches";
    // 使用 ZSet 的 incrementScore 方法，如果 member 不存在会自动创建并设置 score 为 1
    stringRedisTemplate.opsForZSet().incrementScore(redisKey, keyword.trim(), 1.0);
    log.info("记录搜索关键词：{}", keyword);
}

/**
 * 从 Redis ZSet 获取热门搜索列表（按搜索次数降序）
 * @param limit 返回数量限制
 * @return 热门搜索列表，每个元素包含 keyword 和 count
 */
public List<Map<String, Object>> getHotSearches(int limit) {
    String redisKey = "hot_searches";
    Set<ZSetOperations.TypedTuple<String>> typedTuples =
        stringRedisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, limit - 1);

    List<Map<String, Object>> result = new ArrayList<>();
    if (typedTuples != null) {
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            Map<String, Object> item = new HashMap<>();
            item.put("keyword", tuple.getValue());
            item.put("count", tuple.getScore().longValue());
            result.add(item);
        }
    }
    return result;
}

/**
 * 清除指定搜索关键词的热度记录
 * @param keyword 搜索关键词
 */
public void clearSearchKeyword(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return;
    }
    String redisKey = "hot_searches";
    stringRedisTemplate.opsForZSet().remove(redisKey, keyword.trim());
}

/**
 * 清除所有热门搜索记录
 */
public void clearAllHotSearches() {
    String redisKey = "hot_searches";
    stringRedisTemplate.delete(redisKey);
}
```

- [ ] **Step 3: 添加必要的 import**

确保文件顶部包含以下 import：

```java
import org.springframework.data.redis.core.ZSetOperations;
import java.util.*;
```

- [ ] **Step 4: 编译验证**

```bash
cd sidifensen_blog_backend
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisComponent.java
git commit -m "feat: 添加热门搜索 Redis 操作方法"
```

---

### Task 2: 后端 - 实现 SysUserServiceImpl.getHotSearches 方法

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/SysUserServiceImpl.java`

- [ ] **Step 1: 读取现有实现**

读取 `SysUserServiceImpl.java` 文件中 `getHotSearches` 方法的当前实现（第 715-729 行）

- [ ] **Step 2: 替换 getHotSearches 方法实现**

将现有的 TODO 实现替换为：

```java
@Override
public List<Map<String, Object>> getHotSearches(Integer limit) {
    // 从 Redis 中获取热门搜索记录
    // Redis ZSet 格式：key = "hot_searches", member = 关键词，score = 搜索次数
    return redisComponent.getHotSearches(limit);
}
```

- [ ] **Step 3: 编译验证**

```bash
cd sidifensen_blog_backend
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/SysUserServiceImpl.java
git commit -m "feat: 实现获取热门搜索列表方法"
```

---

### Task 3: 后端 - 在搜索文章时记录搜索关键词

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/controller/ArticleController.java`
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/ArticleService.java`
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/ArticleServiceImpl.java`

- [ ] **Step 1: 读取 ArticleController 的搜索接口**

读取 `ArticleController.java` 中搜索文章的相关接口（第 166-190 行）

- [ ] **Step 2: 修改 ArticleController 的搜索接口，添加搜索记录**

修改以下三个接口，在每个接口中调用 `redisComponent.recordSearchKeyword`：

```java
// 1. searchArticleByTitle (第 166-173 行)
@RateLimit(20)
@GetMapping("/search")
public Result searchArticleByTitle(...) {
    PageVo<List<ArticleVo>> articleVoList = articleService.searchArticleByTitle(title, pageNum, pageSize);
    // 记录搜索关键词
    redisComponent.recordSearchKeyword(title);
    return Result.success(articleVoList);
}

// 2. searchArticleByTag (第 183-190 行)
@RateLimit(20)
@GetMapping("/search/tag")
public Result searchArticleByTag(...) {
    PageVo<List<ArticleVo>> articleVoList = articleService.searchArticleByTag(tag, pageNum, pageSize);
    // 记录搜索关键词
    redisComponent.recordSearchKeyword(tag);
    return Result.success(articleVoList);
}

// 3. searchArticleByAuthor (第 191 行附近)
@RateLimit(20)
@GetMapping("/search/author")
public Result searchArticleByAuthor(...) {
    PageVo<List<ArticleVo>> articleVoList = articleService.searchArticleByAuthor(author, pageNum, pageSize);
    // 记录搜索关键词
    redisComponent.recordSearchKeyword(author);
    return Result.success(articleVoList);
}
```

- [ ] **Step 3: 在 ArticleController 中注入 RedisComponent**

如果尚未注入，添加：

```java
@Resource
private RedisComponent redisComponent;
```

- [ ] **Step 4: 编译验证**

```bash
cd sidifensen_blog_backend
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 5: 提交**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/controller/ArticleController.java
git commit -m "feat: 搜索文章时记录搜索关键词到 Redis"
```

---

### Task 4: 前端 - 检查并完善 getHotSearches API

**Files:**
- Check: `sidifensen_blog_frontend/sidifensen_user/src/api/user.js`

- [ ] **Step 1: 读取 user.js 中的 getHotSearches API**

读取 `sidifensen_blog_frontend/sidifensen_user/src/api/user.js` 中 `getHotSearches` 方法的定义（第 123-129 行）

- [ ] **Step 2: 确认 API 已正确定义**

确认存在以下代码：

```javascript
// 获取热门搜索列表
export function getHotSearches(limit) {
  return request({
    url: `/user/search/hot?limit=${limit || 10}`,
    method: "get",
  });
}
```

- [ ] **Step 3: 如无必要不修改**

如果 API 已正确定义，此任务无需修改，直接进入下一个任务

- [ ] **Step 4: 如需修改则提交**

```bash
git add sidifensen_blog_frontend/sidifensen_user/src/api/user.js
git commit -m "fix: 完善热门搜索 API 定义"
```

---

### Task 5: 前端 - 完善 Search 页面的 loadHotSearches 方法

**Files:**
- Modify: `sidifensen_blog_frontend/sidifensen_user/src/views/Search/index.vue`

- [ ] **Step 1: 读取现有 loadHotSearches 方法**

读取 `Search/index.vue` 中 `loadHotSearches` 方法的当前实现（约第 607-615 行）

- [ ] **Step 2: 完善 loadHotSearches 方法**

将现有实现修改为：

```javascript
// 加载热门搜索
const loadHotSearches = async () => {
  try {
    const res = await getHotSearches(10);
    // 后端返回格式：[{keyword: "xxx", count: 123}, ...]
    hotSearches.value = res.data?.data || [];
  } catch (error) {
    console.error("加载热门搜索失败:", error);
    // 失败时使用默认空数组，不影响页面其他功能
    hotSearches.value = [];
  }
};
```

- [ ] **Step 3: 确认 onMounted 中已调用 loadHotSearches**

确认 `onMounted` 异步方法中包含 `loadHotSearches()` 调用：

```javascript
onMounted(async () => {
  // 加载搜索历史
  const savedHistory = localStorage.getItem("searchHistory");
  if (savedHistory) {
    searchHistory.value = JSON.parse(savedHistory);
  }

  // 并行加载所有数据
  await Promise.all([
    loadHotTags(),
    loadRecommendedAuthors(),
    loadHotSearches()
  ]);

  // 检查 URL 参数
  const keyword = route.query.keyword;
  const type = route.query.type || "all";

  if (keyword) {
    searchKeyword.value = keyword;
    searchType.value = type;
    await performSearch(true);
  }

  await nextTick();
  window.addEventListener("scroll", handleScroll, { passive: true });
});
```

- [ ] **Step 4: 编译验证**

```bash
cd sidifensen_blog_frontend/sidifensen_user
npm run build
```

Expected: 构建成功，无 TypeScript 错误

- [ ] **Step 5: 提交**

```bash
git add sidifensen_blog_frontend/sidifensen_user/src/views/Search/index.vue
git commit -m "feat: 完善搜索页面热门搜索数据加载"
```

---

### Task 6: 后端 - 添加定时清理过期搜索记录功能

**Files:**
- Create: `sidifensen_blog_backend/src/main/java/com/sidifensen/task/HotSearchCleanTask.java`

- [ ] **Step 1: 创建定时任务类**

创建新文件 `src/main/java/com/sidifensen/task/HotSearchCleanTask.java`：

```java
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

        // 获取所有热门搜索记录
        Set<String> allKeywords = redisComponent.getStringRedisTemplate()
            .opsForZSet().reverseRange("hot_searches", 100, -1);

        if (allKeywords != null && !allKeywords.isEmpty()) {
            // 删除排名在 100 之后的记录
            redisComponent.getStringRedisTemplate()
                .opsForZSet().remove("hot_searches", allKeywords.toArray());
            log.info("清理完成，共删除 {} 条过期记录", allKeywords.size());
        } else {
            log.info("无需清理，当前热门记录数：{}",
                redisComponent.getStringRedisTemplate()
                    .opsForZSet().size("hot_searches"));
        }
    }
}
```

- [ ] **Step 2: 确保 Spring Boot 已启用定时任务**

检查主应用类或配置类上是否有 `@EnableScheduling` 注解

如果没有，在应用主类上添加：

```java
@EnableScheduling
@SpringBootApplication
public class SidifensenBlogBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SidifensenBlogBackendApplication.class, args);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
cd sidifensen_blog_backend
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/task/HotSearchCleanTask.java
git add sidifensen_blog_backend/src/main/java/com/sidifensen/SidifensenBlogBackendApplication.java
git commit -m "feat: 添加热门搜索定时清理任务"
```

---

### Task 7: 前端 - 添加黑夜模式样式验证

**Files:**
- Modify: `sidifensen_blog_frontend/sidifensen_user/src/views/Search/index.vue`

- [ ] **Step 1: 读取热门搜索区域的样式**

读取 `Search/index.vue` 中 `.hot-search` 和 `.hot-item` 相关样式

- [ ] **Step 2: 确认样式使用 CSS 变量**

确认样式文件中包含黑夜模式适配：

```scss
// 热门搜索
.hot-search {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border);

  .section-header {
    margin-bottom: 12px;

    .section-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-muted);
    }
  }

  .hot-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;

    .hot-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: background 0.2s;

      &:hover {
        background: var(--bg-page);
      }

      .hot-rank {
        width: 20px;
        height: 20px;
        background: var(--border);
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        color: var(--text-muted);
        flex-shrink: 0;

        &.top-3 {
          background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
          color: white;
        }
      }

      .hot-text {
        flex: 1;
        font-size: 14px;
        color: var(--text-regular);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .hot-count {
        font-size: 12px;
        color: var(--text-muted);
      }
    }
  }
}
```

- [ ] **Step 3: 确认 html.dark 覆盖已存在**

如果样式文件中已有 CSS 变量和 `html.dark` 覆盖，则无需修改

- [ ] **Step 4: 编译验证**

```bash
cd sidifensen_blog_frontend/sidifensen_user
npm run build
```

Expected: 构建成功

- [ ] **Step 5: 提交（如无修改则跳过）**

---

### Task 8: 完整功能测试

**Files:** 无修改

- [ ] **Step 1: 启动后端服务**

```bash
cd sidifensen_blog_backend
java -jar target/sidifensen_blog_backend-1.0-SNAPSHOT.jar
```

或者在 IDE 中直接运行 `Main.java`

- [ ] **Step 2: 启动前端服务**

```bash
cd sidifensen_blog_frontend/sidifensen_user
npm run dev
```

- [ ] **Step 3: 浏览器访问搜索页面**

打开浏览器访问 `http://localhost:5173/search`

- [ ] **Step 4: 测试页面初始加载**

验证：
- [ ] 热门搜索区域正常显示
- [ ] 初始加载时显示从后端获取的热门搜索列表（如果有数据）
- [ ] 黑夜模式切换正常

- [ ] **Step 5: 测试搜索功能**

验证：
- [ ] 输入关键词并执行搜索
- [ ] 搜索后刷新页面，检查 Redis 中是否记录了搜索关键词
- [ ] 多次搜索同一关键词，验证热度计数是否累加

- [ ] **Step 6: 测试 Redis 数据**

```bash
# 连接 Redis CLI
redis-cli

# 查看热门搜索 ZSet
ZREVRANGE hot_searches 0 -1 WITHSCORES

# 应该输出类似：
# 1) "java"
#    2) "5"
# 3) "vue"
#    4) "3"
```

- [ ] **Step 7: 测试前端展示**

验证：
- [ ] 搜索后刷新页面，热门搜索列表应显示刚才搜索的关键词
- [ ] 点击热门搜索词，应能执行搜索并跳转到搜索结果
- [ ] 排名前 3 的热门词应显示不同的背景色（红色渐变）

---

## 完成检查清单

- [ ] RedisComponent 添加了记录搜索关键词和获取热门搜索的方法
- [ ] SysUserServiceImpl.getHotSearches 方法已实现
- [ ] ArticleController 的搜索接口已添加搜索记录调用
- [ ] 前端 Search 页面已完善 loadHotSearches 方法
- [ ] 添加了定时清理过期搜索记录的任务
- [ ] 后端编译通过
- [ ] 前端编译通过
- [ ] 功能测试通过
- [ ] 黑夜模式样式正常
- [ ] 所有代码已提交

---

**注意事项：**
1. 所有后端代码修改后必须执行 `mvn clean compile -DskipTests` 验证编译
2. 所有前端代码修改后必须执行 `npm run build` 验证编译
3. 遵循 TDD 原则：先写测试（如果有单元测试），再写实现
4. 每次修改后及时提交，保持小步快跑
5. 后端代码必须添加必要的中文注释
