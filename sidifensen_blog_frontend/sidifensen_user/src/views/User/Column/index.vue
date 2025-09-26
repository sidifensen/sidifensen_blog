<template>
  <div class="column-page">
    <div class="container">
      <div class="page-layout">
        <!-- 左侧作者信息 -->
        <div class="left-sidebar">
          <!-- 作者信息卡片 -->
          <div class="author-card">
            <el-skeleton :loading="!authorInfo" animated>
              <template #template>
                <div class="author-skeleton">
                  <el-skeleton-item variant="circle" style="width: 80px; height: 80px; margin: 0 auto 16px" />
                  <el-skeleton-item variant="h3" style="width: 60%; margin: 0 auto 8px" />
                  <el-skeleton-item variant="text" style="width: 80%; margin: 0 auto 4px" />
                  <el-skeleton-item variant="text" style="width: 70%; margin: 0 auto" />
                </div>
              </template>

              <template #default>
                <div class="author-info" v-if="authorInfo">
                  <el-avatar :size="80" :src="authorInfo.avatar" class="author-avatar" @click="goToAuthorPage" />
                  <h3 class="author-name" @click="goToAuthorPage">{{ authorInfo.nickname }}</h3>
                  <p class="author-intro">{{ authorInfo.introduction || "这个人很懒，什么都没写~" }}</p>

                  <!-- 作者统计信息 -->
                  <div class="author-stats">
                    <div class="stat-item">
                      <span class="stat-number">{{ authorInfo.articleCount || 0 }}</span>
                      <span class="stat-label">文章</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-number">{{ authorInfo.fansCount || 0 }}</span>
                      <span class="stat-label">粉丝</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-number">{{ authorInfo.followCount || 0 }}</span>
                      <span class="stat-label">关注</span>
                    </div>
                  </div>

                  <!-- 操作按钮 -->
                  <div class="author-actions" v-if="!isCurrentUser">
                    <el-button :type="isFollowed ? 'default' : 'primary'" :icon="isFollowed ? null : Plus" @click="handleFollow" :loading="followLoading" block :class="{ 'followed-btn': isFollowed }" @mouseenter="handleFollowButtonHover(true)" @mouseleave="handleFollowButtonHover(false)">
                      {{ followButtonText }}
                    </el-button>
                    <el-button :icon="Message" @click="handleMessage" block> 私信 </el-button>
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 作者其他专栏 -->
          <div class="sidebar-card" v-if="otherColumns.length > 0">
            <h4 class="card-title">作者其他专栏</h4>
            <div class="other-columns">
              <div v-for="column in otherColumns" :key="column.id" class="other-column-item" @click="goToColumn(column.id)">
                <el-image :src="column.coverUrl || ''" class="other-column-cover">
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="other-column-info">
                  <h5 class="other-column-title">{{ column.name }}</h5>
                  <span class="other-column-count">{{ column.articleCount || 0 }} 篇</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间主要内容 -->
        <div class="main-content">
          <!-- 专栏信息区域 -->
          <div class="column-header">
            <el-skeleton :loading="columnLoading" animated>
              <template #template>
                <div class="column-skeleton">
                  <el-skeleton-item variant="image" style="width: 150px; height: 100px; border-radius: 8px" />
                  <div class="skeleton-info">
                    <el-skeleton-item variant="h1" style="width: 300px; margin-bottom: 12px" />
                    <el-skeleton-item variant="text" style="width: 400px; margin-bottom: 8px" />
                    <el-skeleton-item variant="text" style="width: 300px" />
                  </div>
                </div>
              </template>

              <template #default>
                <div class="column-info" v-if="columnInfo">
                  <el-image :src="columnInfo.coverUrl || ''" class="column-cover">
                    <template #placeholder>
                      <div class="loading-text">加载中...</div>
                    </template>
                    <template #error>
                      <div class="error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>

                  <div class="column-details">
                    <h1 class="column-title">{{ columnInfo.name }}</h1>
                    <div class="column-description-container">
                      <p class="column-description" :class="{ expanded: isDescExpanded }">
                        {{ columnInfo.description || "暂无描述" }}
                      </p>
                      <button v-if="columnInfo.description && columnInfo.description.length > 80" class="desc-expand-btn" @click="toggleDescExpand">
                        {{ isDescExpanded ? "收起" : "展开" }}
                        <el-icon>
                          <ArrowDown v-if="!isDescExpanded" />
                          <ArrowUp v-else />
                        </el-icon>
                      </button>
                    </div>

                    <!-- 专栏统计信息 -->
                    <div class="column-stats">
                      <span class="stat-item">
                        <el-icon><Document /></el-icon>
                        {{ columnInfo.articleCount || 0 }} 篇文章
                      </span>
                      <span class="stat-item">
                        <el-icon><Star /></el-icon>
                        {{ formatNumber(columnInfo.focusCount || 0) }} 关注
                      </span>
                      <span class="stat-item">
                        <el-icon><Calendar /></el-icon>
                        {{ columnInfo.createTime }}
                      </span>
                    </div>
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 文章列表 -->
          <div class="article-section">
            <!-- 文章列表标题和排序 -->
            <div class="article-header">
              <h2>专栏文章 ({{ articleList.length }})</h2>
              <div class="sort-controls">
                <el-radio-group v-model="sortType" @change="handleSortChange" size="small">
                  <el-radio value="sort">按顺序</el-radio>
                  <el-radio value="time">按时间</el-radio>
                </el-radio-group>
              </div>
            </div>

            <!-- 文章列表内容 -->
            <div class="article-list-container">
              <div v-if="articleLoading" class="loading-container">
                <el-skeleton animated :count="5">
                  <template #template>
                    <div class="article-skeleton">
                      <div class="skeleton-index"></div>
                      <el-skeleton-item variant="image" style="width: 100px; height: 75px; border-radius: 6px" />
                      <div class="skeleton-content">
                        <el-skeleton-item variant="h3" style="width: 70%; margin-bottom: 8px" />
                        <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 4px" />
                        <el-skeleton-item variant="text" style="width: 60%" />
                      </div>
                    </div>
                  </template>
                </el-skeleton>
              </div>

              <div v-else-if="articleList.length === 0" class="empty-state">
                <el-empty description="专栏暂无文章" />
              </div>

              <div v-else class="article-list">
                <div v-for="(article, index) in articleList" :key="article.id" class="article-item" @click="goToArticle(article.id)">
                  <!-- 序号 -->
                  <div class="article-index">{{ index + 1 }}</div>

                  <!-- 文章封面 -->
                  <el-image :src="article.coverUrl || ''" class="article-cover">
                    <template #placeholder>
                      <div class="loading-text">加载中...</div>
                    </template>
                    <template #error>
                      <div class="error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>

                  <!-- 文章内容 -->
                  <div class="article-content">
                    <h3 class="article-title">{{ article.title }}</h3>
                    <p class="article-description">{{ article.description }}</p>

                    <!-- 文章元信息 -->
                    <div class="article-meta">
                      <span class="article-date">{{ article.createTime }}</span>
                      <span class="article-readCount">
                        <el-icon><View /></el-icon>
                        {{ formatNumber(article.readCount || 0) }}
                      </span>
                      <span class="article-likes">
                        <el-icon><Star /></el-icon>
                        {{ formatNumber(article.likeCount || 0) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧边栏 -->
        <div class="right-sidebar">
          <!-- 专栏信息卡片 -->
          <div class="sidebar-card" v-if="columnInfo">
            <h4 class="card-title">专栏信息</h4>
            <div class="column-sidebar-info">
              <div class="info-item">
                <span class="info-label">文章数量</span>
                <span class="info-value">{{ columnInfo.articleCount || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">关注数</span>
                <span class="info-value">{{ formatNumber(columnInfo.focusCount || 0) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ columnInfo.createTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ columnInfo.updateTime }}</span>
              </div>
            </div>
          </div>

          <!-- 相关推荐 -->
          <div class="sidebar-card">
            <h4 class="card-title">相关推荐</h4>
            <div class="recommendations">
              <div class="recommendation-item">
                <el-icon><Star /></el-icon>
                <span>热门专栏推荐</span>
              </div>
              <div class="recommendation-item">
                <el-icon><TrendCharts /></el-icon>
                <span>技术趋势分析</span>
              </div>
              <div class="recommendation-item">
                <el-icon><Reading /></el-icon>
                <span>学习路径指南</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 返回顶部按钮 -->
    <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
      <el-icon><ArrowUp /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Plus, Message, ArrowDown, ArrowUp, Picture, Document, View, Calendar, Star, TrendCharts, Reading } from "@element-plus/icons-vue";
import { getColumnDetail, getUserColumnList } from "@/api/column";
import { getUserInfoById } from "@/api/user";
import { toggleFollow, isFollowing } from "@/api/follow";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const columnLoading = ref(false); // 专栏信息加载状态
const articleLoading = ref(false); // 文章列表加载状态
const followLoading = ref(false); // 关注操作加载状态

const columnInfo = ref(null); // 专栏信息
const authorInfo = ref(null); // 作者信息
const articleList = ref([]); // 文章列表
const otherColumns = ref([]); // 作者其他专栏

const sortType = ref("sort"); // 排序类型：sort-按顺序，time-按时间

const isDescExpanded = ref(false); // 专栏描述是否展开
const showBackToTop = ref(false); // 是否显示返回顶部按钮
const isFollowed = ref(false); // 是否已关注作者
const isHoveringFollowButton = ref(false); // 关注按钮悬停状态

// 计算属性
const isCurrentUser = computed(() => {
  return userStore.user?.id === parseInt(route.params.userId);
});

// 计算关注按钮显示的文字
const followButtonText = computed(() => {
  if (!isFollowed.value) {
    return "关注";
  }
  return isHoveringFollowButton.value ? "取消关注" : "已关注";
});

// 格式化数字显示
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + "w";
  }
  return num.toString();
};

// 切换专栏描述展开/收起
const toggleDescExpand = () => {
  isDescExpanded.value = !isDescExpanded.value;
};

// 获取专栏详情信息
const fetchColumnDetail = async () => {
  try {
    columnLoading.value = true;
    const columnId = route.params.columnId;
    const res = await getColumnDetail(columnId);
    columnInfo.value = res.data.data;
    articleList.value = res.data.data.articles || [];
  } catch (error) {
    ElMessage.error("获取专栏信息失败");
    console.error("获取专栏信息失败:", error);
  } finally {
    columnLoading.value = false;
  }
};

// 获取作者信息
const fetchAuthorInfo = async () => {
  try {
    const userId = route.params.userId;
    const res = await getUserInfoById(userId);
    authorInfo.value = res.data.data;

    // 如果不是当前用户且已登录，检查关注状态
    if (!isCurrentUser.value && userStore.user) {
      await checkUserFollowStatus();
    }
  } catch (error) {
    ElMessage.error("获取作者信息失败");
    console.error("获取作者信息失败:", error);
  }
};

// 获取作者其他专栏
const fetchOtherColumns = async () => {
  try {
    const userId = route.params.userId;
    const currentColumnId = route.params.columnId;
    const res = await getUserColumnList(1, 5, userId);
    const columns = res.data.data.data;
    // 过滤掉当前专栏
    otherColumns.value = columns.filter((column) => column.id !== parseInt(currentColumnId));
  } catch (error) {
    console.error("获取其他专栏失败:", error);
    otherColumns.value = []; // 设置默认值
  }
};

// 处理排序变化
const handleSortChange = (value) => {
  sortType.value = value;
  // 根据排序类型重新排序文章列表
  if (value === "time") {
    articleList.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
  } else {
    // 按顺序排序（恢复原始顺序）
    fetchColumnDetail();
  }
};

// 处理滚动事件
const handleScroll = () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
  showBackToTop.value = scrollTop > 300;
};

