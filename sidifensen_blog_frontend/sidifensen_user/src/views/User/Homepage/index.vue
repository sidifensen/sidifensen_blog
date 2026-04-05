<template>
  <div class="user-homepage">
    <!-- 视差背景层 -->
    <div class="parallax-bg" ref="parallaxBg"></div>
    <!-- 渐变遮罩层 -->
    <div class="gradient-overlay"></div>

    <!-- 用户信息区域 -->
    <UserProfileCard
      :user-info="userInfo"
      :user-loading="userLoading"
      :total-views="totalViews"
      :is-current-user="isCurrentUser"
      :is-followed="isFollowed"
      :follow-loading="followLoading"
      @follow="handleFollow"
      @message="handleMessage"
    />

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
                <!-- 历史标签页只对当前用户显示 -->
                <el-tab-pane v-if="isCurrentUser" label="历史" name="history" />
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
                <ColumnList
                  v-else-if="activeTab === 'column'"
                  key="column"
                  :column-list="columnList"
                  :column-loading="columnLoading"
                  :loading-more="loadingMore"
                  @column-click="goToColumn"
                />

                <!-- 收藏列表 -->
                <FavoriteList
                  v-else-if="activeTab === 'favorite'"
                  key="favorite"
                  :favorite-list="favoriteList"
                  :favorite-loading="favoriteLoading"
                  :is-current-user="isCurrentUser"
                  @toggle-favorite="toggleFavorite"
                  @article-click="goToArticle"
                  @update-favorite="handleUpdateFavorite"
                />

                <!-- 关注列表 -->
                <FollowList v-else-if="activeTab === 'follow'" key="follow" />

                <!-- 历史列表 -->
                <HistoryList
                  v-else-if="activeTab === 'history'"
                  key="history"
                  ref="historyListRef"
                />
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
                  <el-icon><Trophy /></el-icon>
                  <span>创作达人</span>
                </div>
                <div class="achievement-item" v-if="totalViews >= 1000">
                  <el-icon><View /></el-icon>
                  <span>阅读之星</span>
                </div>
                <div class="achievement-item" v-if="userInfo?.fansCount >= 100">
                  <el-icon><User /></el-icon>
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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Trophy, View, User, ArrowUp } from '@element-plus/icons-vue'
import { getUserInfoById } from '@/api/user'
import { toggleFollow, isFollowing } from '@/api/follow'
import { getUserArticleList, getUserArticleStatisticsById } from '@/api/article'
import { getUserColumnList } from '@/api/column'
import { getFavoriteListByUserId, getArticleListByFavoriteId, updateFavorite } from '@/api/favorite'
import { useUserStore } from '@/stores/userStore'
import UserProfileCard from './components/UserProfileCard.vue'
import ArticleList from './components/ArticleList.vue'
import ColumnList from './components/ColumnList.vue'
import FavoriteList from './components/FavoriteList.vue'
import FollowList from './components/FollowList.vue'
import HistoryList from './components/HistoryList.vue'

// 路由和状态管理
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const userLoading = ref(false)
const articleLoading = ref(false)
const columnLoading = ref(false)
const favoriteLoading = ref(false)
const loadingMore = ref(false)
const followLoading = ref(false)
const userInfo = ref(null)
const articleList = ref([])
const columnList = ref([])
const favoriteList = ref([])
const total = ref(0)
const columnTotal = ref(0)
const totalViews = ref(0)
const articleStatistics = ref(null)
const activeTab = ref('article')
const sortType = ref('time')
const visibilityType = ref('all')
const isFollowed = ref(false)
const hasMore = ref(true)
const columnHasMore = ref(true)
const currentPage = ref(1)
const columnCurrentPage = ref(1)
const showBackToTop = ref(false)
const parallaxBg = ref(null)

// 每页数据量
const pageSize = ref(10)

// 历史组件引用
const historyListRef = ref(null)

