<template>
  <div class="user-homepage">
    <!-- 用户信息区域 -->
    <div class="user-profile-section">
      <div class="container">
        <!-- 用户基本信息卡片 -->
        <div class="user-profile-card">
          <el-skeleton :loading="userLoading" animated>
            <template #template>
              <div class="skeleton-profile">
                <el-skeleton-item variant="circle" style="width: 120px; height: 120px" />
                <div class="skeleton-info">
                  <el-skeleton-item variant="h3" style="width: 200px; margin: 16px 0" />
                  <el-skeleton-item variant="text" style="width: 300px" />
                  <el-skeleton-item variant="text" style="width: 250px; margin-top: 8px" />
                </div>
              </div>
            </template>

            <!-- 实际用户信息 -->
            <template #default>
              <div class="user-profile-content" v-if="userInfo">
                <!-- 用户头像和基本信息 -->
                <div class="user-basic-info">
                  <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar" />
                  <div class="user-details">
                    <h2 class="username">{{ userInfo.nickname }}</h2>
                    <div class="user-intro-container">
                      <p class="user-intro" :class="{ expanded: isIntroExpanded }">{{ userInfo.introduction || "这个人很懒，什么都没写~" }}</p>
                      <button v-if="userInfo.introduction && userInfo.introduction.length > 50" class="intro-expand-btn" @click="toggleIntroExpand">
                        <el-icon>
                          <ArrowDown v-if="!isIntroExpanded" />
                          <ArrowUp v-else />
                        </el-icon>
                      </button>
                    </div>
                    <div class="user-meta">
                      <span class="login-address" v-if="userInfo.loginAddress">IP属地：{{ userInfo.loginAddress }}</span>
                      <span class="join-time">加入时间：{{ userInfo.createTime }}</span>
                    </div>
                  </div>
                </div>

                <!-- 用户统计信息 -->
                <div class="user-stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ articleStatistics?.publishedCount || 0 }}</span>
                    <span class="stat-label">文章</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ userInfo.fansCount || 0 }}</span>
                    <span class="stat-label">粉丝</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ userInfo.followCount || 0 }}</span>
                    <span class="stat-label">关注</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ totalViews }}</span>
                    <span class="stat-label">阅读量</span>
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="user-actions" v-if="!isCurrentUser">
                  <el-button type="primary" :icon="Plus" @click="handleFollow" :loading="followLoading">
                    {{ isFollowed ? "已关注" : "关注" }}
                  </el-button>
                  <el-button :icon="Message" @click="handleMessage">私信</el-button>
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>
    </div>

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
            <div v-if="activeTab === 'article'" class="article-list-wrapper">
              <div class="article-list-section" ref="listContainer" @scroll="handleScroll">
                <div v-if="articleLoading" class="loading-container">
                  <el-skeleton animated :count="5">
                    <template #template>
                      <div class="article-skeleton">
                        <el-skeleton-item variant="image" style="width: 100px; height: 80px" />
                        <div class="skeleton-content">
                          <el-skeleton-item variant="h3" style="width: 70%" />
                          <el-skeleton-item variant="text" style="width: 100%" />
                          <el-skeleton-item variant="text" style="width: 60%" />
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </div>

                <div v-else-if="articleList.length === 0" class="empty-state">
                  <el-empty description="暂无文章" />
                </div>

                <div v-else class="article-list">
                  <div v-for="article in articleList" :key="article.id" class="article-item" @click="goToArticle(article.id)">
                    <!-- 文章封面 -->
                    <el-image :src="article.coverUrl || ''" class="article-cover">
                      <template #placeholder>
                        <div class="loading-text">加载中...</div>
                      </template>
                      <template #error>
                        <div class="error">
                          <el-icon>
                            <Picture />
                          </el-icon>
                        </div>
                      </template>
                    </el-image>

                    <!-- 文章内容 -->
                    <div class="article-content">
                      <h3 class="article-title">{{ article.title }}</h3>
                      <p class="article-description">{{ article.description }}</p>

                      <!-- 文章元信息 -->
                      <div class="article-meta">
                        <!-- 第一行：文章类型、审核状态、发布时间 -->
                        <div class="article-meta-primary">
                          <span class="article-type">{{ getArticleType(article.type) }}</span>
                          <span v-if="isCurrentUser && article.examineStatus !== 1" class="article-examine-status" :class="'status-' + article.examineStatus">
                            {{ getExamineStatus(article.examineStatus) }}
                          </span>
                          <span class="article-date">{{ article.createTime }}</span>
                        </div>
                        <!-- 第二行：统计数据 -->
                        <div class="article-meta-stats">
                          <span class="article-readCount">{{ article.readCount }} 阅读</span>
                          <span class="article-likes">{{ article.likeCount || 0 }} 点赞</span>
                          <span class="article-favorites">{{ article.collectCount || 0 }} 收藏</span>
                          <span class="article-comments">{{ article.commentCount }} 评论</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 加载更多指示器 -->
                  <div v-if="loadingMore" class="loading-more">
                    <div class="loading-spinner"></div>
                    <span>加载更多...</span>
                  </div>
                </div>
              </div>

              <!-- 返回顶部按钮 - 放在wrapper内部，但在滚动容器外部 -->
              <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
                <el-icon>
                  <ArrowUp />
                </el-icon>
              </div>
            </div>

            <!-- 专栏列表 -->
            <div v-if="activeTab === 'column'" class="column-list-wrapper">
              <div class="column-list-section" ref="listContainer" @scroll="handleScroll">
                <div v-if="columnLoading" class="loading-container">
                  <el-skeleton animated :count="3">
                    <template #template>
                      <div class="column-skeleton">
                        <el-skeleton-item variant="image" style="width: 120px; height: 90px" />
                        <div class="skeleton-content">
                          <el-skeleton-item variant="h3" style="width: 60%" />
                          <el-skeleton-item variant="text" style="width: 100%" />
                          <el-skeleton-item variant="text" style="width: 40%" />
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </div>

                <div v-else-if="columnList.length === 0" class="empty-state">
                  <el-empty description="暂无专栏" />
                </div>

                <div v-else class="column-list">
                  <div v-for="column in columnList" :key="column.id" class="column-item" @click="goToColumn(column.id)">
                    <!-- 专栏封面 -->
                    <el-image :src="column.coverUrl || ''" class="column-cover">
                      <template #placeholder>
                        <div class="loading-text">加载中...</div>
                      </template>
                      <template #error>
                        <div class="error">
                          <el-icon>
                            <Collection />
                          </el-icon>
                        </div>
                      </template>
                    </el-image>

                    <!-- 专栏内容 -->
                    <div class="column-content">
                      <h3 class="column-title">{{ column.name }}</h3>
                      <p class="column-description">{{ column.description || "暂无描述" }}</p>

                      <!-- 专栏元信息 -->
                      <div class="column-meta">
                        <!-- 第一行：统计数据 -->
                        <div class="column-meta-stats">
                          <span class="column-articles">
                            <el-icon><Document /></el-icon>
                            {{ column.articleCount || 0 }} 文章
                          </span>
                          <span class="column-focus">
                            <el-icon><Star /></el-icon>
                            {{ column.focusCount || 0 }} 关注
                          </span>
                        </div>
                        <!-- 第二行：创建时间 -->
                        <div class="column-meta-time">
                          <span class="column-date">创建于 {{ column.createTime }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 加载更多指示器 -->
                  <div v-if="loadingMore" class="loading-more">
                    <div class="loading-spinner"></div>
                    <span>加载更多...</span>
                  </div>
                </div>
              </div>

              <!-- 返回顶部按钮 - 放在wrapper内部，但在滚动容器外部 -->
              <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
                <el-icon>
                  <ArrowUp />
                </el-icon>
              </div>
            </div>

            <!-- 收藏列表 -->
            <div v-if="activeTab === 'favorite'" class="favorite-list-wrapper">
              <div class="favorite-list-section">
                <div class="empty-state">
                  <el-empty description="收藏功能开发中..." />
                </div>
              </div>

              <!-- 返回顶部按钮 - 放在wrapper内部，但在滚动容器外部 -->
              <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
                <el-icon>
                  <ArrowUp />
                </el-icon>
              </div>
            </div>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Plus, Message, Trophy, View, User, Star, StarFilled, ArrowDown, ArrowUp, Picture, Document, Collection } from "@element-plus/icons-vue";