// 返回顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 跳转到文章详情页
const goToArticle = (articleId) => {
  const userId = route.params.userId;
  router.push(`/user/${userId}/article/${articleId}`);
};

// 跳转到作者主页
const goToAuthorPage = () => {
  const userId = route.params.userId;
  router.push(`/user/${userId}`);
};

// 跳转到其他专栏
const goToColumn = (columnId) => {
  const userId = route.params.userId;
  router.push(`/user/${userId}/column/${columnId}`);
};

// 检查用户关注状态
const checkUserFollowStatus = async () => {
  try {
    const followerId = userStore.user.id;
    const followedId = parseInt(route.params.userId);
    const res = await isFollowing(followerId, followedId);
    isFollowed.value = res.data.data; // 后端返回 Boolean 值
  } catch (error) {
    console.error("检查关注状态失败:", error);
    isFollowed.value = false;
  }
};

// 关注/取消关注作者
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

    // 更新作者粉丝数
    if (authorInfo.value) {
      if (isFollowed.value) {
        authorInfo.value.fansCount = (authorInfo.value.fansCount || 0) + 1;
      } else {
        authorInfo.value.fansCount = Math.max((authorInfo.value.fansCount || 0) - 1, 0);
      }
    }
  } catch (error) {
    ElMessage.error("操作失败");
    console.error("关注操作失败:", error);
  } finally {
    followLoading.value = false;
  }
};

