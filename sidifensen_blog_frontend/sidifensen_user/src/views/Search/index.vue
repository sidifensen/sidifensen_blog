<template>
  <div class="search-container">
    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="container">
        <div class="search-box-wrapper">
          <div class="search-input-group">
            <!-- 大搜索框（带自动补全） -->
            <el-autocomplete
              v-model="searchKeyword"
              :fetch-suggestions="fetchSuggestions"
              placeholder="搜索文章标题或标签..."
              size="large"
              class="search-input"
              popper-class="search-autocomplete-popper"
              clearable
              :trigger-on-focus="false"
              @select="handleSelectSuggestion"
              @keyup.enter="handleEnterSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #default="{ item }">
                <div class="suggestion-item">
                  <el-icon class="suggestion-icon">
                    <Document v-if="searchType === 'title' || lastSearchAction === 'title'" />
                    <PriceTag v-else />
                  </el-icon>
                  <span class="suggestion-text">{{ item.value }}</span>
                </div>
              </template>
            </el-autocomplete>

            <!-- 搜索按钮 -->
            <el-button type="primary" size="large" class="search-btn" @click="handleSearch" :loading="searchLoading">
              <el-icon><Search /></el-icon>
              搜索标题
            </el-button>

            <!-- 搜索标签按钮 -->
            <el-button type="success" size="large" class="search-tag-btn" @click="handleSearchByTag" :loading="searchLoading">
              <el-icon><PriceTag /></el-icon>
              搜索标签
            </el-button>
          </div>

          <!-- 当前搜索信息 -->
          <div v-if="searchKeyword && hasSearched" class="search-info">
            <span class="search-type-badge" :class="{ 'tag-search': searchType === 'tag' }">
              {{ searchType === "title" ? "标题搜索" : "标签搜索" }}
            </span>
            <span class="search-keyword">"{{ searchKeyword }}"</span>
            <span class="search-result-count">找到 {{ total }} 篇文章</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索结果区域 -->
    <section class="search-results-section">
      <div class="container">
        <!-- 初始提示状态 -->
        <div v-if="!hasSearched" class="initial-state">
          <el-icon class="search-icon"><Search /></el-icon>
          <h3>开始搜索</h3>
          <p>在上方输入关键字，点击按钮开始搜索文章</p>
        </div>

        <!-- 加载状态 -->
        <div v-else-if="searchLoading && articles.length === 0" class="loading-container">
          <el-skeleton animated :count="5">
            <template #template>
              <div class="article-skeleton">
                <el-skeleton-item variant="image" style="width: 120px; height: 90px; border-radius: 6px" />
                <div class="skeleton-content">
                  <el-skeleton-item variant="h3" style="width: 70%; height: 20px; margin-bottom: 10px" />
                  <el-skeleton-item variant="text" style="width: 100%; height: 16px; margin-bottom: 8px" />
                  <el-skeleton-item variant="text" style="width: 90%; height: 16px" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>

        <!-- 空状态 -->
        <div v-else-if="articles.length === 0" class="empty-state">
          <el-empty description="未找到相关文章">
            <template #image>
              <el-icon class="empty-icon"><DocumentDelete /></el-icon>
            </template>
            <el-button type="primary" @click="clearSearch">清空搜索</el-button>
          </el-empty>
        </div>

        <!-- 文章列表 -->
        <div v-else class="article-list">
          <div v-for="article in articles" :key="article.id" class="article-item" @click="goToArticle(article)">
            <!-- 文章封面 -->
            <div class="article-cover-wrapper">
              <el-image :src="article.coverUrl || ''" class="article-cover" fit="cover">
                <template #placeholder>
                  <div class="image-placeholder">
                    <el-icon class="is-loading"><Loading /></el-icon>
                  </div>
                </template>
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <!-- 文章信息 -->
            <div class="article-info">
              <!-- 文章标题 -->
              <h3 class="article-title">{{ article.title }}</h3>

              <!-- 文章描述 -->
              <p class="article-description">{{ article.description || "暂无描述" }}</p>

              <!-- 文章元信息 -->
              <div class="article-meta">
                <!-- 作者信息 -->
                <div class="author-info">
                  <el-avatar :size="24" :src="article.avatar" class="author-avatar" />
                  <span class="author-name">{{ article.nickname || "匿名用户" }}</span>
                </div>

                <!-- 统计信息 -->
                <div class="article-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ article.readCount || 0 }}
                  </span>
                  <span class="stat-item">
                    <svg-icon name="like" width="14px" height="14px" margin-right="4px" color="#909399" />
                    {{ article.likeCount || 0 }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ article.commentCount || 0 }}
                  </span>
                </div>

                <!-- 发布时间 -->
                <span class="article-date">{{ formatDate(article.createTime) }}</span>
              </div>

              <!-- 文章标签 -->
              <div v-if="article.tag" class="article-tags">
                <el-tag v-for="(tag, index) in parseTag(article.tag)" :key="index" size="small" class="tag-item">
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 加载更多指示器 -->
          <div class="load-more-indicator">
            <div v-if="loadingMore" class="loading-more">
              <div class="loading-spinner"></div>
              <span>正在加载更多文章...</span>
            </div>
            <div v-else-if="!hasMore && articles.length > 0" class="no-more">
              <el-divider>已加载全部结果</el-divider>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { Search, PriceTag, View, ChatDotRound, Loading, Picture, DocumentDelete, Document } from "@element-plus/icons-vue";