import { getUserInfoById } from "@/api/user";
import { getUserArticleList, getUserArticleStatisticsById } from "@/api/article";
import { getUserColumnList } from "@/api/column";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const userLoading = ref(false); // 用户信息加载状态
const articleLoading = ref(false); // 文章列表加载状态
const columnLoading = ref(false); // 专栏列表加载状态
const loadingMore = ref(false); // 加载更多数据状态
const followLoading = ref(false); // 关注操作加载状态
const userInfo = ref(null); // 用户信息数据
const articleList = ref([]); // 文章列表数据
const columnList = ref([]); // 专栏列表数据
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
const isIntroExpanded = ref(false); // 个人介绍是否展开
const showBackToTop = ref(false); // 是否显示返回顶部按钮

// 每页数据量
const pageSize = ref(10);

// 文章列表容器引用
const listContainer = ref(null);

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
    // 收藏页面暂时无需处理，内容为空
    console.log("切换到收藏页面");
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

// 切换个人介绍展开状态
const toggleIntroExpand = () => {
  isIntroExpanded.value = !isIntroExpanded.value;
};

// 返回顶部 - 滚动到文章列表顶部
const scrollToTop = () => {
  // 滚动到文章列表容器顶部
  if (listContainer.value) {
    listContainer.value.scrollTo({ top: 0, behavior: "smooth" });
  }
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

// 获取文章类型
const getArticleType = (type) => {
  const typeMap = {
    0: "原创",
    1: "转载",
  };
  return typeMap[type] || "原创";
};

// 获取审核状态
const getExamineStatus = (status) => {
  const statusMap = {
    0: "待审核",
    1: "审核通过",
    2: "审核未通过",
  };
  return statusMap[status] || "审核通过";
};

// 处理滚动事件 - 自定义无限滚动
const handleScroll = () => {
  if (!listContainer.value || loadingMore.value) {
    return;
  }

  const container = listContainer.value;

  // 控制返回顶部按钮的显示/隐藏
  showBackToTop.value = container.scrollTop > 200;

  // 当滚动到底部附近时加载更多
  if (container.scrollTop + container.clientHeight >= container.scrollHeight - 100) {
    if (activeTab.value === "article") {
      // 文章页面的无限滚动
      if (!articleLoading.value && hasMore.value) {
        fetchArticleList();
      }
    } else if (activeTab.value === "column") {
      // 专栏页面的无限滚动
      if (!columnLoading.value && columnHasMore.value) {
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
  overflow-y: scroll;

  .user-profile-section {
    padding: 10px 0 0 0px;
    color: var(--el-text-color-primary);

    .user-profile-card {
      background: var(--el-border-color-lighter);
      backdrop-filter: blur(10px);
      border-radius: 8px;
      padding: 30px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);

      // 骨架屏样式
      .skeleton-profile {
        display: flex;
        align-items: center;
        gap: 24px;

        .skeleton-info {
          flex: 1;
        }
      }

      // 用户信息内容
      .user-profile-content {
        // 用户基本信息
        .user-basic-info {
          display: flex;
          align-items: center;
          gap: 24px;
          margin-bottom: 30px;

          .user-avatar {
            border: 4px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
          }

          .user-details {
            flex: 1;
            min-width: 0; // 允许flex子元素收缩到内容宽度以下
            overflow: hidden; // 防止内容溢出

            .username {
              font-size: 24px;
              font-weight: 700;
              margin: 0 0 8px 0;
              color: var(--el-text-color-primary);
            }

            // 个人介绍容器
            .user-intro-container {
              position: relative;
              margin: 0 0 12px 0;

              // 个人介绍文本
              .user-intro {
                font-size: 14px;
                margin: 0;
                color: var(--el-text-color-primary);
                line-height: 1;
                word-wrap: break-word;
                word-break: break-all;
                overflow-wrap: break-word;
                max-width: 100%;

                // 手机端：默认限制两行，并为箭头按钮预留空间
                @media (max-width: 768px) {
                  display: -webkit-box; // 使用webkit-box布局
                  -webkit-line-clamp: 2; // 限制两行
                  line-clamp: 2;
                  -webkit-box-orient: vertical; // 垂直方向排列
                  overflow: hidden;
                  text-overflow: ellipsis;
                  padding-right: 32px; // 为箭头按钮预留空间

                  // 展开状态：显示全部内容，移除右侧padding
                  &.expanded {
                    display: block;
                    -webkit-line-clamp: unset;
                    line-clamp: unset;
                    overflow: visible;
                    padding-right: 0;
                  }
                }
              }

              // 展开/收起按钮
              .intro-expand-btn {
                display: none;
                background: none;
                border: none;
                cursor: pointer;
                padding: 4px;
                margin-left: 8px;
                color: var(--el-color-primary);
                transition: all 0.3s ease;
                border-radius: 50%;

                &:hover {
                  background-color: var(--el-color-primary-light-9);
                  transform: scale(1.1);
                }

                .el-icon {
                  font-size: 14px;
                }

                // 只在手机端显示
                @media (max-width: 768px) {
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  position: absolute;
                  right: 4px;
                  top: 50%;
                  transform: translateY(-50%);
                  width: 20px;
                  height: 20px;
                  background-color: rgba(255, 255, 255, 0.9);
                  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                }
              }
            }

            .user-meta {
              font-size: 13px;
              color: var(--el-text-color-regular);
              display: flex;
              // flex-wrap: wrap;
              gap: 10px;

              .join-time,
              .login-address {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                white-space: nowrap;
              }

              // 手机端分两行显示
              @media (max-width: 768px) {
                font-size: 10px;
                // flex-direction: column;
              }
            }
          }
        }

        // 统计信息
        .user-stats {
          display: flex;
          justify-content: space-around;
          padding: 10px 0;
          border-top: 1px solid var(--el-border-color);
          border-bottom: 1px solid var(--el-border-color);
          margin-bottom: 10px;

          .stat-item {
            text-align: center;

            .stat-number {
              display: block;
              font-size: 24px;
              font-weight: 700;
              color: var(--el-text-color-primary);
              margin-bottom: 4px;
            }

            .stat-label {
              font-size: 14px;
              color: var(--el-text-color-primary);
            }
          }
        }

        // 操作按钮
        .user-actions {
          display: flex;
          gap: 16px;
          justify-content: center;

          .el-button {
            padding: 12px 24px;
            font-size: 14px;
            border-radius: 20px;
          }
        }
      }
    }
  }

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

    // 文章列表包装器
    .article-list-wrapper {
      position: relative; // 为返回顶部按钮提供定位参考

      // 返回顶部按钮样式 - 固定在文章列表容器的右下角，不随滚动移动
      .back-to-top {
        position: absolute;
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
      }
    }

    // 文章列表区域
    .article-list-section {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);
      max-height: 580px; // 设置最大高度以支持滚动
      overflow-y: auto; // 启用垂直滚动

      // 加载容器样式
      .loading-container {
        padding: 20px 0;
      }

      // 骨架屏样式
      .article-skeleton {
        display: flex;
        gap: 16px;
        padding: 20px 0;
        border-bottom: 1px solid var(--el-border-color-light);

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

      // 自定义滚动条样式，增加 border-radius
      &::-webkit-scrollbar {
        width: 8px;
        border-radius: 8px; // 增大滚动条圆角
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: var(--el-border-color);
        border-radius: 12px; // 滚动条滑块圆角更大
      }
      &::-webkit-scrollbar-track {
        background: transparent;
        border-radius: 12px;
      }

      // 文章列表
      .article-list {
        .article-item {
          display: flex;
          gap: 16px;
          padding: 20px 0;
          border-bottom: 1px solid var(--el-border-color-light);
          cursor: pointer;
          transition: all 0.3s ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: var(--el-bg-color-page);
            transform: translateX(4px);
          }

          // 文章封面
          .article-cover {
            width: 160px;
            height: 100px;
            border-radius: 6px;
            transition: transform 0.3s ease;

            &:hover {
              transform: scale(1.05);
            }

            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 16px;
              color: var(--el-text-color-primary);
              background-color: var(--el-bg-color-page);
            }

            // 错误占位图标样式
            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: var(--el-bg-color-page);

              .el-icon {
                font-size: 40px;
                color: var(--el-text-color-primary);
              }
            }
          }

          // 文章内容
          .article-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .article-title {
              font-size: 18px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              margin: 0 0 8px 0;
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            .article-description {
              font-size: 14px;
              color: var(--el-text-color-regular);
              margin: 0 0 12px 0;
              line-height: 1.5;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            // 文章元信息
            .article-meta {
              font-size: 13px;
              color: var(--el-text-color-secondary);

              // 第一行：文章类型、审核状态、发布时间
              .article-meta-primary {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 8px;
              }

              // 第二行：统计数据
              .article-meta-stats {
                display: flex;
                align-items: center;
                gap: 10px;
              }

              // 桌面端单行显示
              @media (min-width: 769px) {
                .article-meta-primary {
                  margin-bottom: 0;
                }

                .article-meta-primary,
                .article-meta-stats {
                  display: inline-flex;
                }

                .article-meta-stats {
                  margin-left: 10px;
                }

                .article-meta-stats::before {
                  content: "•";
                  margin-right: 10px;
                  color: var(--el-text-color-placeholder);
                }
              }

              .article-type {
                background-color: #f0f9ff;
                color: $primary-color;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 12px;
              }

              // 审核状态样式
              .article-examine-status {
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;

                // 待审核状态 - 橙色
                &.status-0 {
                  background-color: #fff7ed;
                  color: #ea580c;
                  border: 1px solid #fed7aa;
                }

                // 审核未通过状态 - 红色
                &.status-2 {
                  background-color: #fef2f2;
                  color: #dc2626;
                  border: 1px solid #fecaca;
                }
              }

              .article-date,
              .article-readCount,
              .article-likes,
              .article-favorites,
              .article-comments {
                display: flex;
                align-items: center;
                gap: 4px;
              }
            }
          }
        }
      }

      // 空状态
      .empty-state {
        padding: 60px 0;
        text-align: center;
      }

      // 加载更多指示器
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 30px;
        color: var(--el-text-color-primary);

        .loading-spinner {
          margin-right: 10px;
        }
      }
    }

    // 专栏列表包装器
    .column-list-wrapper {
      position: relative; // 为返回顶部按钮提供定位参考

      // 返回顶部按钮样式 - 固定在专栏列表容器的右下角，不随滚动移动
      .back-to-top {
        position: absolute;
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
      }
    }

    // 专栏列表区域
    .column-list-section {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);
      max-height: 650px; // 设置最大高度以支持滚动
      overflow-y: auto; // 启用垂直滚动

      // 加载容器样式
      .loading-container {
        padding: 20px 0;
      }

      // 骨架屏样式
      .column-skeleton {
        display: flex;
        gap: 16px;
        padding: 20px 0;
        border-bottom: 1px solid var(--el-border-color-light);

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

      // 自定义滚动条样式，增加 border-radius
      &::-webkit-scrollbar {
        width: 8px;
        border-radius: 8px; // 增大滚动条圆角
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: var(--el-border-color);
        border-radius: 12px; // 滚动条滑块圆角更大
      }
      &::-webkit-scrollbar-track {
        background: transparent;
        border-radius: 12px;
      }

      // 专栏列表
      .column-list {
        .column-item {
          display: flex;
          gap: 16px;
          padding: 20px 0;
          border-bottom: 1px solid var(--el-border-color-light);
          cursor: pointer;
          transition: all 0.3s ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: var(--el-bg-color-page);
            transform: translateX(4px);
          }

          // 专栏封面
          .column-cover {
            width: 120px;
            height: 90px;
            border-radius: 8px;
            transition: transform 0.3s ease;

            &:hover {
              transform: scale(1.05);
            }

            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 12px;
              color: var(--el-text-color-regular);
              background-color: var(--el-bg-color-page);
            }

            // 错误占位图标样式
            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: var(--el-bg-color-page);

              .el-icon {
                font-size: 24px;
                color: var(--el-text-color-placeholder);
              }
            }
          }

          // 专栏内容
          .column-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .column-title {
              font-size: 18px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              margin: 0 0 8px 0;
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 1;
              line-clamp: 1;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            .column-description {
              font-size: 14px;
              color: var(--el-text-color-regular);
              margin: 0 0 12px 0;
              line-height: 1.5;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            // 专栏元信息
            .column-meta {
              display: flex;
              align-items: center;
              font-size: 13px;
              color: var(--el-text-color-secondary);

              // 第一行：统计数据
              .column-meta-stats {
                gap: 16px;
                margin-bottom: 8px;

                .column-articles,
                .column-focus {
                  display: flex;
                  align-items: center;
                  gap: 4px;

                  .el-icon {
                    font-size: 14px;
                  }
                }
              }

              // 第二行：创建时间
              .column-meta-time {
                .column-date {
                  color: var(--el-text-color-placeholder);
                }
              }

              // 桌面端单行显示
              @media (min-width: 769px) {
                .column-meta-stats {
                  margin-bottom: 0;
                }

                .column-meta-stats,
                .column-meta-time {
                  display: inline-flex;
                }

                .column-meta-time {
                  margin-left: 16px;
                }

                .column-meta-time::before {
                  content: "•";
                  margin-right: 8px;
                  color: var(--el-text-color-placeholder);
                }
              }
            }
          }
        }
      }

      // 空状态
      .empty-state {
        padding: 60px 0;
        text-align: center;
      }

      // 加载更多指示器
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 30px;
        color: var(--el-text-color-primary);

        .loading-spinner {
          margin-right: 10px;
        }
      }
    }

    // 收藏列表包装器
    .favorite-list-wrapper {
      position: relative; // 为返回顶部按钮提供定位参考

      // 返回顶部按钮样式 - 固定在收藏列表容器的右下角，不随滚动移动
      .back-to-top {
        position: absolute;
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
      }
    }

    // 收藏列表区域
    .favorite-list-section {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);
      min-height: 400px; // 设置最小高度

      // 空状态
      .empty-state {
        padding: 60px 0;
        text-align: center;
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

    .user-profile-section {
      .user-profile-card {
        .user-profile-content {
          .user-basic-info {
            // flex-direction: column;
            // text-align: center;
            gap: 5px;
          }

          .user-stats {
            flex-wrap: wrap;
            gap: 20px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-homepage {
    font-size: 10px;
    // 移动端优化背景图片显示
    background-attachment: scroll; // 移动端使用scroll避免兼容性问题

    .user-profile-section {
      padding: 5px 0;

      .user-profile-card {
        padding: 10px;
        .user-profile-content {
          .user-basic-info {
            margin-bottom: 5px;
            .user-avatar {
              width: 70px;
              height: 70px;
            }
          }
        }
      }
    }

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
      .article-list-wrapper {
        // 移动端返回顶部按钮调整
        .back-to-top {
          width: 40px;
          height: 40px;
          right: 15px;
          bottom: 15px;

          .el-icon {
            font-size: 16px;
          }
        }
      }

      .article-list-section {
        .article-list {
          .article-item {
            flex-direction: column;
            gap: 12px;

            .article-cover {
              width: 100%;
              height: 180px;
            }
          }
        }
      }

      // 专栏列表移动端样式
      .column-list-wrapper {
        // 移动端返回顶部按钮调整
        .back-to-top {
          width: 40px;
          height: 40px;
          right: 15px;
          bottom: 15px;

          .el-icon {
            font-size: 16px;
          }
        }
      }

      .column-list-section {
        .column-list {
          .column-item {
            flex-direction: column;
            gap: 12px;

            .column-cover {
              width: 100%;
              height: 180px;
            }
          }
        }
      }

      // 收藏列表移动端样式
      .favorite-list-wrapper {
        // 移动端返回顶部按钮调整
        .back-to-top {
          width: 40px;
          height: 40px;
          right: 15px;
          bottom: 15px;

          .el-icon {
            font-size: 16px;
          }
        }
      }
    }
    .sidebar {
      display: none;
    }
  }

  // 自定义加载指示器样式
  .loading-spinner {
    width: 20px;
    height: 20px;
    border: 2px solid #f3f3f3;
    border-top: 2px solid #409eff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    display: inline-block;
    vertical-align: middle;
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
}
</style>
