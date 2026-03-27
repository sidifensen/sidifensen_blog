<template>
  <div class="search-container">
    <!-- 主容器 -->
    <div class="search-main-container">
      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 搜索区域 -->
        <section class="search-section">
          <h2 class="search-title">
            <el-icon class="search-title-icon"><Search /></el-icon>
            搜索内容
          </h2>

          <div class="search-box-wrapper">
            <div class="search-input-group">
              <div class="search-input-wrapper">
                <el-icon class="search-icon"><Search /></el-icon>
                <input
                  type="text"
                  class="search-input"
                  v-model="searchKeyword"
                  placeholder="搜索文章标题、标签、作者..."
                  @keyup.enter="handleSearch"
                />
                <button v-if="searchKeyword" class="clear-btn" @click="clearKeyword">×</button>
              </div>
              <button class="search-btn" @click="handleSearch" :loading="searchLoading">
                <el-icon><Search /></el-icon>
                搜索
              </button>
            </div>

            <!-- 搜索类型选择 -->
            <div class="search-types">
              <button
                class="search-type-pill"
                :class="{ active: searchType === 'all' }"
                @click="changeSearchType('all')"
              >
                🔍 综合
              </button>
              <button
                class="search-type-pill"
                :class="{ active: searchType === 'title' }"
                @click="changeSearchType('title')"
              >
                📄 标题
              </button>
              <button
                class="search-type-pill"
                :class="{ active: searchType === 'tag' }"
                @click="changeSearchType('tag')"
              >
                🏷️ 标签
              </button>
              <button
                class="search-type-pill"
                :class="{ active: searchType === 'author' }"
                @click="changeSearchType('author')"
              >
                👤 作者
              </button>
            </div>

            <!-- 搜索历史 -->
            <div v-if="!hasSearched && searchHistory.length > 0" class="search-history">
              <div class="section-header">
                <span class="section-title">🕐 搜索历史</span>
                <span class="clear-history" @click="clearHistory">清空</span>
              </div>
              <div class="history-tags">
                <span
                  v-for="(item, index) in searchHistory"
                  :key="index"
                  class="history-tag"
                  @click="searchByHistory(item)"
                >
                  {{ item }}
                  <el-icon class="close-icon" @click.stop="removeHistory(index)"><Close /></el-icon>
                </span>
              </div>
            </div>

            <!-- 热门搜索 -->
            <div v-if="!hasSearched" class="hot-search">
              <div class="section-header">
                <span class="section-title">🔥 热门搜索</span>
              </div>
              <div class="hot-list">
                <div
                  v-for="(item, index) in hotSearches"
                  :key="index"
                  class="hot-item"
                  @click="searchByHot(item.keyword)"
                >
                  <span class="hot-rank" :class="{ 'top-3': index < 3 }">{{ index + 1 }}</span>
                  <span class="hot-text">{{ item.keyword }}</span>
                  <span class="hot-count">{{ formatCount(item.count) }}</span>
                </div>
              </div>
            </div>

            <!-- 移动端专属：热门标签（在热门搜索下方） -->
            <div v-if="!hasSearched" class="mobile-hot-tags">
              <div class="section-header">
                <span class="section-title">🏷️ 热门标签</span>
              </div>
              <div class="tag-cloud">
                <span
                  v-for="(tag, index) in hotTags"
                  :key="index"
                  class="cloud-tag"
                  :class="getTagSizeClass(index)"
                  @click="searchByTag(tag.name)"
                >
                  {{ tag.name }}
                </span>
              </div>
            </div>
          </div>
        </section>

        <!-- 搜索结果区域 -->
        <section v-if="hasSearched" class="results-section">
          <div class="results-header">
            <div class="results-info">
              <span class="result-count">
                找到 <strong>{{ total }}</strong> 条结果
                <span v-if="searchKeyword">"</span>
                <span class="keyword-tag" v-if="searchKeyword">{{ searchKeyword }}</span>
                <span v-if="searchKeyword">"</span>
                <span class="search-type-hint" v-if="searchType === 'all'">（综合搜索：标题 + 标签）</span>
                <span class="search-type-hint" v-if="searchType === 'title'">（标题搜索）</span>
                <span class="search-type-hint" v-if="searchType === 'tag'">（标签搜索）</span>
                <span class="search-type-hint" v-if="searchType === 'author'">（作者搜索）</span>
              </span>
            </div>
            <el-dropdown trigger="click" class="sort-dropdown-wrapper" @visible-change="handleSortDropdownVisible">
              <div class="sort-dropdown-trigger">
                <span class="sort-dropdown-text">{{ sortOptions.find(o => o.value === sortBy)?.label }}</span>
                <el-icon class="sort-arrow-icon" :class="{ 'is-up': isSortDropdownOpen }"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="sort-menu">
                  <el-dropdown-item
                    v-for="option in sortOptions"
                    :key="option.value"
                    @click="changeSortType(option.value)"
                  >
                    {{ option.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <!-- 加载状态 -->
          <div v-if="searchLoading && articles.length === 0" class="loading-container">
            <el-skeleton animated :count="5">
              <template #template>
                <div v-for="i in 5" :key="i" class="article-skeleton">
                  <el-skeleton-item variant="image" style="width: 180px; height: 108px; border-radius: 8px" />
                  <div class="skeleton-content">
                    <el-skeleton-item variant="h3" style="width: 80%; height: 20px; margin-bottom: 10px" />
                    <el-skeleton-item variant="text" style="width: 100%; height: 16px; margin-bottom: 8px" />
                    <el-skeleton-item variant="text" style="width: 90%; height: 16px" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 空状态 -->
          <div v-else-if="articles.length === 0 && !searchLoading" class="empty-state">
            <el-icon class="empty-icon"><DocumentDelete /></el-icon>
            <h3>未找到相关文章</h3>
            <p>尝试更换搜索关键词或清空搜索</p>
            <el-button type="primary" @click="clearSearch">清空搜索</el-button>
          </div>

          <!-- 文章列表 -->
          <div v-else class="article-list">
            <article
              v-for="article in articles"
              :key="article.id"
              class="article-card"
              @click="goToArticle(article)"
            >
              <el-image
                :src="article.coverUrl || ''"
                class="article-cover"
                fit="cover"
              >
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

              <div class="article-content">
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-description">{{ article.description || "暂无描述" }}</p>
                <div class="article-meta">
                  <!-- 第一行：时间、浏览量、点赞量 -->
                  <div class="meta-row stats-row">
                    <span class="meta-item">📅 {{ formatDate(article.createTime) }}</span>
                    <span class="meta-item">👁 {{ formatCompactNumber(article.readCount || 0) }}</span>
                    <span class="meta-item">❤️ {{ formatCompactNumber(article.likeCount || 0) }}</span>
                  </div>
                  <!-- 第二行：作者 -->
                  <div class="meta-row author-row">
                    <span class="meta-item author-link" @click.stop="goToAuthor(article.userId)">
                      👤 {{ article.nickname || "匿名用户" }}
                    </span>
                  </div>
                  <!-- 第三行：标签 -->
                  <div class="meta-row tags-row">
                    <div class="article-tags">
                      <el-tag
                        v-for="(tag, index) in parseTag(article.tag)"
                        :key="index"
                        size="small"
                        class="article-tag"
                      >
                        {{ tag }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </article>

            <!-- 加载更多 -->
            <div v-if="loadingMore" class="loading-more">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>正在加载更多文章...</span>
            </div>
            <div v-else-if="!hasMore && articles.length > 0" class="no-more">
              <el-divider>已加载全部结果</el-divider>
            </div>
          </div>
        </section>

        <!-- 初始状态 -->
        <div v-else class="initial-hint">
          <el-icon class="hint-icon"><Search /></el-icon>
          <h3>搜索小贴士</h3>
          <p>输入关键词，探索你感兴趣的内容吧</p>
        </div>
      </div>

      <!-- 侧边栏 -->
      <aside class="sidebar">
        <!-- 热门标签 -->
        <div class="sidebar-card">
          <h3 class="sidebar-title">🏷️ 热门标签</h3>
          <div class="tag-cloud">
            <span
              v-for="(tag, index) in hotTags"
              :key="index"
              class="cloud-tag"
              :class="getTagSizeClass(index)"
              @click="searchByTag(tag.name)"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>

        <!-- 推荐作者 -->
        <div class="sidebar-card">
          <h3 class="sidebar-title">👨‍💻 推荐作者</h3>
          <div class="author-list">
            <div
              v-for="(author, index) in recommendedAuthors"
              :key="index"
              class="author-item"
              @click="goToAuthor(author.id)"
            >
              <el-avatar :size="40" :src="author.avatar || ''" class="author-avatar" />
              <div class="author-info">
                <div class="author-name">{{ author.nickname || author.username }}</div>
                <div class="author-desc">{{ author.articleCount || 0 }} 篇文章</div>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- 移动端侧边栏 - 在主内容下方垂直排列 -->
    <aside class="mobile-sidebar">
      <!-- 推荐作者 -->
      <div class="sidebar-card">
        <h3 class="sidebar-title">👨‍ 推荐作者</h3>
        <div class="author-list">
          <div
            v-for="(author, index) in recommendedAuthors"
            :key="index"
            class="author-item"
            @click="goToAuthor(author.id)"
          >
            <el-avatar :size="40" :src="author.avatar || ''" class="author-avatar" />
            <div class="author-info">
              <div class="author-name">{{ author.nickname || author.username }}</div>
              <div class="author-desc">{{ author.articleCount || 0 }} 篇文章</div>
            </div>
          </div>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  Search,
  Close,
  ArrowDown,
  Loading,
  Picture,
  DocumentDelete
} from "@element-plus/icons-vue";
import {
  searchArticleByTitle,
  searchArticleByTag,
  searchArticleByAuthor,
  getTitleSuggestions,
  getTagSuggestions,
  getHotArticleList,
  getHotTags
} from "@/api/article";
import { getRecommendedAuthors, getHotSearches } from "@/api/user";
import { formatCompactNumber } from "@/utils/formatNumber";

// 路由
const router = useRouter();
const route = useRoute();

// 响应式数据
const searchKeyword = ref("");
const searchType = ref("all"); // all, title, tag, author
const articles = ref([]);
const searchLoading = ref(false);
const loadingMore = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const hasSearched = ref(false);
const sortBy = ref("default");
const isSortDropdownOpen = ref(false);
// 标记是否还有更多数据（当页加载数量小于 pageSize 说明没有更多了）
const hasMoreData = ref(true);

// 搜索历史（从 localStorage 读取）
const searchHistory = ref([]);

// 热门搜索数据（从后端获取）
const hotSearches = ref([]);

// 热门标签（从后端获取）
const hotTags = ref([]);

// 推荐作者（从后端获取）
const recommendedAuthors = ref([]);

// 排序选项
const sortOptions = ref([
  { value: "default", label: "综合排序" },
  { value: "latest", label: "最新发布" },
  { value: "hot", label: "最热门" },
  { value: "discussed", label: "最多讨论" }
]);

// 计算属性 - 基于 hasMoreData 标记判断是否还有更多
const hasMore = computed(() => {
  // 只要有更多数据标记为 true，就继续加载
  return hasMoreData.value;
});

// 获取标签大小样式
const getTagSizeClass = (index) => {
  if (index === 0) return "large";
  if (index < 3) return "medium";
  return "small";
};

// 格式化数字显示
const formatCount = (count) => {
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + "k";
  }
  return count.toString();
};

// 清空关键词
const clearKeyword = () => {
  searchKeyword.value = "";
};

// 切换搜索类型
const changeSearchType = (type) => {
  searchType.value = type;
  // 如果已经有搜索关键词，立即执行搜索
  if (searchKeyword.value.trim()) {
    if (hasSearched.value) {
      // 已经搜索过，重新搜索
      handleSearch();
    } else {
      // 未搜索过，直接执行搜索
      performSearch(true);
    }
  }
};

// 处理排序下拉菜单显示状态变化
const handleSortDropdownVisible = (visible) => {
  isSortDropdownOpen.value = visible;
};

// 切换排序类型
const changeSortType = (value) => {
  sortBy.value = value;
  // 如果已经有搜索关键词，立即执行搜索
  if (searchKeyword.value.trim() && hasSearched.value) {
    handleSearch();
  }
};

// 执行搜索
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) {
    // 搜索栏为空时，清空搜索恢复到初始状态
    clearSearch();
    return;
  }

  // 添加到搜索历史
  if (keyword && !searchHistory.value.includes(keyword)) {
    searchHistory.value.unshift(keyword);
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop();
    }
    localStorage.setItem("searchHistory", JSON.stringify(searchHistory.value));
  }

  // 更新 URL 参数
  router.replace({
    path: "/search",
    query: {
      keyword: keyword,
      type: searchType.value,
    },
  });

  await performSearch(true);
};

