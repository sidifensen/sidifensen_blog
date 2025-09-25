<template>
  <div class="user-homepage">
    <!-- 用户信息区域 -->
    <UserProfileCard :user-info="userInfo" :user-loading="userLoading" :article-statistics="articleStatistics" :total-views="totalViews" :is-current-user="isCurrentUser" :is-followed="isFollowed" :follow-loading="followLoading" @follow="handleFollow" @message="handleMessage" />

    <!-- 内容区域 -->
    <div class="content-section">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-content">
            <!-- 文章筛选标签 -->
            <div class="article-filters">
              <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane label="文章" name="article">
                  <!-- 文章筛选条件 -->
                  <div class="filter-controls">
                    <!-- 可见范围筛选 -->
                    <div v-if="isCurrentUser" class="visibility-filter">
                      <el-select v-model="visibilityType" @change="handleVisibilityChange" placeholder="可见范围" size="default">
                        <el-option label="全部可见" value="all" />
                        <el-option label="仅我可见" value="private" />
                        <el-option label="审核中&失败" value="pending" />
                      </el-select>
                    </div>
                    <!-- 排序筛选条件 -->
                    <div class="sort-filters">
                      <el-radio-group v-model="sortType" @change="handleSortChange">
                        <el-radio value="time">时间排序</el-radio>
                        <el-radio value="views">阅读量排序</el-radio>
                      </el-radio-group>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="专栏" name="column" />
                <el-tab-pane label="收藏" name="favorite" />
              </el-tabs>
            </div>

            <!-- 文章列表 -->
            <ArticleList v-if="activeTab === 'article'" :article-list="articleList" :article-loading="articleLoading" :loading-more="loadingMore" :is-current-user="isCurrentUser" @article-click="goToArticle" />

            <!-- 专栏列表 -->
            <ColumnList v-if="activeTab === 'column'" :column-list="columnList" :column-loading="columnLoading" :loading-more="loadingMore" @column-click="goToColumn" />

            <!-- 收藏列表 -->
            <FavoriteList v-if="activeTab === 'favorite'" :favorite-list="favoriteList" :favorite-loading="favoriteLoading" @toggle-favorite="toggleFavorite" @article-click="goToArticle" />
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 个人成就 -->
            <div class="sidebar-card">
              <h4 class="card-title">个人成就</h4>
              <div class="achievements">
                <div class="achievement-item" v-if="articleStatistics?.publishedCount >= 10">
                  <el-icon class="achievement-icon"><Trophy /></el-icon>
                  <span>创作达人</span>
                </div>
                <div class="achievement-item" v-if="totalViews >= 1000">
                  <el-icon class="achievement-icon"><View /></el-icon>
                  <span>阅读之星</span>
                </div>
                <div class="achievement-item" v-if="userInfo?.fansCount >= 100">
                  <el-icon class="achievement-icon"><User /></el-icon>
                  <span>人气作者</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 返回顶部按钮 - 统一在父组件管理 -->
    <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
      <el-icon>
        <ArrowUp />
      </el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Trophy, View, User, ArrowUp } from "@element-plus/icons-vue";
import { getUserInfoById } from "@/api/user";
import { getUserArticleList, getUserArticleStatisticsById } from "@/api/article";
import { getUserColumnList } from "@/api/column";
import { getFavoriteListByUserId, getArticleListByFavoriteId } from "@/api/favorite";
import { useUserStore } from "@/stores/userStore";
import UserProfileCard from "./components/UserProfileCard.vue";
import ArticleList from "./components/ArticleList.vue";
import ColumnList from "./components/ColumnList.vue";
import FavoriteList from "./components/FavoriteList.vue";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const userLoading = ref(false); // 用户信息加载状态
const articleLoading = ref(false); // 文章列表加载状态
const columnLoading = ref(false); // 专栏列表加载状态
const favoriteLoading = ref(false); // 收藏夹加载状态
const loadingMore = ref(false); // 加载更多数据状态
const followLoading = ref(false); // 关注操作加载状态
const userInfo = ref(null); // 用户信息数据
const articleList = ref([]); // 文章列表数据
const columnList = ref([]); // 专栏列表数据
const favoriteList = ref([]); // 收藏夹列表数据
const total = ref(0); // 文章总数
const columnTotal = ref(0); // 专栏总数
const totalViews = ref(0); // 总阅读量
const articleStatistics = ref(null); // 文章统计信息
const activeTab = ref("article"); // 当前激活的标签页
const sortType = ref("time"); // 排序类型：time-时间排序，views-阅读量排序
const visibilityType = ref("all"); // 可见范围类型：all-全部可见，private-仅我可见，pending-审核中&失败
const isFollowed = ref(false); // 是否已关注该用户
const hasMore = ref(true); // 是否还有更多数据可加载
const columnHasMore = ref(true); // 专栏是否还有更多数据可加载
const currentPage = ref(1); // 当前页码
const columnCurrentPage = ref(1); // 专栏当前页码
const showBackToTop = ref(false); // 是否显示返回顶部按钮