import { searchArticleByTitle, searchArticleByTag, getTitleSuggestions, getTagSuggestions } from "@/api/article";

// 路由
const router = useRouter();
const route = useRoute();

// 响应式数据
const searchKeyword = ref(""); // 搜索关键字
const searchType = ref("title"); // 搜索类型：title 或 tag
const lastSearchAction = ref("title"); // 记录最后一次搜索操作类型（用于回车键）
const articles = ref([]); // 文章列表
const searchLoading = ref(false); // 搜索加载状态
const loadingMore = ref(false); // 加载更多状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页数据量
const total = ref(0); // 总数据量
const hasSearched = ref(false); // 是否已经搜索过

// 计算属性
const hasMore = computed(() => {
  return articles.value.length < total.value;
});

// 获取搜索建议（自动补全）
const fetchSuggestions = async (queryString, callback) => {
  // 如果输入为空，不显示建议
  if (!queryString || queryString.trim().length === 0) {
    callback([]);
    return;
  }

  try {
    let res;
    // 根据当前搜索类型获取不同的建议
    if (lastSearchAction.value === "tag") {
      res = await getTagSuggestions(queryString);
    } else {
      // 默认为标题搜索
      res = await getTitleSuggestions(queryString);
    }

    const suggestions = res.data.data || [];
    // 转换为 Autocomplete 需要的格式（包含 value 字段）
    const formattedSuggestions = suggestions.map((item) => ({
      value: item,
    }));
    callback(formattedSuggestions);
  } catch (error) {
    console.error("获取搜索建议失败:", error);
    callback([]);
  }
};

// 处理选择建议项
const handleSelectSuggestion = (item) => {
  // 选择建议后，自动执行搜索
  if (lastSearchAction.value === "tag") {
    handleSearchByTag();
  } else {
    handleSearch();
  }
};

// 处理回车键搜索 - 根据最后一次搜索操作类型决定搜索方式
const handleEnterSearch = async () => {
  if (lastSearchAction.value === "tag") {
    await handleSearchByTag();
  } else {
    await handleSearch();
  }
};

// 执行搜索（标题搜索）
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning("请输入搜索关键字");
    return;
  }

  lastSearchAction.value = "title"; // 记录搜索操作类型
  searchType.value = "title";

  // 更新 URL 参数（使用 replace 避免产生多个历史记录）
  router.replace({
    path: "/search",
    query: {
      keyword: searchKeyword.value,
      type: "title",
    },
  });

  await performSearch(true);
};

// 执行搜索（标签搜索）
const handleSearchByTag = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning("请输入标签关键字");
    return;
  }

  lastSearchAction.value = "tag"; // 记录搜索操作类型
  searchType.value = "tag";

  // 更新 URL 参数（使用 replace 避免产生多个历史记录）
  router.replace({
    path: "/search",
    query: {
      keyword: searchKeyword.value,
      type: "tag",
    },
  });

  await performSearch(true);
};