// 根据历史搜索
const searchByHistory = (keyword) => {
  searchKeyword.value = keyword;
  handleSearch();
};

// 根据热门搜索搜索
const searchByHot = (keyword) => {
  searchKeyword.value = keyword;
  searchType.value = "all";
  handleSearch();
};

// 根据标签搜索
const searchByTag = (tagName) => {
  searchKeyword.value = tagName;
  searchType.value = "tag";
  handleSearch();
};

// 移除历史
const removeHistory = (index) => {
  searchHistory.value.splice(index, 1);
  localStorage.setItem("searchHistory", JSON.stringify(searchHistory.value));
};

// 清空历史
const clearHistory = () => {
  searchHistory.value = [];
  localStorage.removeItem("searchHistory");
};

// 执行搜索请求
const performSearch = async (reset = false) => {
  try {
    if (reset) {
      searchLoading.value = true;
      currentPage.value = 1;
      articles.value = [];
      hasMoreData.value = true; // 重置时重置标记
    } else {
      loadingMore.value = true;
    }

    hasSearched.value = true;

    let allArticles = [];
    let totalTitle = 0;
    let totalTag = 0;

    // 根据搜索类型调用不同接口
    if (searchType.value === "all") {
      // 综合搜索：同时搜索标题和标签，合并结果
      const [titleRes, tagRes] = await Promise.all([
        searchArticleByTitle(searchKeyword.value, currentPage.value, pageSize.value),
        searchArticleByTag(searchKeyword.value, currentPage.value, pageSize.value)
      ]);

      // 解析标题搜索结果：PageVo { data: [...], total }
      const titlePageVo = titleRes?.data || {};
      const titleArticles = titlePageVo.data || titlePageVo.records || [];
      totalTitle = titlePageVo.total || titleArticles.length;

      // 解析标签搜索结果：PageVo { data: [...], total }
      const tagPageVo = tagRes?.data || {};
      const tagArticles = tagPageVo.data || tagPageVo.records || [];
      totalTag = tagPageVo.total || tagArticles.length;

      // 合并结果（按文章 ID 去重）
      const articleMap = new Map();
      titleArticles.forEach(article => {
        articleMap.set(article.id, article);
      });
      tagArticles.forEach(article => {
        if (!articleMap.has(article.id)) {
          articleMap.set(article.id, article);
        }
      });
      allArticles = Array.from(articleMap.values());
      // 综合搜索时，total 设置为两者之和（可能包含重复，但用于 hasMore 判断是保守安全的）
      total.value = totalTitle + totalTag;

    } else if (searchType.value === "title") {
      // 标题搜索
      const res = await searchArticleByTitle(searchKeyword.value, currentPage.value, pageSize.value);
      const pageVo = res?.data || {};
      allArticles = pageVo.data || pageVo.records || [];
      total.value = pageVo.total || allArticles.length;

    } else if (searchType.value === "tag") {
      // 标签搜索
      const res = await searchArticleByTag(searchKeyword.value, currentPage.value, pageSize.value);
      const pageVo = res?.data || {};
      allArticles = pageVo.data || pageVo.records || [];
      total.value = pageVo.total || allArticles.length;

    } else if (searchType.value === "author") {
      // 作者搜索
      const res = await searchArticleByAuthor(searchKeyword.value, currentPage.value, pageSize.value);
      const pageVo = res?.data || {};
      allArticles = pageVo.data || pageVo.records || [];
      total.value = pageVo.total || allArticles.length;
    }

    if (reset) {
      articles.value = allArticles;
      hasMoreData.value = true; // 重置时重置标记
    } else {
      articles.value = [...articles.value, ...allArticles];
    }

    // 判断是否还有更多数据：已加载的文章数量是否已经达到总数
    // 当已加载数量 >= 总数时，说明没有更多数据了
    if (articles.value.length >= total.value) {
      hasMoreData.value = false;
    }

    // 只有当页有数据时才递增页码，没有数据说明已经加载完毕
    if (allArticles.length > 0) {
      currentPage.value++;
    }

    if (total.value === 0) {
      ElMessage.info(`未找到相关内容`);
    }
  } catch (error) {
    ElMessage.error("搜索失败，请稍后重试");
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
  hasMoreData.value = true; // 重置 hasMoreData 标记
  router.replace({ path: "/search", query: {} });
};

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

// 解析标签
const parseTag = (tagString) => {
  if (!tagString) return [];
  return tagString.split(",").filter((tag) => tag.trim());
};

// 跳转到文章详情
const goToArticle = (article) => {
  router.push(`/user/${article.userId}/article/${article.id}`);
};

// 跳转到作者个人主页
const goToAuthor = (userId) => {
  router.push(`/user/${userId}`);
};

// 节流函数 - 简化版本
const throttle = (func, delay) => {
  let lastCall = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastCall >= delay) {
      lastCall = now;
      func.apply(this, args);
    }
  };
};