// 每页数据量
const pageSize = ref(10);

// 计算属性
const isCurrentUser = computed(() => {
  return userStore.user?.id === parseInt(route.params.userId);
});

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    userLoading.value = true;
    const userId = route.params.userId;
    const res = await getUserInfoById(userId);
    userInfo.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
    console.error("获取用户信息失败:", error);
  } finally {
    userLoading.value = false;
  }
};

// 获取文章统计信息
const fetchArticleStatistics = async () => {
  try {
    const userId = route.params.userId;
    const res = await getUserArticleStatisticsById(userId);
    articleStatistics.value = res.data.data;
  } catch (error) {
    console.error("获取文章统计信息失败:", error);
  }
};

// 获取文章列表
const fetchArticleList = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!hasMore.value || articleLoading.value || loadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      articleLoading.value = true;
    } else {
      loadingMore.value = true;
    }

    const userId = route.params.userId;

    // 构建查询参数
    const articleStatusDto = {
      userId: parseInt(userId),
    };

    // 文章标签页，根据排序类型设置orderBy
    articleStatusDto.orderBy = sortType.value === "time" ? 0 : 1;

    // 根据可见范围类型设置查询条件
    if (visibilityType.value === "all") {
      // 全部可见：查询全部可见且审核通过的文章
      articleStatusDto.visibleRange = 0;
      articleStatusDto.examineStatus = 1;
    } else if (visibilityType.value === "private") {
      // 仅我可见：查询私有文章
      articleStatusDto.visibleRange = 1;
    } else if (visibilityType.value === "pending") {
      // 审核中&失败：
      articleStatusDto.examineStatusList = [0, 2]; // 0-审核中, 2-审核失败
    }

    const res = await getUserArticleList(currentPage.value, pageSize.value, articleStatusDto);
    const newArticles = res.data.data.data || [];
    total.value = res.data.data.total || 0;

    if (reset) {
      // 初次加载或筛选条件改变时
      articleList.value = newArticles;
    } else {
      // 无限滚动时加载下一页数据
      articleList.value = [...articleList.value, ...newArticles];
    }

    // 判断是否还有更多数据
    hasMore.value = articleList.value.length < total.value;

    // 更新页码
    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++;
    }

    // 计算总阅读量
    totalViews.value = articleList.value.reduce((sum, article) => sum + (article.viewCount || 0), 0);
  } catch (error) {
    ElMessage.error("获取文章列表失败");
    console.error("获取文章列表失败:", error);
  } finally {
    // 重置加载状态
    articleLoading.value = false;
    loadingMore.value = false;
  }
};

// 获取专栏列表
const fetchColumnList = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!columnHasMore.value || columnLoading.value || loadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      columnLoading.value = true;
    } else {
      loadingMore.value = true;
    }

    const userId = route.params.userId;
    const res = await getUserColumnList(columnCurrentPage.value, pageSize.value, parseInt(userId));
    const newColumns = res.data.data.data || [];
    columnTotal.value = res.data.data.total || 0;

    if (reset) {
      // 初次加载或筛选条件改变时
      columnList.value = newColumns;
    } else {
      // 无限滚动时加载下一页数据
      columnList.value = [...columnList.value, ...newColumns];
    }

    // 判断是否还有更多数据
    columnHasMore.value = columnList.value.length < columnTotal.value;

    // 更新页码
    if (columnHasMore.value && newColumns.length > 0) {
      columnCurrentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取专栏列表失败");
    console.error("获取专栏列表失败:", error);
  } finally {
    // 重置加载状态
    columnLoading.value = false;
    loadingMore.value = false;
  }
};

