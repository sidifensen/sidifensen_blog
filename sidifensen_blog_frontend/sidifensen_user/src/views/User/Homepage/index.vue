<template>
  <div class="user-homepage">
    <!-- 用户信息区域 -->
    <UserProfileCard :user-info="userInfo" :user-loading="userLoading" :total-views="totalViews" :is-current-user="isCurrentUser" :is-followed="isFollowed" :follow-loading="followLoading" @follow="handleFollow" @message="handleMessage" />

    <!-- 内容区域 -->
    <div class="content-section">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-content">
            <!-- 标签页切换 -->
            <div class="tab-filters">
              <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane label="文章" name="article" />
                <el-tab-pane label="专栏" name="column" />
                <el-tab-pane label="收藏" name="favorite" />
                <el-tab-pane label="关注" name="follow" />
              </el-tabs>
            </div>

            <!-- 内容区域 - 固定高度以防止抖动 -->
            <div class="tab-content-container">
              <!-- 内容切换过渡动画 -->
              <transition name="tab-fade" mode="out-in">
                <!-- 文章列表 -->
                <ArticleList
                  v-if="activeTab === 'article'"
                  key="article"
                  :article-list="articleList"
                  :article-loading="articleLoading"
                  :loading-more="loadingMore"
                  :is-current-user="isCurrentUser"
                  :sort-type="sortType"
                  :visibility-type="visibilityType"
                  @article-click="goToArticle"
                  @sort-change="handleSortChange"
                  @visibility-change="handleVisibilityChange"
                />

                <!-- 专栏列表 -->
                <ColumnList v-else-if="activeTab === 'column'" key="column" :column-list="columnList" :column-loading="columnLoading" :loading-more="loadingMore" @column-click="goToColumn" />

                <!-- 收藏列表 -->
                <FavoriteList v-else-if="activeTab === 'favorite'" key="favorite" :favorite-list="favoriteList" :favorite-loading="favoriteLoading" :is-current-user="isCurrentUser" @toggle-favorite="toggleFavorite" @article-click="goToArticle" @update-favorite="handleUpdateFavorite" />

                <!-- 关注列表 -->
                <FollowList v-else-if="activeTab === 'follow'" key="follow" />
              </transition>
            </div>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 个人成就 -->
            <div class="sidebar-card">
              <h4 class="card-title">个人成就</h4>
              <div class="achievements">
                <div class="achievement-item" v-if="userInfo?.articleCount >= 10">
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
import { toggleFollow, isFollowing } from "@/api/follow";
import { getUserArticleList, getUserArticleStatisticsById } from "@/api/article";
import { getUserColumnList } from "@/api/column";
import { getFavoriteListByUserId, getArticleListByFavoriteId, updateFavorite } from "@/api/favorite";
import { useUserStore } from "@/stores/userStore";
import UserProfileCard from "./components/UserProfileCard.vue";
import ArticleList from "./components/ArticleList.vue";
import ColumnList from "./components/ColumnList.vue";
import FavoriteList from "./components/FavoriteList.vue";
import FollowList from "./components/FollowList.vue";

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

    // 如果不是当前用户且已登录，检查关注状态
    if (!isCurrentUser.value && userStore.user) {
      await checkUserFollowStatus();
    }
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
    // 更新总阅读量显示
    if (articleStatistics.value && articleStatistics.value.totalReadCount !== undefined) {
      totalViews.value = articleStatistics.value.totalReadCount;
    }
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

    // 使用后端统计的总阅读量
    if (articleStatistics.value && articleStatistics.value.totalReadCount !== undefined) {
      totalViews.value = articleStatistics.value.totalReadCount;
    }
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

// 处理更新收藏夹
const handleUpdateFavorite = async (formData) => {
  try {
    await updateFavorite(formData);

    // 更新本地收藏夹列表中对应的数据
    const favoriteIndex = favoriteList.value.findIndex((f) => f.id === formData.id);
    if (favoriteIndex !== -1) {
      favoriteList.value[favoriteIndex].name = formData.name;
      favoriteList.value[favoriteIndex].showStatus = formData.showStatus;
    }

    ElMessage.success("收藏夹更新成功");
  } catch (error) {
    console.error("更新收藏夹失败:", error);
    ElMessage.error("更新收藏夹失败");
    throw error; // 重新抛出错误，让子组件知道更新失败
  }
};

// 切换文章筛选标签
const handleTabChange = (tabName) => {
  activeTab.value = tabName;

  if (tabName === "article") {
    // 如果文章列表为空，重新加载数据
    if (articleList.value.length === 0) {
      currentPage.value = 1;
      hasMore.value = true;
      fetchArticleList(true);
    }
  } else if (tabName === "column") {
    // 如果专栏列表为空，重新加载数据
    if (columnList.value.length === 0) {
      columnCurrentPage.value = 1;
      columnHasMore.value = true;
      fetchColumnList(true);
    }
  } else if (tabName === "favorite") {
    // 如果收藏夹列表为空，加载收藏夹数据
    if (favoriteList.value.length === 0) {
      fetchFavoriteList();
    }
  } else if (tabName === "follow") {
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

// 检查用户关注状态
const checkUserFollowStatus = async () => {
  try {
    const followerId = userStore.user.id;
    const followedId = parseInt(route.params.userId);
    const res = await isFollowing(followerId, followedId);
    isFollowed.value = res.data.data;
  } catch (error) {
    console.error("检查关注状态失败:", error);
    isFollowed.value = false;
  }
};

// 关注用户
const handleFollow = async () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    followLoading.value = true;
    const followedId = parseInt(route.params.userId);
    const wasFollowed = isFollowed.value;

    // 调用切换关注状态接口
    await toggleFollow(followedId);

    // 切换状态
    isFollowed.value = !wasFollowed;

    // 显示操作结果
    ElMessage.success(isFollowed.value ? "关注成功" : "取消关注成功");

    // 更新用户粉丝数
    if (userInfo.value) {
      if (isFollowed.value) {
        userInfo.value.fansCount = (userInfo.value.fansCount || 0) + 1;
      } else {
        userInfo.value.fansCount = Math.max((userInfo.value.fansCount || 0) - 1, 0);
      }
    }
  } catch (error) {
    ElMessage.error("操作失败，请重试");
    console.error("关注操作失败:", error);
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
  const userId = route.params.userId;
  router.push(`/user/${userId}/column/${columnId}`);
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
      isFollowed.value = false; // 重置关注状态

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
    // 标签页切换
    .tab-filters {
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

    // 标签页内容容器 - 固定高度防止抖动
    .tab-content-container {
      min-height: 620px; // 设置固定最小高度，包含筛选栏高度
      position: relative;

      // 确保子组件占据完整高度
      > div {
        min-height: 100%;
      }
    }
  }
}

// 标签页切换过渡动画
.tab-fade-enter-active,
.tab-fade-leave-active {
  transition: opacity 0.3s ease;
}

.tab-fade-enter-from,
.tab-fade-leave-to {
  opacity: 0;
}

.tab-fade-enter-to,
.tab-fade-leave-from {
  opacity: 1;
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
    // background-position: center top; // 确保背景图片从顶部开始显示
    // background-size: cover; // 确保背景图片完全覆盖
    // min-height: 100vh; // 确保容器高度至少为视口高度

    .content-section {
      padding: 0;
    }

    .main-content {
      .tab-filters {
        padding: 0px 10px 10px 10px;
        margin-bottom: 5px;
      }

      // 移动端调整标签页内容容器高度
      .tab-content-container {
        min-height: 500px; // 移动端减少高度
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