// 滚动加载更多
const handleScroll = throttle(() => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;
  const remaining = documentHeight - scrollTop - windowHeight;

  if (loadingMore.value || searchLoading.value || !hasSearched.value) {
    return;
  }

  // 检查是否还有更多数据
  if (!hasMore.value) {
    return;
  }

  // 距离底部 300px 时触发加载
  if (remaining <= 300) {
    performSearch(false);
  }
}, 300);

// 生命周期
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

// 加载热门标签
const loadHotTags = async () => {
  try {
    const res = await getHotTags(10);
    hotTags.value = res.data || [];
  } catch (error) {
    // 静默处理
  }
};

// 加载推荐作者
const loadRecommendedAuthors = async () => {
  try {
    const res = await getRecommendedAuthors(3);
    recommendedAuthors.value = res.data || [];
  } catch (error) {
    // 静默处理
  }
};

// 加载热门搜索
const loadHotSearches = async () => {
  try {
    const res = await getHotSearches(10);
    // 后端返回格式：[{keyword: "xxx", count: 123}, ...]
    hotSearches.value = res.data || [];
  } catch (error) {
    // 失败时使用默认空数组，不影响页面其他功能
    hotSearches.value = [];
  }
};

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 搜索页面容器
.search-container {
  min-height: 100vh;
  background: var(--bg-page);

  // CSS 变量定义
  --bg-page: #f6f7f9;
  --bg-card: #ffffff;
  --text-primary: #18191c;
  --text-regular: #646a73;
  --text-muted: #8f959e;
  --border: #ebeef0;
  --primary: #0066cc;
  --primary-light: #ecf5ff;
  --success: #52c41a;
  --success-light: #f0f9eb;
  --shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-hover: 0 2px 8px rgba(0, 0, 0, 0.08);

  // 黑夜模式
  html.dark & {
    --bg-page: #121212;
    --bg-card: #1e1e1e;
    --text-primary: #e6e6e6;
    --text-regular: #a0a0a0;
    --text-muted: #737373;
    --border: #2d2d2d;
    --primary: #58a6ff;
    --primary-light: rgba(0, 102, 204, 0.15);
    --success: #73d13d;
    --success-light: rgba(82, 196, 26, 0.15);
    --shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
    --shadow-hover: 0 2px 12px rgba(0, 0, 0, 0.3);
  }

  // 主容器
  .search-main-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: 24px;
  }

  // 主内容区
  .main-content {
    min-width: 0;
  }

  // 搜索区域
  .search-section {
    background: var(--bg-card);
    border-radius: 12px;
    padding: 24px;
    box-shadow: var(--shadow);
    margin-bottom: 24px;

    .search-title {
      font-size: 20px;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 8px;
      color: var(--text-primary);

      .search-title-icon {
        width: 24px;
        height: 24px;
        color: var(--primary);
      }
    }

    .search-box-wrapper {
      .search-input-group {
        display: flex;
        gap: 12px;
        margin-bottom: 16px;

        .search-input-wrapper {
          flex: 1;
          position: relative;

          .search-icon {
            position: absolute;
            left: 14px;
            top: 50%;
            transform: translateY(-50%);
            width: 20px;
            height: 20px;
            color: var(--text-muted);
          }

          .search-input {
            width: 100%;
            height: 48px;
            padding: 0 16px 0 44px;
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 15px;
            background: var(--bg-page);
            color: var(--text-primary);
            transition: all 0.2s;

            &:focus {
              outline: none;
              border-color: var(--primary);
              box-shadow: 0 0 0 3px var(--primary-light);
              background: var(--bg-card);
            }

            &::placeholder {
              color: var(--text-muted);
            }
          }

          .clear-btn {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: transparent;
            border: none;
            font-size: 18px;
            color: var(--text-muted);
            cursor: pointer;
            padding: 4px;

            &:hover {
              color: var(--text-primary);
            }
          }
        }

        .search-btn {
          height: 48px;
          padding: 0 24px;
          background: var(--primary);
          color: white;
          border: none;
          border-radius: 8px;
          font-size: 15px;
          font-weight: 500;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 6px;
          transition: all 0.2s;

          &:hover {
            opacity: 0.9;
            transform: translateY(-1px);
          }

          .el-icon {
            font-size: 18px;
          }
        }
      }

      .search-types {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        margin-bottom: 20px;

        .search-type-pill {
          padding: 6px 14px;
          border-radius: 16px;
          font-size: 13px;
          background: var(--bg-page);
          color: var(--text-regular);
          border: 1px solid var(--border);
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            background: var(--border);
          }

          &.active {
            background: var(--primary);
            color: white;
            border-color: var(--primary);
          }
        }
      }

      // 搜索历史
      .search-history {
        margin-top: 20px;
        padding-top: 20px;
        border-top: 1px solid var(--border);

        .section-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          .section-title {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-muted);
          }

          .clear-history {
            font-size: 12px;
            color: var(--text-muted);
            cursor: pointer;

            &:hover {
              color: var(--primary);
            }
          }
        }

        .history-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;

          .history-tag {
            padding: 6px 12px;
            background: var(--bg-page);
            border-radius: 6px;
            font-size: 13px;
            color: var(--text-regular);
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 6px;
            transition: all 0.2s;

            &:hover {
              background: var(--border);
            }

            .close-icon {
              font-size: 12px;
              opacity: 0.6;

              &:hover {
                opacity: 1;
              }
            }
          }
        }
      }

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
    }
  }

  // 搜索结果区域
  .results-section {
    background: var(--bg-card);
    border-radius: 12px;
    box-shadow: var(--shadow);
    overflow: hidden;

    .results-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      border-bottom: 1px solid var(--border);
      background: var(--bg-page);
      flex-wrap: nowrap;

      .results-info {
        flex: 1;
        min-width: 0;

        .result-count {
          font-size: 14px;
          color: var(--text-regular);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: inline-block;
          max-width: 100%;

          strong {
            color: var(--text-primary);
          }

          .keyword-tag {
            background: var(--primary-light);
            color: var(--primary);
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 500;
          }

          .search-type-hint {
            color: var(--text-muted);
            font-size: 11px;
            margin-left: 4px;
            font-weight: 400;
            white-space: nowrap;
          }
        }
      }

      .sort-dropdown-wrapper {
        .sort-dropdown-trigger {
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 4px 8px;
          background: var(--bg-card);
          border: 1px solid var(--border);
          border-radius: 4px;
          font-size: 12px;
          color: var(--text-regular);
          cursor: pointer;
          flex-shrink: 0;
          white-space: nowrap;
          transition: border-color 0.2s;

          &:hover {
            border-color: var(--primary);
          }

          .sort-dropdown-text {
            margin-right: 2px;
          }

          .sort-arrow-icon {
            transition: transform 0.3s;
            font-size: 14px;

            &.is-up {
              transform: rotate(-180deg);
            }
          }
        }
      }
    }

    .loading-container {
      padding: 20px;

      .article-skeleton {
        display: flex;
        gap: 20px;
        padding: 16px;
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

    .empty-state {
      text-align: center;
      padding: 80px 24px;

      .empty-icon {
        font-size: 56px;
        color: var(--text-muted);
        margin-bottom: 16px;
        opacity: 0.7;
      }

      h3 {
        font-size: 18px;
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 8px;
      }

      p {
        font-size: 14px;
        color: var(--text-muted);
        margin-bottom: 20px;
      }
    }

    .article-list {
      padding: 16px;

      .article-card {
        display: flex;
        gap: 16px;
        padding: 16px;
        border-radius: 10px;
        border: 1px solid var(--border);
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.2s;
        background: var(--bg-card);

        &:hover {
          border-color: var(--primary);
          box-shadow: var(--shadow-hover);
          transform: translateY(-2px);

          .article-title {
            color: var(--primary);
          }
        }

        .article-cover {
          width: 180px;
          height: 108px;
          border-radius: 8px;
          object-fit: cover;
          flex-shrink: 0;
          background: var(--bg-page);

          .image-placeholder,
          .image-error {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--bg-page);
            color: var(--text-muted);

            .el-icon {
              font-size: 32px;
            }
          }
        }

        .article-content {
          flex: 1;
          min-width: 0;
          display: flex;
          flex-direction: column;

          .article-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 8px;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.2s;
          }

          .article-description {
            font-size: 13px;
            color: var(--text-regular);
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            margin-bottom: 12px;
          }

          .article-meta {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-top: auto;
            font-size: 12px;
            color: var(--text-muted);

            // 第一行：统计数据（时间、浏览量、点赞量）
            .stats-row {
              display: flex;
              align-items: center;
              gap: 12px;
              flex-wrap: nowrap;
              overflow-x: auto;
              -webkit-overflow-scrolling: touch;

              .meta-item {
                display: flex;
                align-items: center;
                gap: 4px;
                flex-shrink: 0;
                white-space: nowrap;
              }
            }

            // 第二行：作者
            .author-row {
              .author-link {
                display: flex;
                align-items: center;
                gap: 4px;
                cursor: pointer;
                transition: color 0.2s;
                color: var(--text-muted);

                &:hover {
                  color: var(--primary);
                }
              }
            }

            // 第三行：标签
            .tags-row {
              .article-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 6px;

                .article-tag {
                  background: var(--bg-page);
                  color: var(--text-regular);
                  border: none;
                }
              }
            }
          }
        }
      }

      .loading-more {
        text-align: center;
        padding: 24px;
        color: var(--text-muted);
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;

        .el-icon {
          font-size: 16px;
        }
      }

      .no-more {
        padding: 16px;

        :deep(.el-divider__text) {
          color: var(--text-muted);
          font-size: 13px;
        }
      }
    }
  }

  // 初始提示
  .initial-hint {
    text-align: center;
    padding: 80px 24px;
    color: var(--text-muted);

    .hint-icon {
      font-size: 48px;
      margin-bottom: 16px;
      opacity: 0.7;
    }

    h3 {
      font-size: 18px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 8px;
    }

    p {
      font-size: 14px;
    }
  }

  // 侧边栏
  .sidebar {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .sidebar-card {
      background: var(--bg-card);
      border-radius: 12px;
      padding: 20px;
      box-shadow: var(--shadow);

      .sidebar-title {
        font-size: 15px;
        font-weight: 600;
        margin-bottom: 16px;
        color: var(--text-primary);
      }
    }

    // 标签云
    .tag-cloud {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .cloud-tag {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 6px 12px;
        background: var(--bg-page);
        border-radius: 16px;
        font-size: 13px;
        color: var(--text-regular);
        cursor: pointer;
        transition: all 0.2s;
        line-height: 1;

        &:hover {
          background: var(--primary);
          color: white;
        }

        &.large {
          font-size: 15px;
          padding: 6px 14px;
          font-weight: 500;
        }

        &.medium {
          font-size: 14px;
        }

        &.small {
          font-size: 12px;
        }
      }
    }

    // 作者列表
    .author-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .author-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px;
        border-radius: 8px;
        cursor: pointer;
        transition: background 0.2s;

        &:hover {
          background: var(--bg-page);
        }

        .author-avatar {
          flex-shrink: 0;
          background: var(--bg-page);
        }

        .author-info {
          flex: 1;
          min-width: 0;

          .author-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .author-desc {
            font-size: 12px;
            color: var(--text-muted);
            margin-top: 2px;
          }
        }
      }
    }
  }
}