// 获取收藏夹列表
const fetchFavoriteList = async () => {
  try {
    favoriteLoading.value = true;
    const userId = route.params.userId;
    const res = await getFavoriteListByUserId(parseInt(userId));
    favoriteList.value = (res.data.data || []).map((favorite) => ({
      ...favorite,
      expanded: false, // 默认收起
      loading: false, // 加载状态
      articles: [], // 文章列表
    }));
  } catch (error) {
    ElMessage.error("获取收藏夹列表失败");
    console.error("获取收藏夹列表失败:", error);
  } finally {
    favoriteLoading.value = false;
  }
};

// 获取收藏夹中的文章列表
const fetchFavoriteArticleList = async (favorite) => {
  try {
    favorite.loading = true;
    const res = await getArticleListByFavoriteId(favorite.id);
    favorite.articles = res.data.data || [];
  } catch (error) {
    ElMessage.error("获取收藏夹文章失败");
    console.error("获取收藏夹文章失败:", error);
  } finally {
    favorite.loading = false;
  }
};

// 切换收藏夹展开状态
const toggleFavorite = async (favorite) => {
  favorite.expanded = !favorite.expanded;

  // 如果展开并且还没加载过文章，则加载文章
  if (favorite.expanded && favorite.articles.length === 0) {
    await fetchFavoriteArticleList(favorite);
  }
};

// 切换文章筛选标签
const handleTabChange = (tabName) => {
  activeTab.value = tabName;

  if (tabName === "article") {
    // 重置文章页码和列表，重新加载数据
    currentPage.value = 1;
    articleList.value = [];
    hasMore.value = true;
    fetchArticleList(true);
  } else if (tabName === "column") {
    // 重置专栏页码和列表，重新加载数据
    columnCurrentPage.value = 1;
    columnList.value = [];
    columnHasMore.value = true;
    fetchColumnList(true);
  } else if (tabName === "favorite") {
    // 加载收藏夹数据
    fetchFavoriteList();
  }
};

// 处理排序条件变化
const handleSortChange = (value) => {
  sortType.value = value;
  // 重置页码和文章列表，重新加载数据
  currentPage.value = 1;
  articleList.value = [];
  hasMore.value = true;
  fetchArticleList(true);
};

// 处理可见范围变化
const handleVisibilityChange = (value) => {
  visibilityType.value = value;
  // 重置页码和文章列表，重新加载数据
  currentPage.value = 1;
  articleList.value = [];
  hasMore.value = true;
  fetchArticleList(true);
};

// 关注用户
const handleFollow = async () => {
  try {
    followLoading.value = true;
    // TODO: 调用关注API
    isFollowed.value = !isFollowed.value;
    ElMessage.success(isFollowed.value ? "关注成功" : "取消关注成功");
  } catch (error) {
    ElMessage.error("操作失败，请重试");
  } finally {
    followLoading.value = false;
  }
};

// 私信用户
const handleMessage = () => {
  ElMessage.info("私信功能开发中...");
};