// 计算属性
const isCurrentUser = computed(() => {
  return userStore.user?.id === parseInt(route.params.userId)
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    userLoading.value = true
    const userId = route.params.userId
    const res = await getUserInfoById(userId)
    userInfo.value = res.data

    if (!isCurrentUser.value && userStore.user) {
      await checkUserFollowStatus()
    }
  } catch (error) {
    // 静默处理
    ElMessage.error('获取用户信息失败')
  } finally {
    userLoading.value = false
  }
}

// 获取文章统计信息
const fetchArticleStatistics = async () => {
  try {
    const userId = route.params.userId
    const res = await getUserArticleStatisticsById(userId)
    articleStatistics.value = res.data
    if (articleStatistics.value && articleStatistics.value.totalReadCount !== undefined) {
      totalViews.value = articleStatistics.value.totalReadCount
    }
  } catch (error) {
    // 静默处理
    ElMessage.error('获取文章统计信息失败')
  }
}

// 获取文章列表
const fetchArticleList = async (reset = false) => {
  if (!hasMore.value || articleLoading.value || loadingMore.value) {
    return
  }

  try {
    if (reset) {
      articleLoading.value = true
    } else {
      loadingMore.value = true
    }

    const userId = route.params.userId
    const articleStatusDto = { userId: parseInt(userId) }
    articleStatusDto.orderBy = sortType.value === 'time' ? 0 : 1

    if (isCurrentUser.value) {
      if (visibilityType.value === 'all') {
        articleStatusDto.visibleRange = 0
        articleStatusDto.examineStatus = 1
      } else if (visibilityType.value === 'private') {
        articleStatusDto.visibleRange = 1
      } else if (visibilityType.value === 'pending') {
        articleStatusDto.examineStatusList = [0, 2]
      }
    }

    const res = await getUserArticleList(currentPage.value, pageSize.value, articleStatusDto)
    const newArticles = res.data.data || []
    total.value = res.data.total || 0

    if (reset) {
      articleList.value = newArticles
    } else {
      articleList.value = [...articleList.value, ...newArticles]
    }

    hasMore.value = articleList.value.length < total.value

    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++
    }

    if (articleStatistics.value && articleStatistics.value.totalReadCount !== undefined) {
      totalViews.value = articleStatistics.value.totalReadCount
    }
  } catch (error) {
    // 静默处理
  } finally {
    articleLoading.value = false
    loadingMore.value = false
  }
}

// 获取专栏列表
const fetchColumnList = async (reset = false) => {
  if (!columnHasMore.value || columnLoading.value || loadingMore.value) {
    return
  }

  try {
    if (reset) {
      columnLoading.value = true
    } else {
      loadingMore.value = true
    }

    const userId = route.params.userId
    const res = await getUserColumnList(columnCurrentPage.value, pageSize.value, parseInt(userId))
    const newColumns = res.data.data || []
    columnTotal.value = res.data.total || 0

    if (reset) {
      columnList.value = newColumns
    } else {
      columnList.value = [...columnList.value, ...newColumns]
    }

    columnHasMore.value = columnList.value.length < columnTotal.value

    if (columnHasMore.value && newColumns.length > 0) {
      columnCurrentPage.value++
    }
  } catch (error) {
    // 静默处理
  } finally {
    columnLoading.value = false
    loadingMore.value = false
  }
}

// 获取收藏夹列表
const fetchFavoriteList = async () => {
  try {
    favoriteLoading.value = true
    const userId = route.params.userId
    const res = await getFavoriteListByUserId(parseInt(userId))
    favoriteList.value = (res.data || []).map((favorite) => ({
      ...favorite,
      expanded: false,
      loading: false,
      articles: [],
    }))
  } catch (error) {
    // 静默处理
  } finally {
    favoriteLoading.value = false
  }
}

// 获取收藏夹中的文章列表
const fetchFavoriteArticleList = async (favorite) => {
  try {
    favorite.loading = true
    const res = await getArticleListByFavoriteId(favorite.id)
    favorite.articles = res.data || []
  } catch (error) {
    // 静默处理
  } finally {
    favorite.loading = false
  }
}