// 执行搜索请求
const performSearch = async (reset = false) => {
  try {
    // 设置加载状态
    if (reset) {
      searchLoading.value = true;
      currentPage.value = 1;
      articles.value = [];
    } else {
      loadingMore.value = true;
    }

    hasSearched.value = true;

    // 根据搜索类型调用不同的接口
    let res;
    if (searchType.value === "title") {
      res = await searchArticleByTitle(searchKeyword.value, currentPage.value, pageSize.value);
    } else {
      res = await searchArticleByTag(searchKeyword.value, currentPage.value, pageSize.value);
    }

    const newArticles = res.data.data.data || [];
    total.value = res.data.data.total || 0;

    if (reset) {
      articles.value = newArticles;
    } else {
      articles.value = [...articles.value, ...newArticles];
    }

    // 更新页码
    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++;
    }

    // 如果没有搜索结果，显示提示
    if (total.value === 0) {
      ElMessage.info(`未找到包含"${searchKeyword.value}"的文章`);
    }
  } catch (error) {
    ElMessage.error("搜索失败，请稍后重试");
    console.error("搜索失败:", error);
  } finally {
    searchLoading.value = false;
    loadingMore.value = false;
  }
};

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = "";
  articles.value = [];
  hasSearched.value = false;
  currentPage.value = 1;
  total.value = 0;
  lastSearchAction.value = "title"; // 重置为默认的标题搜索

  // 清空 URL 参数
  router.replace({
    path: "/search",
    query: {},
  });
};

// 节流函数
const throttle = (func, delay) => {
  let timeoutId;
  let lastExecTime = 0;
  return function (...args) {
    const currentTime = Date.now();

    if (currentTime - lastExecTime > delay) {
      func.apply(this, args);
      lastExecTime = currentTime;
    } else {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        func.apply(this, args);
        lastExecTime = Date.now();
      }, delay - (currentTime - lastExecTime));
    }
  };
};

// 处理滚动事件 - 自动加载更多
const handleScroll = throttle(() => {
  // 如果正在加载或没有更多数据或未搜索，则不处理
  if (loadingMore.value || !hasMore.value || searchLoading.value || !hasSearched.value) {
    return;
  }

  // 获取页面滚动信息
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  // 当滚动到距离底部 300px 时开始加载更多
  if (scrollTop + windowHeight >= documentHeight - 300) {
    performSearch(false);
  }
}, 200); // 200ms 节流

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 解析标签（标签可能是逗号分隔的字符串）
const parseTag = (tagString) => {
  if (!tagString) return [];
  return tagString.split(",").filter((tag) => tag.trim());
};

// 跳转到文章详情页
const goToArticle = (article) => {
  router.push(`/user/${article.userId}/article/${article.id}`);
};

// 组件挂载时绑定滚动事件
onMounted(async () => {
  // 检查是否有 URL 参数携带的搜索关键字
  const keyword = route.query.keyword;
  const type = route.query.type || "title";

  if (keyword) {
    searchKeyword.value = keyword;
    searchType.value = type;
    lastSearchAction.value = type; // 同步记录搜索操作类型
    await performSearch(true);
  }

  // 等待DOM更新后绑定滚动事件
  await nextTick();
  window.addEventListener("scroll", handleScroll, { passive: true });
});