// 返回顶部 - 统一滚动到页面顶部
const scrollToTop = () => {
  // 所有标签页都滚动到页面顶部
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 跳转至文章详情页
const goToArticle = (articleId) => {
  const userId = route.params.userId;
  router.push(`/user/${userId}/article/${articleId}`);
};

// 跳转至专栏详情页
const goToColumn = (columnId) => {
  router.push(`/column/${columnId}`);
};

// 处理页面滚动事件 - 统一所有标签页的滚动监听
const handlePageScroll = () => {
  // 控制返回顶部按钮的显示/隐藏
  showBackToTop.value = window.scrollY > 200;

  // 收藏夹标签页不需要无限滚动
  if (activeTab.value === "favorite") {
    return;
  }

  // 当滚动到页面底部附近时加载更多
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = window.scrollY || document.documentElement.scrollTop;
  const clientHeight = window.innerHeight;

  if (scrollTop + clientHeight >= scrollHeight - 100) {
    if (activeTab.value === "article") {
      // 文章页面的无限滚动
      if (!articleLoading.value && !loadingMore.value && hasMore.value) {
        fetchArticleList();
      }
    } else if (activeTab.value === "column") {
      // 专栏页面的无限滚动
      if (!columnLoading.value && !loadingMore.value && columnHasMore.value) {
        fetchColumnList();
      }
    }
  }
};

// 监听路由参数变化
watch(
  () => route.params.userId,
  (newUserId) => {
    if (newUserId) {
      // 重置所有数据
      currentPage.value = 1;
      columnCurrentPage.value = 1;
      articleList.value = [];
      columnList.value = [];
      favoriteList.value = [];
      hasMore.value = true;
      columnHasMore.value = true;
      activeTab.value = "article"; // 重置到文章标签页

      fetchUserInfo();
      fetchArticleStatistics();
      fetchArticleList(true);
    }
  },
  { immediate: true }
);

// 组件挂载
onMounted(() => {
  fetchUserInfo();
  fetchArticleStatistics();
  fetchArticleList(true);

  // 添加页面滚动监听
  window.addEventListener("scroll", handlePageScroll);
});

// 组件卸载
onUnmounted(() => {
  // 移除页面滚动监听
  window.removeEventListener("scroll", handlePageScroll);
});
</script>

<style lang="scss" scoped>
// 全局变量
$primary-color: #409eff;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #dcdfe6;
$bg-color: #f5f7fa;

// 工具类
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 10px;
}

// 用户主页容器
.user-homepage {
  background: url("@/assets/img/homepage1.jpg") no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  min-height: calc(100vh - 48px);
  overflow-y: auto; // 改为auto，只在需要时显示滚动条

  // 内容区域
  .content-section {
    padding: 10px 0;

    .content-layout {
      display: grid;
      grid-template-columns: 1fr 300px;
      gap: 20px;
    }
  }

  // 主要内容区域
  .main-content {
    // 文章筛选标签
    .article-filters {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 10px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);

      :deep(.el-tabs__header) {
        margin: 0;
      }

      :deep(.el-tabs__nav-wrap::after) {
        display: none;
      }

      // 筛选控件整体样式
      .filter-controls {
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid var(--el-border-color-light);
        display: flex;
        align-items: center;
        gap: 14px;
        flex-wrap: wrap;

        // 可见范围筛选器样式
        .visibility-filter {
          .el-select {
            width: 100px;
          }
        }

        // 排序筛选器样式
        .sort-filters {
          .el-radio-group {
            display: flex;
            gap: 24px;

            .el-radio {
              margin-right: 0;
              font-size: 14px;
              color: var(--el-text-color-regular);

              &.is-checked {
                color: var(--el-color-primary);
              }
            }
          }
        }
      }
    }
  }

  // 右侧边栏
  .sidebar {
    // 侧边栏卡片
    .sidebar-card {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 2px solid var(--el-color-primary);
      }

      // 个人成就
      .achievements {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .achievement-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 12px;
          background-color: var(--el-bg-color-page);
          border-radius: 6px;

          .achievement-icon {
            color: var(--el-color-primary);
          }

          span {
            font-size: 14px;
            color: var(--el-text-color-regular);
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-homepage {
    .content-section {
      .content-layout {
        grid-template-columns: 1fr;
        gap: 20px;
      }
    }
  }
}

@media (max-width: 768px) {
  .user-homepage {
    font-size: 10px;
    // 移动端优化背景图片显示
    background-attachment: scroll; // 移动端使用scroll避免兼容性问题

    .content-section {
      padding: 0;
    }

    .main-content {
      .article-filters {
        padding: 0px 10px 10px 10px;
        margin-bottom: 5px;
        .filter-controls {
          margin-top: 5px;
          padding-top: 5px;
        }
      }
    }
    .sidebar {
      display: none;
    }
  }
}

// 返回顶部按钮 - 统一在父组件管理
.back-to-top {
  position: fixed;
  right: 20px;
  bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  font-size: 20px;
  backdrop-filter: blur(2px);
  background-color: color-mix(in srgb, var(--el-bg-color) 90%, transparent);
  border: 1px solid var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  z-index: 100;

  &:hover {
    background: var(--el-color-primary);
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  }

  .el-icon {
    font-size: 18px;
  }

  // 移动端样式调整
  @media (max-width: 768px) {
    width: 40px;
    height: 40px;
    right: 15px;
    bottom: 15px;

    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