// 发送私信
const handleMessage = () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }
  ElMessage.info("私信功能开发中");
};

// 处理关注按钮悬停状态
const handleFollowButtonHover = (isHovering) => {
  isHoveringFollowButton.value = isHovering;
};

// 监听路由参数变化
watch(
  () => [route.params.userId, route.params.columnId],
  ([newUserId, newColumnId]) => {
    if (newUserId && newColumnId) {
      // 重置数据
      columnInfo.value = null;
      authorInfo.value = null;
      articleList.value = [];
      otherColumns.value = [];
      isDescExpanded.value = false;
      showBackToTop.value = false;
      isFollowed.value = false; // 重置关注状态

      // 获取数据
      fetchColumnDetail();
      fetchAuthorInfo();
      fetchOtherColumns();
    }
  },
  { immediate: true }
);

// 组件挂载
onMounted(() => {
  // 监听页面滚动
  window.addEventListener("scroll", handleScroll);
  // 数据获取由watch监听器处理，这里不需要重复调用
});

// 组件卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 工具类
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

// 专栏页面容器
.column-page {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
  padding: 20px 0;

  // 页面布局 - 三栏结构
  .page-layout {
    display: grid;
    grid-template-columns: 280px 1fr 280px;
    gap: 20px;
    align-items: flex-start;

    // 响应式布局
    @media (max-width: 1200px) {
      grid-template-columns: 260px 1fr 260px;
      gap: 15px;
    }

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
      gap: 20px;
    }
  }

  // 左侧边栏 - 作者信息
  .left-sidebar {
    // 作者信息卡片
    .author-card {
      background: var(--el-bg-color);
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      margin-bottom: 20px;
      text-align: center;

      .author-skeleton {
        text-align: center;
      }

      .author-info {
        .author-avatar {
          cursor: pointer;
          transition: transform 0.3s ease;
          border: 3px solid var(--el-border-color-light);

          &:hover {
            transform: scale(1.05);
          }
        }

        .author-name {
          font-size: 18px;
          font-weight: 600;
          margin: 16px 0 8px 0;
          color: var(--el-text-color-primary);
          cursor: pointer;
          transition: color 0.3s ease;

          &:hover {
            color: var(--el-color-primary);
          }
        }

        .author-intro {
          font-size: 14px;
          color: var(--el-text-color-regular);
          margin: 0 0 20px 0;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        // 作者统计信息
        .author-stats {
          display: flex;
          justify-content: space-around;
          margin-bottom: 20px;
          padding: 16px 0;
          border-top: 1px solid var(--el-border-color-light);
          border-bottom: 1px solid var(--el-border-color-light);

          .stat-item {
            text-align: center;

            .stat-number {
              display: block;
              font-size: 20px;
              font-weight: 700;
              color: var(--el-text-color-primary);
              margin-bottom: 4px;
            }

            .stat-label {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }
          }
        }

        .author-actions {
          display: flex;
          flex-direction: column;
          gap: 8px;
          :deep(.el-button) {
            margin-left: 0;
          }

          // 已关注按钮样式
          :deep(.followed-btn) {
            border-color: var(--el-color-success);
            color: var(--el-color-success);

            &:hover {
              background-color: var(--el-color-danger);
              border-color: var(--el-color-danger);
              color: white;
            }
          }
        }
      }
    }

    // 侧边栏卡片通用样式
    .sidebar-card {
      background: var(--el-bg-color);
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      margin-bottom: 20px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 2px solid var(--el-color-primary);
      }

      // 其他专栏列表
      .other-columns {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .other-column-item {
          display: flex;
          gap: 12px;
          padding: 8px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background-color: var(--el-bg-color-page);
            transform: translateX(4px);
          }

          .other-column-cover {
            width: 50px;
            height: 35px;
            border-radius: 6px;
            flex-shrink: 0;

            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: var(--el-bg-color-page);

              .el-icon {
                font-size: 14px;
                color: var(--el-text-color-placeholder);
              }
            }
          }

          .other-column-info {
            flex: 1;
            min-width: 0;

            .other-column-title {
              font-size: 14px;
              font-weight: 500;
              color: var(--el-text-color-primary);
              margin: 0 0 4px 0;
              display: -webkit-box;
              -webkit-line-clamp: 1;
              line-clamp: 1;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            .other-column-count {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }

    // 响应式：移动端隐藏左侧边栏
    @media (max-width: 992px) {
      display: none;
    }
  }

  // 中间主要内容
  .main-content {
    // 专栏信息区域
    .column-header {
      background: var(--el-bg-color);
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      margin-bottom: 20px;

      .column-skeleton {
        display: flex;
        gap: 20px;
        align-items: flex-start;

        .skeleton-info {
          flex: 1;
        }
      }

      .column-info {
        display: flex;
        gap: 20px;
        align-items: flex-start;

        @media (max-width: 768px) {
          flex-direction: column;
        }

        .column-cover {
          width: 150px;
          height: 100px;
          border-radius: 8px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          flex-shrink: 0;

          @media (max-width: 768px) {
            width: 100%;
            height: 100%;
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

        .column-details {
          flex: 1;
          min-width: 0;

          .column-title {
            font-size: 24px;
            font-weight: 700;
            margin: 0 0 12px 0;
            color: var(--el-text-color-primary);
            line-height: 1.3;
          }

          .column-description-container {
            position: relative;
            margin-bottom: 16px;

            .column-description {
              font-size: 14px;
              color: var(--el-text-color-regular);
              line-height: 1.6;
              margin: 0;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;

              &.expanded {
                display: block;
                -webkit-line-clamp: unset;
                line-clamp: unset;
                overflow: visible;
              }
            }

            .desc-expand-btn {
              background: none;
              border: none;
              color: var(--el-color-primary);
              cursor: pointer;
              padding: 4px 8px;
              border-radius: 4px;
              transition: all 0.3s ease;
              font-size: 12px;
              margin-top: 4px;

              &:hover {
                background-color: var(--el-color-primary-light-9);
              }
            }
          }

          .column-stats {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 6px;
              font-size: 13px;
              color: var(--el-text-color-regular);

              .el-icon {
                font-size: 14px;
                color: var(--el-color-primary);
              }
            }
          }
        }
      }
    }

    // 文章列表区域
    .article-section {
      background: var(--el-bg-color);
      border-radius: 12px;
      box-shadow: 0 2px 12px var(--el-border-color-light);

      // 文章列表标题和排序
      .article-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        border-bottom: 1px solid var(--el-border-color-light);

        h2 {
          font-size: 18px;
          font-weight: 600;
          margin: 0;
          color: var(--el-text-color-primary);
        }

        .sort-controls {
          display: flex;
          align-items: center;
        }

        // 响应式
        @media (max-width: 768px) {
          flex-direction: column;
          gap: 16px;
          align-items: stretch;
        }
      }

      // 文章列表容器
      .article-list-container {
        padding: 0 24px 24px;

        .loading-container {
          padding: 20px 0;
        }

        .article-skeleton {
          display: flex;
          gap: 16px;
          padding: 20px 0;
          border-bottom: 1px solid var(--el-border-color-light);
          align-items: flex-start;

          .skeleton-index {
            width: 24px;
            height: 24px;
            background: var(--el-border-color-light);
            border-radius: 50%;
            flex-shrink: 0;
            margin-top: 8px;
          }

          .skeleton-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;
          }
        }

        .empty-state {
          padding: 60px 0;
          text-align: center;
        }

        .article-list {
          .article-item {
            display: flex;
            gap: 16px;
            padding: 10px 0;
            border-bottom: 1px solid var(--el-border-color-light);
            cursor: pointer;
            transition: all 0.3s ease;
            align-items: flex-start;

            &:last-child {
              border-bottom: none;
            }

            &:hover {
              background-color: var(--el-bg-color-page);
              transform: translateX(4px);
              border-radius: 8px;
              padding-left: 16px;
            }

            // 文章序号
            .article-index {
              width: 24px;
              height: 24px;
              background: var(--el-color-primary-light-8);
              color: var(--el-color-primary);
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 12px;
              font-weight: 600;
              flex-shrink: 0;
              margin-top: 8px;
            }

            // 文章封面
            .article-cover {
              width: 100px;
              height: 75px;
              border-radius: 6px;
              transition: transform 0.3s ease;
              flex-shrink: 0;

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

              .error {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                background-color: var(--el-bg-color-page);

                .el-icon {
                  font-size: 18px;
                  color: var(--el-text-color-placeholder);
                }
              }
            }

            // 文章内容
            .article-content {
              flex: 1;
              display: flex;
              flex-direction: column;
              justify-content: space-between;
              min-width: 0;

              .article-title {
                font-size: 16px;
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
                font-size: 13px;
                color: var(--el-text-color-regular);
                margin: 0 0 12px 0;
                line-height: 1.5;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .article-meta {
                font-size: 12px;
                color: var(--el-text-color-secondary);
                display: flex;
                align-items: center;
                gap: 16px;
                flex-wrap: wrap;

                span {
                  display: flex;
                  align-items: center;
                  gap: 4px;

                  .el-icon {
                    font-size: 12px;
                  }
                }
              }
            }

            // 响应式
            @media (max-width: 768px) {
              flex-direction: column;
              gap: 12px;

              .article-index {
                align-self: flex-start;
              }

              .article-cover {
                width: 100%;
                height: 150px;
              }
            }
          }
        }
      }
    }
  }

  // 右侧边栏
  .right-sidebar {
    .sidebar-card {
      background: var(--el-bg-color);
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      margin-bottom: 20px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 2px solid var(--el-color-primary);
      }

      // 专栏信息
      .column-sidebar-info {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 14px;

          .info-label {
            color: var(--el-text-color-regular);
          }

          .info-value {
            color: var(--el-text-color-primary);
            font-weight: 500;
          }
        }
      }

      // 相关推荐
      .recommendations {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .recommendation-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 12px;
          background-color: var(--el-bg-color-page);
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background-color: var(--el-color-primary-light-9);
            transform: translateX(2px);
          }

          .el-icon {
            color: var(--el-color-primary);
            font-size: 14px;
          }

          span {
            font-size: 14px;
            color: var(--el-text-color-regular);
          }
        }
      }
    }

    // 响应式：平板端隐藏右侧边栏
    @media (max-width: 992px) {
      display: none;
    }
  }

  // 返回顶部按钮
  .back-to-top {
    position: fixed;
    right: 30px;
    bottom: 30px;
    width: 50px;
    height: 50px;
    background-color: var(--el-bg-color);
    border: 1px solid var(--el-border-color);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 1000;

    &:hover {
      background: var(--el-color-primary);
      color: white;
      transform: translateY(-2px);
    }

    .el-icon {
      font-size: 20px;
    }
  }
}
</style>