// 响应式设计
// 900px-1199px: 小屏桌面，隐藏侧边栏
@media (max-width: 1199px) and (min-width: 900px) {
  .search-main-container {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }
}

// <768px: 移动端，侧边栏移动到主内容下方垂直排列
@media (max-width: 768px) {
  .search-container {
    .search-main-container {
      grid-template-columns: 1fr;
      padding: 16px;

      // 隐藏原始位置的侧边栏
      .sidebar {
        display: none;
      }
    }
  }

  // 移动端侧边栏 - 显示在主内容下方
  .search-container > .mobile-sidebar {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 16px;

    .sidebar-card {
      background: var(--bg-card);
      border-radius: 12px;
      padding: 16px;
      box-shadow: var(--shadow);

      .sidebar-title {
        font-size: 15px;
        font-weight: 600;
        margin-bottom: 12px;
        color: var(--text-primary);
      }
    }

    // 标签云
    .tag-cloud {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .cloud-tag {
        padding: 6px 12px;
        font-size: 13px;
      }
    }

    // 作者列表
    .author-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .author-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px;
        border-radius: 8px;
        cursor: pointer;
        transition: background 0.2s;

        &:hover {
          background: var(--bg-page);
        }

        .author-avatar {
          flex-shrink: 0;
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: var(--bg-page);
        }

        .author-info {
          flex: 1;
          min-width: 0;

          .author-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .author-desc {
            font-size: 12px;
            color: var(--text-muted);
            margin-top: 2px;
          }
        }
      }
    }
  }
}

