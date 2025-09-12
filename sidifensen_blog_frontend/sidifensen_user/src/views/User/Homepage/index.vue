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
                    <p class="user-intro">{{ userInfo.introduction || "这个人很懒，什么都没写~" }}</p>
                    <div class="user-meta">
                      <span class="join-time">加入时间：{{ userInfo.createTime }}</span>
                    </div>
                  </div>
                </div>

                <!-- 用户统计信息 -->
                <div class="user-stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ userInfo.articleCount || 0 }}</span>
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
                <el-tab-pane label="全部文章" name="all" />
                <el-tab-pane label="原创" name="original" />
                <el-tab-pane label="转载" name="repost" />
              </el-tabs>
            </div>

            <!-- 文章列表 -->
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
                  <el-image :src="article.coverUrl" class="article-cover">
                    <template #placeholder>
                      <div class="loading-text">加载中...</div>
                    </template>
                    <template #error>
                      <div class="error">
                        <el-icon>
                          <Picture />
                        </el-icon>
                      </div> </template
                  ></el-image>

                  <!-- 文章内容 -->
                  <div class="article-content">
                    <h3 class="article-title">{{ article.title }}</h3>
                    <p class="article-summary">{{ article.summary }}</p>

                    <!-- 文章元信息 -->
                    <div class="article-meta">
                      <span class="article-type">{{ getArticleType(article.type) }}</span>
                      <span class="article-date">{{ article.createTime }}</span>
                      <span class="article-views">{{ article.viewCount }} 阅读</span>
                      <span class="article-comments">{{ article.commentCount }} 评论</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Plus, Message, Trophy, View, User } from "@element-plus/icons-vue";
import { getUserInfo } from "@/api/user";
import { getUserArticleList } from "@/api/article";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const userLoading = ref(false);
const articleLoading = ref(false);
const loadingMore = ref(false);
const followLoading = ref(false);
const userInfo = ref(null);
const articleList = ref([]);
const total = ref(0);
const totalViews = ref(0);
const activeTab = ref("all");
const isFollowed = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);

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
    const res = await getUserInfo(userId);
    userInfo.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
    console.error("获取用户信息失败:", error);
  } finally {
    userLoading.value = false;
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
      type: activeTab.value === "all" ? null : activeTab.value === "original" ? 0 : 1,
    };

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

// 切换文章筛选标签
const handleTabChange = (tabName) => {
  activeTab.value = tabName;
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

// 跳转至文章详情页
const goToArticle = (articleId) => {
  const userId = route.params.userId;
  router.push(`/user/${userId}/article/${articleId}`);
};

// 获取文章类型
const getArticleType = (type) => {
  const typeMap = {
    0: "原创",
    1: "转载",
    2: "翻译",
  };
  return typeMap[type] || "原创";
};

// 处理滚动事件 - 自定义无限滚动
const handleScroll = () => {
  // 如果没有列表容器或正在加载中或加载更多中或没有更多内容时,不用加载下一页了
  if (!listContainer.value || articleLoading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const container = listContainer.value;
  // 当滚动到底部附近时加载更多
  if (container.scrollTop + container.clientHeight >= container.scrollHeight - 100) {
    fetchArticleList();
  }
};

// 监听路由参数变化
watch(
  () => route.params.userId,
  (newUserId) => {
    if (newUserId) {
      // 重置数据
      currentPage.value = 1;
      articleList.value = [];
      hasMore.value = true;
      fetchUserInfo();
      fetchArticleList(true);
    }
  },
  { immediate: true }
);

// 组件挂载
onMounted(() => {
  fetchUserInfo();
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
  padding: 0 20px;
}

// 用户主页容器
.user-homepage {
  min-height: 100%;
  height: 100%;
  background-color: $bg-color;

  // 用户信息区域
  .user-profile-section {
    // background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    background: var(--el-bg-color-page);
    padding: 40px 0;
    color: var(--el-text-color-primary);

    .user-profile-card {
      background: var(--el-border-color-lighter);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 30px;
      border: 1px solid var(--el-border-color-light);
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

            .username {
              font-size: 28px;
              font-weight: 700;
              margin: 0 0 8px 0;
              color: var(--el-text-color-primary);
            }

            .user-intro {
              font-size: 16px;
              margin: 0 0 12px 0;
              color: var(--el-text-color-primary);
              line-height: 1.5;
            }

            .user-meta {
              font-size: 14px;
              color: var(--el-text-color-primary);

              .join-time {
                display: inline-flex;
                align-items: center;
                gap: 4px;
              }
            }
          }
        }

        // 统计信息
        .user-stats {
          display: flex;
          justify-content: space-around;
          margin-bottom: 30px;
          padding: 20px 0;
          border-top: 1px solid var(--el-border-color-light);
          border-bottom: 1px solid var(--el-border-color-light);

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
    padding: 40px 0;
    background: var(--el-bg-color-page);

    .content-layout {
      display: grid;
      grid-template-columns: 1fr 300px;
      gap: 30px;
    }
  }

  // 主要内容区域
  .main-content {
    // 文章筛选标签
    .article-filters {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      box-shadow: 0 2px 12px var(--el-border-color-light);

      :deep(.el-tabs__header) {
        margin: 0;
      }

      :deep(.el-tabs__nav-wrap::after) {
        display: none;
      }
    }

    // 文章列表区域
    .article-list-section {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      max-height: 800px; // 设置最大高度以支持滚动
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
            width: 120px;
            height: 90px;
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

            .article-summary {
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
              display: flex;
              align-items: center;
              gap: 16px;
              font-size: 13px;
              color: var(--el-text-color-secondary);

              .article-type {
                background-color: #f0f9ff;
                color: $primary-color;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 12px;
              }

              .article-date,
              .article-views,
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
  }

  // 右侧边栏
  .sidebar {
    // 侧边栏卡片
    .sidebar-card {
      background: var(--el-bg-color-page);
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
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
            flex-direction: column;
            text-align: center;
            gap: 16px;
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
    .user-profile-section {
      padding: 20px 0;

      .user-profile-card {
        padding: 20px;
      }
    }

    .content-section {
      padding: 20px 0;
    }

    .main-content {
      .article-list-section {
        .article-list {
          .article-item {
            flex-direction: column;
            gap: 12px;

            .article-cover {
              width: 100%;
              height: 200px;
            }
          }
        }
      }
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