// 组件卸载时移除滚动事件监听
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 搜索页面容器
.search-container {
  min-height: 100vh;
  padding-top: 50px;
  background: var(--el-bg-color-page);

  // 搜索区域
  .search-section {
    padding: 40px 0;
    background: transparent;

    // 容器
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;

      // 搜索框包装器
      .search-box-wrapper {
        max-width: 900px;
        margin: 0 auto;

        // 搜索输入组
        .search-input-group {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 20px;

          // 搜索输入框（自动补全）- 椭圆形设计
          // 注意：具体样式在全局样式中定义（不受 scoped 限制）
          .search-input {
            flex: 1;

            :deep(.el-input__wrapper) {
              background: var(--el-bg-color) !important;
              box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
              border: 2px solid transparent;
              transition: all 0.3s ease;
              backdrop-filter: blur(10px);

              &:hover,
              &:focus {
                background: var(--el-bg-color) !important;
                border-color: var(--el-color-primary);
                box-shadow: 0 8px 32px rgba(64, 158, 255, 0.2);
              }
            }

            :deep(.el-input__inner) {
              color: var(--el-text-color-primary);
            }

            // 左侧搜索图标
            :deep(.el-input__prefix) {
              color: #909399;
              margin-left: 6px;
              display: flex;
              align-items: center;
            }

            // 右侧清除图标
            :deep(.el-input__suffix) {
              margin-right: 6px;
              display: flex;
              align-items: center;
            }
          }

          // 自动补全建议项样式
          .suggestion-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 4px 0;

            .suggestion-icon {
              font-size: 16px;
              color: var(--el-color-primary);
            }

            .suggestion-text {
              flex: 1;
              font-size: 14px;
              color: var(--el-text-color-regular);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          // 搜索按钮通用样式
          .search-btn,
          .search-tag-btn {
            border-radius: 16px;
            padding: 16px 40px;
            font-weight: 600;
            font-size: 15px;
            letter-spacing: 0.3px;
            border: 2px solid transparent;
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            white-space: nowrap;
            position: relative;
            overflow: hidden;
            backdrop-filter: blur(10px);

            .el-icon {
              margin-right: 8px;
              font-size: 18px;
              transition: transform 0.3s ease;
            }

            // 悬停时图标动画
            &:hover .el-icon {
              transform: scale(1.1) rotate(5deg);
            }

            // 按钮光泽效果
            &::after {
              content: "";
              position: absolute;
              top: 0;
              left: -100%;
              width: 100%;
              height: 100%;
              background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
              transition: left 0.5s;
            }

            &:hover::after {
              left: 100%;
            }
          }

          // 标题搜索按钮（现代浅蓝紫色）
          .search-btn {
            background: #e8f4ff;
            color: #4a90e2;
            border-color: rgba(74, 144, 226, 0.2);
            box-shadow: 0 4px 16px rgba(74, 144, 226, 0.15), 0 2px 4px rgba(74, 144, 226, 0.1);

            &:hover {
              background: #d5ebff;
              border-color: rgba(74, 144, 226, 0.4);
              transform: translateY(-4px) scale(1.02);
              box-shadow: 0 12px 28px rgba(74, 144, 226, 0.25), 0 4px 8px rgba(74, 144, 226, 0.15);
              color: #3a7bc8;
            }

            &:active {
              transform: translateY(-2px) scale(1);
              box-shadow: 0 6px 16px rgba(74, 144, 226, 0.2), 0 2px 4px rgba(74, 144, 226, 0.1);
            }
          }

          // 标签搜索按钮（现代浅青绿色）
          .search-tag-btn {
            background: #e6f9f5;
            color: #36b89a;
            border-color: rgba(54, 184, 154, 0.2);
            box-shadow: 0 4px 16px rgba(54, 184, 154, 0.15), 0 2px 4px rgba(54, 184, 154, 0.1);

            &:hover {
              background: #d1f4eb;
              border-color: rgba(54, 184, 154, 0.4);
              transform: translateY(-4px) scale(1.02);
              box-shadow: 0 12px 28px rgba(54, 184, 154, 0.25), 0 4px 8px rgba(54, 184, 154, 0.15);
              color: #2a9d7f;
            }

            &:active {
              transform: translateY(-2px) scale(1);
              box-shadow: 0 6px 16px rgba(54, 184, 154, 0.2), 0 2px 4px rgba(54, 184, 154, 0.1);
            }
          }
        }

        // 搜索信息
        .search-info {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 24px;
          background: var(--el-bg-color);
          border-radius: 50px;
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

          // 搜索类型标识
          .search-type-badge {
            padding: 6px 16px;
            background: #e8f4ff;
            color: #4a90e2;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            border: 1.5px solid rgba(74, 144, 226, 0.25);
            box-shadow: 0 2px 8px rgba(74, 144, 226, 0.12);

            &.tag-search {
              background: #e6f9f5;
              color: #36b89a;
              border-color: rgba(54, 184, 154, 0.25);
              box-shadow: 0 2px 8px rgba(54, 184, 154, 0.12);
            }
          }

          // 搜索关键字
          .search-keyword {
            font-size: 16px;
            font-weight: 600;
            color: var(--el-text-color-primary);
          }

          // 搜索结果数量
          .search-result-count {
            margin-left: auto;
            font-size: 14px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }
  }

  // 搜索结果区域
  .search-results-section {
    padding: 20px 0 60px 0;

    // 容器
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;

      // 初始状态
      .initial-state {
        text-align: center;
        padding: 80px 20px;
        background: var(--el-color-primary-light-9);
        border-radius: 16px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

        // 搜索图标
        .search-icon {
          font-size: 80px;
          color: #909399;
          margin-bottom: 20px;
        }

        // 标题
        h3 {
          font-size: 24px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 12px 0;
        }

        // 描述
        p {
          font-size: 16px;
          color: var(--el-text-color-secondary);
          margin: 0;
        }
      }

      // 加载容器
      .loading-container {
        background: var(--el-bg-color);
        border-radius: 16px;
        padding: 30px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

        // 骨架屏
        .article-skeleton {
          display: flex;
          gap: 20px;
          padding: 20px 0;
          border-bottom: 1px solid var(--el-border-color-lighter);

          &:last-child {
            border-bottom: none;
          }

          .skeleton-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;
          }
        }
      }

      // 空状态
      .empty-state {
        background: var(--el-bg-color);
        border-radius: 16px;
        padding: 60px 20px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        text-align: center;

        // 空状态图标
        .empty-icon {
          font-size: 80px;
          color: #909399;
        }

        :deep(.el-empty__description) {
          font-size: 16px;
          margin-top: 16px;
        }
      }

      // 文章列表
      .article-list {
        background: var(--el-bg-color);
        border-radius: 16px;
        padding: 30px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

        // 文章项
        .article-item {
          display: flex;
          gap: 20px;
          padding: 24px 0;
          border-bottom: 1px solid var(--el-border-color-lighter);
          cursor: pointer;
          transition: all 0.3s ease;

          &:last-child {
            border-bottom: none;
            padding-bottom: 0;
          }

          &:hover {
            transform: translateX(8px);
            background: var(--el-fill-color-light);
            padding-left: 16px;
            padding-right: 16px;
            margin-left: -16px;
            margin-right: -16px;
            border-radius: 12px;
          }

          // 文章封面包装器
          .article-cover-wrapper {
            flex-shrink: 0;

            // 文章封面
            .article-cover {
              width: 200px;
              height: 125px;
              border-radius: 12px;
              overflow: hidden;
              box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
              transition: transform 0.3s ease;

              &:hover {
                transform: scale(1.05);
              }

              // 图片加载占位符
              .image-placeholder {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--el-fill-color-light);
                color: var(--el-text-color-placeholder);

                .el-icon {
                  font-size: 32px;
                }
              }

              // 图片加载错误
              .image-error {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--el-fill-color-light);
                color: var(--el-text-color-placeholder);

                .el-icon {
                  font-size: 32px;
                }
              }
            }
          }

          // 文章信息
          .article-info {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-width: 0;

            // 文章标题
            .article-title {
              font-size: 20px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              margin: 0 0 12px 0;
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              transition: color 0.3s ease;

              &:hover {
                color: var(--el-color-primary);
              }
            }

            // 文章描述
            .article-description {
              font-size: 14px;
              color: var(--el-text-color-regular);
              margin: 0 0 12px 0;
              line-height: 1.6;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            // 文章元信息
            .article-meta {
              display: flex;
              align-items: center;
              gap: 16px;
              flex-wrap: wrap;
              font-size: 13px;
              color: var(--el-text-color-secondary);

              // 作者信息
              .author-info {
                display: flex;
                align-items: center;
                gap: 8px;

                // 作者头像
                .author-avatar {
                  border: 2px solid var(--el-border-color-lighter);
                }

                // 作者名称
                .author-name {
                  font-weight: 500;
                  color: var(--el-text-color-regular);
                }
              }

              // 统计信息
              .article-stats {
                display: flex;
                align-items: center;
                gap: 12px;

                // 统计项
                .stat-item {
                  display: flex;
                  align-items: center;
                  gap: 4px;

                  .el-icon {
                    font-size: 14px;
                  }
                }
              }

              // 发布时间
              .article-date {
                margin-left: auto;
              }
            }

            // 文章标签
            .article-tags {
              display: flex;
              gap: 8px;
              flex-wrap: wrap;
              margin-top: 8px;

              // 标签项
              .tag-item {
                border-radius: 4px;
                font-size: 12px;
              }
            }
          }
        }

        // 加载更多指示器
        .load-more-indicator {
          margin-top: 30px;
          text-align: center;

          // 加载更多中
          .loading-more {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            padding: 20px;
            color: var(--el-text-color-regular);

            // 加载动画
            .loading-spinner {
              width: 24px;
              height: 24px;
              border: 3px solid var(--el-border-color);
              border-top-color: var(--el-color-primary);
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }
          }

          // 没有更多
          .no-more {
            padding: 20px 0;

            :deep(.el-divider__text) {
              color: var(--el-text-color-secondary);
              font-size: 14px;
            }
          }
        }
      }
    }
  }
}

