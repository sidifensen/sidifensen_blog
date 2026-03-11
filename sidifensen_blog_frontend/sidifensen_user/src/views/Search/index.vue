<template>
  <div class="search-container">
    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="container">
        <div class="search-box-wrapper">
          <!-- 页面标题 -->
          <div class="search-page-title">
            <h1>搜索文章</h1>
            <p>按标题或标签快速查找内容</p>
          </div>

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

            <!-- 按钮组 -->
            <div class="search-buttons">
              <!-- 搜索标题按钮 -->
              <el-button type="primary" size="large" class="search-btn" @click="handleSearch" :loading="searchLoading">
                <el-icon><Search /></el-icon>
                标题
              </el-button>

              <!-- 搜索标签按钮 -->
              <el-button type="success" size="large" class="search-tag-btn" @click="handleSearchByTag" :loading="searchLoading">
                <el-icon><PriceTag /></el-icon>
                标签
              </el-button>
            </div>
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
                    <svg-icon name="like" width="14px" height="14px" margin-right="4px" color="var(--text-muted)" />
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
  padding-top: 48px;
  background: var(--bg-page);

  // 主题变量（浅色）
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  --bg-elevated: #ffffff;
  --control-bg: #ffffff;
  --control-bg-hover: #ffffff;
  --item-hover-bg: rgba(15, 23, 42, 0.03);

  --text-primary: #0f172a;
  --text-regular: #334155;
  --text-muted: #64748b;

  --border: #e2e8f0;
  --border-strong: #cbd5e1;

  --shadow-1: 0 1px 3px rgba(15, 23, 42, 0.08);
  --shadow-2: 0 8px 24px rgba(15, 23, 42, 0.08);

  --badge-primary-bg: rgba(2, 132, 199, 0.08);
  --badge-primary-border: rgba(2, 132, 199, 0.22);
  --badge-success-bg: rgba(16, 185, 129, 0.12);
  --badge-success-border: rgba(16, 185, 129, 0.28);

  --spinner-track: rgba(148, 163, 184, 0.35);

  // 按钮（浅色）
  --btn-title-bg: rgba(2, 132, 199, 0.1);
  --btn-title-border: rgba(2, 132, 199, 0.25);
  --btn-title-hover-bg: rgba(2, 132, 199, 0.16);
  --btn-title-hover-border: rgba(2, 132, 199, 0.35);
  --btn-title-focus: rgba(2, 132, 199, 0.22);

  --btn-tag-bg: rgba(16, 185, 129, 0.12);
  --btn-tag-border: rgba(16, 185, 129, 0.28);
  --btn-tag-hover-bg: rgba(16, 185, 129, 0.18);
  --btn-tag-hover-border: rgba(16, 185, 129, 0.38);
  --btn-tag-focus: rgba(16, 185, 129, 0.24);

  // 黑夜模式适配（背景色对齐个人主页的深蓝色调）
  html.dark & {
    --bg-page: #0f172a;
    --bg-card: #1e293b;
    --bg-elevated: #22314b;
    --control-bg: #1e293b;
    --control-bg-hover: #22314b;
    --item-hover-bg: rgba(255, 255, 255, 0.04);

    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --text-muted: #94a3b8;

    --border: #334155;
    --border-strong: #475569;

    --shadow-1: 0 1px 3px rgba(0, 0, 0, 0.35);
    --shadow-2: 0 10px 28px rgba(0, 0, 0, 0.35);

    --badge-primary-bg: rgba(56, 189, 248, 0.12);
    --badge-primary-border: rgba(56, 189, 248, 0.22);
    --badge-success-bg: rgba(52, 211, 153, 0.14);
    --badge-success-border: rgba(52, 211, 153, 0.22);

    --spinner-track: rgba(148, 163, 184, 0.28);

    // 按钮（黑夜）
    --btn-title-bg: rgba(56, 189, 248, 0.16);
    --btn-title-border: rgba(56, 189, 248, 0.26);
    --btn-title-hover-bg: rgba(56, 189, 248, 0.22);
    --btn-title-hover-border: rgba(56, 189, 248, 0.36);
    --btn-title-focus: rgba(56, 189, 248, 0.3);

    --btn-tag-bg: rgba(52, 211, 153, 0.16);
    --btn-tag-border: rgba(52, 211, 153, 0.26);
    --btn-tag-hover-bg: rgba(52, 211, 153, 0.22);
    --btn-tag-hover-border: rgba(52, 211, 153, 0.36);
    --btn-tag-focus: rgba(52, 211, 153, 0.3);
  }

  // 搜索区域
  .search-section {
    padding: 32px 0 20px;
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

        // 页面标题
        .search-page-title {
          text-align: center;
          margin-bottom: 20px;

          h1 {
            font-size: 28px;
            font-weight: 650;
            color: var(--text-primary);
            margin: 0 0 8px 0;
            letter-spacing: -0.2px;
          }

          p {
            font-size: 14px;
            color: var(--text-muted);
            margin: 0;
          }
        }

        // 搜索输入组
        .search-input-group {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;
          padding: 10px;
          background: var(--bg-card);
          border-radius: 16px;
          border: 1px solid var(--border);
          box-shadow: var(--shadow-1);
          transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;

          &:hover {
            background: var(--control-bg-hover);
            border-color: var(--border-strong);
          }

          &:focus-within {
            border-color: var(--el-color-primary);
            box-shadow: var(--shadow-2);
          }

          // 搜索输入框（自动补全）
          .search-input {
            flex: 1;

            :deep(.el-input__wrapper) {
              background: transparent !important;
              box-shadow: none !important;
              border: none !important;
              padding: 10px 12px !important;
              height: 44px !important;

              .el-input__inner {
                font-size: 15px !important;
                color: var(--text-primary);
                font-weight: 500;

                &::placeholder {
                  color: var(--text-muted);
                  font-weight: 400;
                }
              }

              .el-input__prefix {
                .el-icon {
                  color: var(--el-color-primary);
                  font-size: 18px !important;
                }
              }

              .el-input__suffix {
                .el-icon {
                  color: var(--text-muted);
                  font-size: 18px !important;

                  &:hover {
                    color: var(--el-color-primary);
                  }
                }
              }
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
              color: var(--text-regular);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          // 按钮组
          .search-buttons {
            display: flex;
            gap: 8px;
            flex-shrink: 0;

            // 搜索按钮通用样式
            .search-btn,
            .search-tag-btn {
              border-radius: 10px;
              height: 44px;
              padding: 0 14px;
              font-weight: 600;
              font-size: 14px;
              letter-spacing: 0.2px;
              border: 1px solid transparent;
              transition: background-color 0.15s ease, border-color 0.15s ease, color 0.15s ease;
              white-space: nowrap;
              box-shadow: none !important;
              user-select: none;

              .el-icon {
                margin-right: 6px;
                font-size: 16px;
                color: currentColor;
              }

              &:hover,
              &:active,
              &:focus-visible {
                box-shadow: none !important;
              }
            }

            // 标题按钮（更克制的软填充）
            .search-btn {
              background-color: var(--btn-title-bg) !important;
              border-color: var(--btn-title-border) !important;
              color: var(--el-color-primary) !important;

              &:hover {
                background-color: var(--btn-title-hover-bg) !important;
                border-color: var(--btn-title-hover-border) !important;
              }

              &:focus-visible {
                outline: none;
                box-shadow: 0 0 0 3px var(--btn-title-focus) !important;
              }
            }

            // 标签按钮（与标题一致的风格）
            .search-tag-btn {
              background-color: var(--btn-tag-bg) !important;
              border-color: var(--btn-tag-border) !important;
              color: var(--el-color-success) !important;

              &:hover {
                background-color: var(--btn-tag-hover-bg) !important;
                border-color: var(--btn-tag-hover-border) !important;
              }

              &:focus-visible {
                outline: none;
                box-shadow: 0 0 0 3px var(--btn-tag-focus) !important;
              }
            }
          }
        }

        // 搜索信息
        .search-info {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 14px 24px;
          background: var(--bg-card);
          border-radius: 14px;
          box-shadow: var(--shadow-1);
          border: 1px solid var(--border);

          // 搜索类型标识
          .search-type-badge {
            padding: 6px 14px;
            background: var(--badge-primary-bg);
            color: var(--el-color-primary);
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;
            border: 1px solid var(--badge-primary-border);

            &.tag-search {
              background: var(--badge-success-bg);
              color: var(--el-color-success);
              border-color: var(--badge-success-border);
            }
          }

          // 搜索关键字
          .search-keyword {
            font-size: 15px;
            font-weight: 600;
            color: var(--text-primary);
            padding: 2px 8px;
            border-radius: 8px;
          }

          // 搜索结果数量
          .search-result-count {
            margin-left: auto;
            font-size: 14px;
            color: var(--text-muted);
            font-weight: 500;
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
        padding: 72px 24px;
        background: var(--bg-card);
        border-radius: 16px;
        box-shadow: var(--shadow-1);
        border: 1px solid var(--border);

        // 搜索图标
        .search-icon {
          font-size: 64px;
          color: var(--text-muted);
          margin-bottom: 18px;
        }

        // 标题
        h3 {
          font-size: 20px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0 0 12px 0;
        }

        // 描述
        p {
          font-size: 14px;
          color: var(--text-muted);
          margin: 0;
        }
      }

      // 加载容器
      .loading-container {
        background: var(--bg-card);
        border-radius: 16px;
        padding: 30px;
        box-shadow: var(--shadow-1);
        border: 1px solid var(--border);

        // 骨架屏
        .article-skeleton {
          display: flex;
          gap: 20px;
          padding: 20px 0;
          border-bottom: 1px solid var(--border);

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
        background: var(--bg-card);
        border-radius: 16px;
        padding: 80px 24px;
        box-shadow: var(--shadow-1);
        border: 1px solid var(--border);
        text-align: center;

        // 空状态图标
        .empty-icon {
          font-size: 64px;
          color: var(--text-muted);
          margin-bottom: 16px;
        }

        :deep(.el-empty__description) {
          font-size: 16px;
          margin-top: 16px;
          color: var(--text-muted);
        }
      }

      // 文章列表容器
      .article-list {
        background: var(--bg-card);
        border-radius: 16px;
        padding: 8px 0;
        box-shadow: var(--shadow-1);
        border: 1px solid var(--border);

        // 文章项
        .article-item {
          display: flex;
          gap: 24px;
          padding: 18px 20px;
          margin-bottom: 0;
          background: transparent;
          border-radius: 0;
          border: none;
          border-bottom: 1px solid var(--border);
          cursor: pointer;
          transition: background-color 0.15s ease;
          position: relative;
          overflow: hidden;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background: var(--item-hover-bg);
          }

          // 文章封面包装器
          .article-cover-wrapper {
            flex-shrink: 0;

            // 文章封面
            .article-cover {
              width: 220px;
              height: 138px;
              border-radius: 12px;
              overflow: hidden;
              box-shadow: none;
              transition: none;
              border: 1px solid var(--border);

              // 图片加载占位符
              .image-placeholder {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--bg-page);
                color: var(--text-muted);

                .el-icon {
                  font-size: 36px;
                }
              }

              // 图片加载错误
              .image-error {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--bg-page);
                color: var(--text-muted);

                .el-icon {
                  font-size: 36px;
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
              font-weight: 700;
              color: var(--text-primary);
              margin: 0 0 14px 0;
              line-height: 1.45;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              transition: color 0.15s ease;
              letter-spacing: -0.3px;
            }

            // 文章描述
            .article-description {
              font-size: 14px;
              color: var(--text-muted);
              margin: 0 0 16px 0;
              line-height: 1.7;
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
              gap: 20px;
              flex-wrap: wrap;
              font-size: 13px;
              color: var(--text-muted);

              // 作者信息
              .author-info {
                display: flex;
                align-items: center;
                gap: 10px;

                // 作者头像
                .author-avatar {
                  border: 1px solid var(--border);
                  box-shadow: none;
                }

                // 作者名称
                .author-name {
                  font-weight: 600;
                  color: var(--text-regular);
                }
              }

              // 统计信息
              .article-stats {
                display: flex;
                align-items: center;
                gap: 16px;

                // 统计项
                .stat-item {
                  display: flex;
                  align-items: center;
                  gap: 5px;
                  padding: 0;
                  background: transparent;
                  border-radius: 0;
                  font-weight: 500;
                  transition: color 0.15s ease;

                  .el-icon {
                    font-size: 15px;
                  }
                }
              }

              // 发布时间
              .article-date {
                margin-left: auto;
                font-weight: 500;
              }
            }

            // 文章标签
            .article-tags {
              display: flex;
              gap: 8px;
              flex-wrap: wrap;
              margin-top: 12px;

              // 标签项
              .tag-item {
                border-radius: 10px;
              }
            }
          }
        }

        // 加载更多指示器
        .load-more-indicator {
          margin-top: 36px;
          text-align: center;

          // 加载更多中
          .loading-more {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            padding: 24px;
            color: var(--text-muted);
            font-weight: 500;
            background: var(--bg-card);
            border-radius: 14px;
            border: 1px solid var(--border);
            box-shadow: var(--shadow-1);

            // 加载动画
            .loading-spinner {
              width: 24px;
              height: 24px;
              border: 3px solid var(--spinner-track);
              border-top-color: var(--el-color-primary);
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }
          }

          // 没有更多
          .no-more {
            padding: 20px 0;

            :deep(.el-divider__text) {
              color: var(--text-muted);
              font-size: 14px;
              font-weight: 500;
              background: var(--bg-card);
              padding: 8px 20px;
              border-radius: 20px;
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
    padding-top: 32px;

    // 搜索区域
    .search-section {
      padding: 24px 0;

      .container {
        padding: 0 16px;

        // 搜索框包装器
        .search-box-wrapper {
          // 页面标题
          .search-page-title {
            h1 {
              font-size: 28px;
            }

            p {
              font-size: 14px;
            }
          }

          // 搜索输入组
          .search-input-group {
            flex-direction: column;
            border-radius: 14px;
            padding: 12px;

            // 搜索输入框
            .search-input {
              width: 100%;

              :deep(.el-input__wrapper) {
                padding: 12px 16px !important;
              }
            }

            // 按钮组
            .search-buttons {
              width: 100%;
              flex-direction: row;
              gap: 8px;

              // 搜索按钮
              .search-btn,
              .search-tag-btn {
                flex: 1;
                height: 44px;
                padding: 0 14px !important;
                font-size: 14px !important;
              }
            }
          }

          // 搜索信息
          .search-info {
            flex-direction: column;
            align-items: flex-start;
            padding: 14px 18px;
            border-radius: 16px;

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
        padding: 0 16px;

        // 文章列表
        .article-list {
          padding: 8px 0;
          border-radius: 16px;

          // 文章项
          .article-item {
            flex-direction: column;
            gap: 16px;
            padding: 18px 16px;
            margin-bottom: 0;

            &:hover {
              background: var(--item-hover-bg);
            }

            // 文章封面包装器
            .article-cover-wrapper {
              width: 100%;

              .article-cover {
                width: 100%;
                height: 180px;
              }
            }

            // 文章信息
            .article-info {
              // 文章标题
              .article-title {
                font-size: 18px;
              }

              // 文章元信息
              .article-meta {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;

                .article-stats {
                  width: 100%;
                  justify-content: flex-start;
                }

                .article-date {
                  margin-left: 0;
                }
              }

              // 文章标签
              .article-tags {
                .tag-item {
                  padding: 4px 10px;
                  font-size: 11px;
                }
              }
            }
          }
        }

        // 加载更多指示器
        .load-more-indicator {
          margin-top: 24px;

          .loading-more {
            padding: 20px;
            font-size: 13px;

            .loading-spinner {
              width: 20px;
              height: 20px;
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

// 搜索输入框全局样式
.search-section {
  .search-input {
    .el-input__wrapper {
      border-radius: 12px !important;
      padding: 10px 12px !important;
      height: 44px !important;
      min-height: 44px !important;
      background: transparent !important;
      box-shadow: none !important;
    }

    .el-input__inner {
      font-size: 15px !important;
      height: 100% !important;
      color: var(--text-primary) !important;
      font-weight: 500 !important;

      &::placeholder {
        color: var(--text-muted) !important;
        font-weight: 400 !important;
      }
    }

    // 图标样式
    .el-input__prefix,
    .el-input__suffix {
      .el-icon {
        font-size: 18px !important;
      }
    }

    .el-input__prefix {
      .el-icon {
        color: var(--el-color-primary) !important;
      }
    }

    .el-input__suffix {
      .el-icon {
        color: var(--text-muted) !important;
      }
    }

    .el-input__clear {
      .el-icon {
        font-size: 18px !important;
        color: var(--text-muted) !important;

        &:hover {
          color: var(--el-color-primary) !important;
        }
      }
    }
  }
}

// 自动补全下拉框样式
.search-autocomplete-popper {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: var(--el-box-shadow-light) !important;
  background: var(--el-bg-color-overlay) !important;
  border: 1px solid var(--el-border-color-lighter) !important;

  .el-autocomplete-suggestion {
    .el-autocomplete-suggestion__wrap {
      max-height: 400px !important;
      overflow-y: auto !important;
      padding: 8px !important;

      ul {
        padding: 0 !important;
      }
    }

    .el-autocomplete-suggestion__list {
      padding: 0 !important;

      li {
        padding: 12px 16px !important;
        margin: 4px 0 !important;
        border-radius: 10px !important;
        line-height: 24px !important;
        font-size: 14px !important;
        color: var(--el-text-color-primary) !important;
        transition: background-color 0.15s ease, color 0.15s ease !important;

        &:hover {
          background-color: var(--el-fill-color-light) !important;
        }

        &.is-highlighted {
          background-color: var(--el-color-primary-light-9) !important;
          color: var(--el-color-primary) !important;
          font-weight: 600 !important;
        }
      }
    }
  }
}
</style>