// ≥769px: 桌面端，隐藏移动端侧边栏，显示原始侧边栏
@media (min-width: 769px) {
  .search-container > .mobile-sidebar {
    display: none;
  }

  // 隐藏移动端专属热门标签
  .mobile-hot-tags {
    display: none;
  }
}

// 768px 移动端其他样式优化
@media (max-width: 768px) {
  .search-container {
    .search-main-container {
      padding: 16px;
    }

    .search-section {
      padding: 16px;

      .search-box-wrapper {
        .search-input-group {
          flex-direction: column;

          .search-input-wrapper {
            width: 100%;
          }

          .search-btn {
            width: 100%;
            justify-content: center;
          }
        }

        .hot-list {
          grid-template-columns: 1fr;
        }

        // 移动端专属：热门标签（在热门搜索下方）
        .mobile-hot-tags {
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

          .tag-cloud {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;

            .cloud-tag {
              display: inline-flex;
              align-items: center;
              justify-content: center;
              padding: 6px 12px;
              font-size: 13px;
              background: var(--bg-page);
              border-radius: 16px;
              color: var(--text-regular);
              cursor: pointer;
              transition: all 0.2s;
              line-height: 1;
              min-height: 32px;

              &:hover {
                background: var(--primary);
                color: white;
              }

              &.large {
                font-size: 15px;
                padding: 6px 14px;
                font-weight: 500;
              }

              &.medium {
                font-size: 14px;
              }

              &.small {
                font-size: 12px;
              }
            }
          }
        }
      }
    }

    .results-section {
      .results-header {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        gap: 8px;

        .results-info {
          flex: 1;
          min-width: 0;

          .result-count {
            font-size: 13px;

            .keyword-tag {
              padding: 2px 6px;
            }

            .search-type-hint {
              font-size: 10px;
            }
          }
        }

        .sort-dropdown-wrapper {
          flex-shrink: 0;
          min-height: 32px;

          .sort-dropdown-trigger {
            padding: 4px 10px;
            font-size: 12px;
          }
        }
      }

      .article-list {
        .article-card {
          flex-direction: column;

          .article-cover {
            width: 100%;
            height: 160px;
          }
        }
      }
    }
  }
}