// 加载动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .search-container {
    // 搜索区域
    .search-section {
      padding: 20px 0;

      .container {
        // 搜索框包装器
        .search-box-wrapper {
          // 搜索输入组
          .search-input-group {
            flex-direction: column;

            // 搜索按钮
            .search-btn,
            .search-tag-btn {
              width: 100%;
              margin-left: 0 !important; // 移除 Element Plus 默认的 margin-left
            }
          }

          // 搜索信息
          .search-info {
            flex-direction: column;
            align-items: flex-start;
            padding: 16px;
            border-radius: 10px; // 手机端减小搜索信息区域的圆角

            .search-result-count {
              margin-left: 0;
            }
          }
        }
      }
    }

    // 搜索结果区域
    .search-results-section {
      .container {
        // 文章列表
        .article-list {
          padding: 20px;

          // 文章项
          .article-item {
            flex-direction: column;
            gap: 16px;

            &:hover {
              transform: none;
            }

            // 文章封面包装器
            .article-cover-wrapper {
              .article-cover {
                width: 100%;
                height: 200px;
              }
            }

            // 文章信息
            .article-info {
              // 文章元信息
              .article-meta {
                flex-direction: column;
                align-items: flex-start;
                gap: 8px;

                .article-date {
                  margin-left: 0;
                }
              }
            }
          }
        }
      }
    }
  }
}