// 切换收藏夹展开状态
const toggleFavorite = async (favorite) => {
  favorite.expanded = !favorite.expanded
  if (favorite.expanded && favorite.articles.length === 0) {
    await fetchFavoriteArticleList(favorite)
  }
}

// 处理更新收藏夹
const handleUpdateFavorite = async (formData) => {
  try {
    await updateFavorite(formData)
    const favoriteIndex = favoriteList.value.findIndex((f) => f.id === formData.id)
    if (favoriteIndex !== -1) {
      favoriteList.value[favoriteIndex].name = formData.name
      favoriteList.value[favoriteIndex].showStatus = formData.showStatus
    }
    ElMessage.success('收藏夹更新成功')
  } catch (error) {
    // 静默处理
    ElMessage.error('更新收藏夹失败')
    throw error
  }
}

// 切换文章筛选标签
const handleTabChange = (tabName) => {
  activeTab.value = tabName

  if (tabName === 'article' && articleList.value.length === 0) {
    currentPage.value = 1
    hasMore.value = true
    fetchArticleList(true)
  } else if (tabName === 'column' && columnList.value.length === 0) {
    columnCurrentPage.value = 1
    columnHasMore.value = true
    fetchColumnList(true)
  } else if (tabName === 'favorite' && favoriteList.value.length === 0) {
    fetchFavoriteList()
  }
}

// 处理排序条件变化
const handleSortChange = (value) => {
  sortType.value = value
  currentPage.value = 1
  articleList.value = []
  hasMore.value = true
  fetchArticleList(true)
}

// 处理可见范围变化
const handleVisibilityChange = (value) => {
  visibilityType.value = value
  currentPage.value = 1
  articleList.value = []
  hasMore.value = true
  fetchArticleList(true)
}

// 检查用户关注状态
const checkUserFollowStatus = async () => {
  try {
    const followerId = userStore.user.id
    const followedId = parseInt(route.params.userId)
    const res = await isFollowing(followerId, followedId)
    isFollowed.value = res.data
  } catch (error) {
    // 静默处理
    isFollowed.value = false
  }
}

// 关注用户
const handleFollow = async () => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    followLoading.value = true
    const followedId = parseInt(route.params.userId)
    const wasFollowed = isFollowed.value

    await toggleFollow(followedId)
    isFollowed.value = !wasFollowed

    ElMessage.success(isFollowed.value ? '关注成功' : '取消关注成功')

    if (userInfo.value) {
      userInfo.value.fansCount = isFollowed.value
        ? (userInfo.value.fansCount || 0) + 1
        : Math.max((userInfo.value.fansCount || 0) - 1, 0)
    }
  } catch (error) {
    // 静默处理
  } finally {
    followLoading.value = false
  }
}