// 480px 小屏手机优化
@media (max-width: 480px) {
  .search-container {
    // 基础布局
    .search-main-container {
      padding: 12px;
    }

    .search-section {
      padding: 12px;

      .search-title {
        font-size: 18px;
      }

      // 搜索输入区域
      .search-box-wrapper {
        .search-input-group {
          gap: 8px;

          .search-input {
            height: 44px;
            font-size: 15px;
          }

          .search-btn {
            height: 44px;
            min-height: 44px;
          }
        }

        // 搜索类型按钮 2×2 网格
        .search-types {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 8px;

          .search-type-pill {
            min-height: 44px;
            justify-content: center;
            -webkit-tap-highlight-color: transparent;
            user-select: none;
          }
        }

        // 搜索历史 - 标签变小
        .search-history {
          .section-header {
            margin-bottom: 8px;
          }

          .history-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;

            .history-tag {
              padding: 4px 8px;
              font-size: 12px;
              height: 26px;

              .close-icon {
                font-size: 11px;
              }
            }
          }
        }

        // 热门搜索单列
        .hot-search {
          .hot-list {
            grid-template-columns: 1fr;
            gap: 8px;

            .hot-item {
              padding: 8px 10px;

              .hot-rank {
                width: 18px;
                height: 18px;
                font-size: 11px;
              }

              .hot-text {
                font-size: 13px;
              }

              .hot-count {
                font-size: 11px;
              }
            }
          }
        }

        // 移动端专属热门标签
        .mobile-hot-tags {
          .tag-cloud {
            gap: 6px;

            .cloud-tag {
              padding: 5px 10px;
              font-size: 12px;
            }
          }
        }
      }
    }

    // 搜索结果区域
    .results-section {
      .results-header {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
        padding: 12px 16px;

        .results-info {
          flex: 1;
          min-width: 0;

          .result-count {
            font-size: 13px;
            flex-wrap: wrap;

            .keyword-tag {
              padding: 2px 6px;
            }

            .search-type-hint {
              font-size: 10px;
            }
          }
        }

        .sort-dropdown-wrapper {
          flex-shrink: 0;
          min-height: 36px;

          .sort-dropdown-trigger {
            justify-content: center;
            padding: 6px 12px;
            font-size: 12px;
            -webkit-tap-highlight-color: transparent;
          }
        }
      }

      // 文章卡片优化
      .article-list {
        .article-card {
          padding: 12px;
          gap: 0px;

          .article-cover {
            height: 120px;
          }

          .article-title {
            font-size: 15px;
          }

          .article-description {
            font-size: 13px;
            line-height: 1.7;
          }

          .article-meta {
            font-size: 12px;
            gap: 0px;

            .article-tags {
              margin-left: 0;
              flex-wrap: wrap;
            }
          }
        }
      }

      // 空状态优化
      .empty-state {
        padding: 60px 16px;

        .empty-icon {
          font-size: 48px;
        }

        h3 {
          font-size: 16px;
        }

        p {
          font-size: 13px;
        }
      }

      // 骨架屏优化
      .loading-container {
        .article-skeleton {
          flex-direction: column;
          gap: 12px;

          .skeleton-content {
            gap: 6px;
          }
        }
      }
    }

    // 初始提示区域
    .initial-hint {
      padding: 40px 16px;

      .hint-icon {
        font-size: 36px;
      }

      h3 {
        font-size: 16px;
      }

      p {
        font-size: 13px;
      }
    }

    // 移动端侧边栏
    .mobile-sidebar {
      padding: 12px;
      gap: 12px;

      .sidebar-card {
        padding: 12px;

        .sidebar-title {
          font-size: 15px;
        }
      }

      .tag-cloud {
        gap: 6px;

        .cloud-tag {
          padding: 6px 10px;
          font-size: 12px;
        }
      }

      .author-list {
        gap: 8px;

        .author-item {
          padding: 8px;

          .author-avatar {
            width: 36px;
            height: 36px;
          }

          .author-name {
            font-size: 13px;
          }

          .author-desc {
            font-size: 11px;
          }
        }
      }
    }
  }

  // 触摸优化 - 所有可点击元素
  .search-container {
    -webkit-tap-highlight-color: transparent;

    button,
    .search-type-pill,
    .history-tag,
    .hot-item,
    .article-card,
    .cloud-tag,
    .author-item,
    .sort-dropdown-trigger,
    .clear-btn,
    .close-icon,
    .article-tag,
    .author-link {
      user-select: none;
      -webkit-user-select: none;
      touch-action: manipulation;
      min-height: 44px;
    }
  }
}

// 尊重用户动画偏好
@media (prefers-reduced-motion: reduce) {
  .search-container {
    *,
    *::before,
    *::after {
      animation-duration: 0.01ms !important;
      animation-iteration-count: 1 !important;
      transition-duration: 0.01ms !important;
    }
  }
}
</style>