// 注意：自动补全下拉框样式在文件末尾的全局样式块中定义
</style>

<style lang="scss">
// 全局样式（不带 scoped），强制覆盖 Element Plus 默认样式

// 强制搜索输入框椭圆形样式（全局样式，优先级最高）
.search-section {
  .search-input {
    .el-input__wrapper {
      border-radius: 40px !important;
      padding: 18px 28px !important;
      height: 56px !important;
      min-height: 56px !important;
      background: var(--el-bg-color) !important;
      backdrop-filter: blur(10px) !important;
    }

    .el-input__inner {
      font-size: 16px !important;
      height: 100% !important;
    }

    // 图标样式
    .el-input__prefix,
    .el-input__suffix {
      font-size: 24px !important;

      .el-icon {
        font-size: 24px !important;
      }
    }

    .el-input__clear {
      font-size: 24px !important;
    }
  }
}

// 自动补全下拉框样式
.search-autocomplete-popper {
  max-height: none !important;
  height: auto !important;

  .el-autocomplete-suggestion {
    max-height: none !important;
    height: auto !important;
    overflow: visible !important;

    .el-autocomplete-suggestion__wrap {
      max-height: none !important;
      height: auto !important;
      overflow: visible !important;
      overflow-y: visible !important;

      ul {
        max-height: none !important;
        overflow: visible !important;
      }
    }

    .el-autocomplete-suggestion__list {
      padding: 8px 0;
      max-height: none !important;
      overflow: visible !important;

      li {
        padding: 10px 16px;
        line-height: 24px;
        transition: background-color 0.2s ease;

        &:hover {
          background-color: var(--el-fill-color-light);
        }

        &.is-highlighted {
          background-color: var(--el-fill-color);
        }
      }
    }
  }
}
</style>