// 私信用户
const handleMessage = () => {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/message/chat/${route.params.userId}`)
}

// 返回顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 跳转至文章详情页
const goToArticle = (articleId) => {
  const userId = route.params.userId
  router.push(`/user/${userId}/article/${articleId}`)
}

// 跳转至专栏详情页
const goToColumn = (columnId) => {
  const userId = route.params.userId
  router.push(`/user/${userId}/column/${columnId}`)
}

// 处理页面滚动事件
const handlePageScroll = () => {
  showBackToTop.value = window.scrollY > 200

  // 视差背景效果
  if (parallaxBg.value) {
    const scrollY = window.scrollY
    parallaxBg.value.style.transform = `translateY(${scrollY * 0.3}px)`
  }

  if (activeTab.value === 'favorite' || activeTab.value === 'follow') {
    return
  }

  const scrollHeight = document.documentElement.scrollHeight
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const clientHeight = window.innerHeight

  if (scrollTop + clientHeight >= scrollHeight - 100) {
    if (
      activeTab.value === 'article' &&
      !articleLoading.value &&
      !loadingMore.value &&
      hasMore.value
    ) {
      fetchArticleList()
    } else if (
      activeTab.value === 'column' &&
      !columnLoading.value &&
      !loadingMore.value &&
      columnHasMore.value
    ) {
      fetchColumnList()
    } else if (activeTab.value === 'history' && historyListRef.value) {
      historyListRef.value.loadMore()
    }
  }
}

// 监听路由参数变化
watch(
  () => route.params.userId,
  (newUserId) => {
    if (newUserId) {
      currentPage.value = 1
      columnCurrentPage.value = 1
      articleList.value = []
      columnList.value = []
      favoriteList.value = []
      hasMore.value = true
      columnHasMore.value = true

      if (activeTab.value === 'history' && userStore.user?.id !== parseInt(newUserId)) {
        activeTab.value = 'article'
      } else if (activeTab.value !== 'history') {
        activeTab.value = 'article'
      }

      isFollowed.value = false

      fetchUserInfo()
      fetchArticleStatistics()
      fetchArticleList(true)
    }
  },
  { immediate: true },
)

// 组件挂载
onMounted(() => {
  window.addEventListener('scroll', handlePageScroll)
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('scroll', handlePageScroll)
})
</script>

<style lang="scss" scoped>
.user-homepage {
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  min-height: 100vh;
  position: relative;
  //overflow-x: hidden;
  background: var(--bg-page);

  // 黑夜模式适配
  html.dark & {
    --bg-page: #0f172a;
    --bg-card: #1e293b;
  }

  // 视差背景层
  .parallax-bg {
    position: fixed;
    top: -10%;
    left: 0;
    width: 100%;
    height: 120%;
    z-index: 0;
    will-change: transform;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      // background: linear-gradient(135deg, rgba(15, 23, 42, 0.5) 0%, rgba(30, 41, 59, 0.4) 100%),
      //   url("https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=2072&auto=format&fit=crop") center/cover no-repeat;
      filter: blur(10px);
      transform: scale(1.1);
      opacity: 0.6;
    }

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image:
        linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
      background-size: 60px 60px;
      pointer-events: none;
      opacity: 0.3;
    }
  }

  // 渐变遮罩层
  .gradient-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    background:
      radial-gradient(circle at 50% 0%, rgba(99, 102, 241, 0.12) 0%, transparent 50%),
      radial-gradient(circle at 100% 50%, rgba(168, 85, 247, 0.08) 0%, transparent 40%),
      radial-gradient(circle at 0% 100%, rgba(59, 130, 246, 0.1) 0%, transparent 40%);
    pointer-events: none;
  }

  // 内容区域
  .content-section {
    padding: 20px 0 60px 0;
    position: relative;
    z-index: 1;

    .content-layout {
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 24px;
    }
  }

  // 主要内容区域
  .main-content {
    --tab-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
    --tab-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
    --tab-shadow: rgba(0, 0, 0, 0.04);
    --tab-shadow-light: rgba(0, 0, 0, 0.02);
    --tab-hover-bg: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.08);
    --tab-active-shadow: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.3);
    min-width: 0;

    // 黑夜模式适配
    html.dark & {
      --tab-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.9);
      --tab-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
      --tab-shadow: rgba(0, 0, 0, 0.2);
      --tab-shadow-light: rgba(0, 0, 0, 0.1);
      --tab-hover-bg: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.1);
      --tab-active-shadow: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.3);
    }

    .tab-filters {
      background: var(--tab-bg);
      backdrop-filter: blur(20px);
      border-radius: 12px;
      padding: 4px;
      margin-bottom: 16px;
      border: 1px solid var(--tab-border);
      box-shadow:
        0 4px 24px var(--tab-shadow),
        0 1px 3px var(--tab-shadow-light);
      transition: all 0.3s ease;

      &:hover {
        box-shadow:
          0 8px 32px var(--tab-shadow),
          0 2px 6px var(--tab-shadow-light);
      }

      ::v-deep(.el-tabs__header) {
        margin: 0;
        padding: 0;
      }

      ::v-deep(.el-tabs__nav-wrap) {
        &::after {
          display: none;
        }
      }

      ::v-deep(.el-tabs__item) {
        font-size: 15px;
        font-weight: 500;
        padding: 10px 20px !important;
        border-radius: 8px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        margin: 0 4px;

        &:hover {
          background: var(--tab-hover-bg);
        }

        &.is-active {
          background: linear-gradient(
            135deg,
            var(--el-color-primary) 0%,
            var(--el-color-primary-light-3) 100%
          );
          color: var(--el-color-white, #fff);
          box-shadow: 0 4px 16px var(--tab-active-shadow);
        }
      }
    }

    .tab-content-container {
      position: relative;

      > div {
        animation: contentFadeIn 0.5s ease forwards;
      }
    }
  }

  // 右侧边栏
  .sidebar {
    --sidebar-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
    --sidebar-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
    --sidebar-shadow: rgba(0, 0, 0, 0.06);
    --sidebar-shadow-light: rgba(0, 0, 0, 0.04);
    --achievement-bg: linear-gradient(
      135deg,
      rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.06) 0%,
      rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.02) 100%
    );
    --achievement-bg-hover: linear-gradient(
      135deg,
      rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.12) 0%,
      rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.04) 100%
    );
    --achievement-border: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.1);

    // 黑夜模式适配
    html.dark & {
      --sidebar-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.9);
      --sidebar-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
      --sidebar-shadow: rgba(0, 0, 0, 0.2);
      --sidebar-shadow-light: rgba(0, 0, 0, 0.1);
      --achievement-bg: linear-gradient(
        135deg,
        rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.1) 0%,
        rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.03) 100%
      );
      --achievement-bg-hover: linear-gradient(
        135deg,
        rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.15) 0%,
        rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.05) 100%
      );
      --achievement-border: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.15);
    }

    .sidebar-card {
      background: var(--sidebar-bg);
      backdrop-filter: blur(20px);
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 20px;
      border: 1px solid var(--sidebar-border);
      box-shadow:
        0 4px 24px var(--sidebar-shadow),
        0 1px 3px var(--sidebar-shadow-light);
      transition: all 0.3s ease;

      &:hover {
        box-shadow:
          0 8px 32px var(--sidebar-shadow),
          0 2px 6px var(--sidebar-shadow-light);
        transform: translateY(-2px);
      }

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 16px 0;
        padding-bottom: 12px;
        border-bottom: 2px solid var(--el-color-primary);
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 40px;
          height: 2px;
          background: linear-gradient(90deg, var(--el-color-primary) 0%, transparent 100%);
        }
      }

      .achievements {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .achievement-item {
          display: flex;
          align-items: center;
          gap: 10px;
          padding: 12px 14px;
          background: var(--achievement-bg);
          border-radius: 10px;
          border: 1px solid var(--achievement-border);
          transition: all 0.3s ease;

          &:hover {
            background: var(--achievement-bg-hover);
            transform: translateX(4px);
          }

          .achievement-icon {
            color: var(--el-color-primary);
            font-size: 18px;
          }

          span {
            font-size: 14px;
            color: var(--el-text-color-regular);
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 工具类
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

// 标签页内容容器
.tab-content-container {
  min-height: 400px;
  position: relative;

  > div {
    min-height: 100%;
  }
}

// 标签页切换过渡动画
.tab-fade-enter-active,
.tab-fade-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.tab-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.tab-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// 内容渐入动画
@keyframes contentFadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

    .sidebar {
      display: none;
    }
  }
}

@media (max-width: 768px) {
  .user-homepage {
    .content-section {
      padding: 10px 0 40px 0;
    }

    .main-content {
      .tab-filters {
        padding: 0 10px 10px 10px;
        margin-bottom: 10px;
      }
    }

    .tab-content-container {
      min-height: 300px;
    }
  }
}

// 返回顶部按钮
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
  backdrop-filter: blur(10px);
  background-color: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
  border: 1px solid var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 100;

  &:hover {
    background: linear-gradient(
      135deg,
      var(--el-color-primary) 0%,
      var(--el-color-primary-light-3) 100%
    );
    color: white;
    transform: translateY(-4px) scale(1.05);
    box-shadow: 0 8px 24px rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.4);
  }

  .el-icon {
    font-size: 18px;
  }

  @media (max-width: 768px) {
    width: 44px;
    height: 44px;
    right: 15px;
    bottom: 15px;

    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
