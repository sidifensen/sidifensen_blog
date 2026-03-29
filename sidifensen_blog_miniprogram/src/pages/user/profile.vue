<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getUserInfo, followUser, unfollowUser, getFollowers, getFollowings, getUserArticles, getUserAlbums } from '@/api/user'
import { formatCount } from '@/utils/format'

const userStore = useUserStore()
const userId = ref('')
const userInfo = ref({})
const userArticles = ref([])
const userAlbums = ref([])
const isFollowing = ref(false)
const loading = ref(false)
const activeTab = ref('articles')

const tabs = [
  { key: 'articles', name: '文章' },
  { key: 'albums', name: '相册' }
]

/**
 * 获取用户信息
 */
async function fetchUserInfo() {
  try {
    loading.value = true
    const res = await getUserInfo(userId.value)
    userInfo.value = res.data || res
  } catch (err) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 获取用户文章和相册列表
 */
async function fetchUserContent() {
  try {
    const [articlesRes, albumsRes] = await Promise.all([
      getUserArticles(userId.value, { pageNum: 1, pageSize: 20 }),
      getUserAlbums(userId.value, { pageNum: 1, pageSize: 20 })
    ])
    userArticles.value = articlesRes.data || articlesRes || []
    userAlbums.value = albumsRes.data || albumsRes || []
  } catch (err) {
    console.error('获取用户内容失败', err)
  }
}

/**
 * 检查关注状态
 */
async function checkFollowStatus() {
  try {
    const res = await getFollowers(userId.value, { pageNum: 1, pageSize: 100 })
    const followers = res.data || res || []
    const currentUserId = userStore.userInfo?.id
    isFollowing.value = followers.some(f => f.id === currentUserId)
  } catch (err) {
    console.error('检查关注状态失败', err)
  }
}

/**
 * 关注/取消关注
 */
async function handleFollow() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  try {
    if (isFollowing.value) {
      await unfollowUser(userId.value)
      isFollowing.value = false
      userInfo.value.followerCount--
    } else {
      await followUser(userId.value)
      isFollowing.value = true
      userInfo.value.followerCount++
    }
  } catch (err) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 切换标签
 */
function onTabChange(tab) {
  activeTab.value = tab
}

/**
 * 跳转文章详情
 */
function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

/**
 * 跳转相册详情
 */
function goToAlbum(albumId) {
  uni.navigateTo({ url: `/pages/albumDetail/albumDetail?id=${albumId}` })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  userId.value = currentPage.options?.userId || ''

  if (userId.value) {
    fetchUserInfo()
    fetchUserContent()
    checkFollowStatus()
  }
})
</script>

<template>
  <view class="profile-page">
    <!-- 用户信息头部 -->
    <view class="profile-header card">
      <view class="user-info">
        <uv-avatar :src="userInfo.avatar" size="160" />
        <view class="user-detail">
          <view class="nickname">{{ userInfo.nickname || '匿名用户' }}</view>
          <view v-if="userInfo.bio" class="bio">{{ userInfo.bio }}</view>
        </view>
      </view>

      <view class="user-stats">
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.articleCount) }}</view>
          <view class="stat-label">文章</view>
        </view>
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.followerCount) }}</view>
          <view class="stat-label">粉丝</view>
        </view>
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.followingCount) }}</view>
          <view class="stat-label">关注</view>
        </view>
      </view>

      <!-- 关注按钮 -->
      <button
        v-if="userId != userStore.userInfo?.id"
        class="follow-btn"
        :class="{ following: isFollowing }"
        @click="handleFollow"
      >
        {{ isFollowing ? '已关注' : '关注' }}
      </button>
    </view>

    <!-- 内容切换 -->
    <view class="content-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="onTabChange(tab.key)"
      >
        {{ tab.name }}
      </view>
    </view>

    <!-- 内容列表 -->
    <scroll-view class="content-list" scroll-y>
      <view v-if="activeTab === 'articles'" class="article-list">
        <!-- 文章列表 -->
        <view
          v-for="article in userArticles"
          :key="article.id"
          class="article-item card"
          @click="goToArticle(article.id)"
        >
          <view class="article-title">{{ article.title }}</view>
          <view class="article-summary">{{ article.summary }}</view>
        </view>
      </view>

      <view v-if="activeTab === 'albums'" class="album-list">
        <!-- 相册列表 -->
        <view
          v-for="album in userAlbums"
          :key="album.id"
          class="album-item"
          @click="goToAlbum(album.id)"
        >
          <image class="album-cover" :src="album.coverUrl" mode="aspectFill" />
          <view class="album-name">{{ album.name }}</view>
        </view>
      </view>

      <view v-if="!userArticles.length && !userAlbums.length" class="empty-state">
        <text class="empty-text">暂无内容</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.profile-header {
  text-align: center;

  .user-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-md);

    .user-detail {
      .nickname {
        font-size: 20px;
        font-weight: 600;
        color: var(--u-main-color);
      }

      .bio {
        font-size: 14px;
        color: var(--u-tips-color);
        margin-top: var(--spacing-sm);
      }
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;
    margin-top: var(--spacing-xl);
    padding-top: var(--spacing-lg);
    border-top: 1px solid var(--u-border-color);

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: var(--u-main-color);
      }

      .stat-label {
        font-size: 12px;
        color: var(--u-tips-color);
        margin-top: 4px;
      }
    }
  }

  .follow-btn {
    margin-top: var(--spacing-lg);
    padding: 0 var(--spacing-2xl);
    height: 36px;
    line-height: 36px;
    background: var(--u-type-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-full);
    font-size: 14px;

    &.following {
      background: var(--u-bg-color);
      color: var(--u-content-color);
      border: 1px solid var(--u-border-color);
    }
  }
}

.content-tabs {
  display: flex;
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);
  margin-top: var(--spacing-md);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: var(--spacing-md) 0;
    font-size: 15px;
    color: var(--u-tips-color);

    &.active {
      color: var(--u-type-primary);
      font-weight: 500;
    }
  }
}

.content-list {
  height: calc(100vh - 300px);
  padding: var(--spacing-lg);
}

.article-list {
  .article-item {
    margin-bottom: var(--spacing-md);

    .article-title {
      font-size: 15px;
      font-weight: 500;
      color: var(--u-main-color);
    }

    .article-summary {
      font-size: 14px;
      color: var(--u-tips-color);
      margin-top: var(--spacing-sm);
      @include text-ellipsis(2);
    }
  }
}

.album-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);

  .album-item {
    border-radius: var(--radius-md);
    overflow: hidden;
    border: 1px solid var(--u-border-color);

    .album-cover {
      width: 100%;
      height: 150px;
    }

    .album-name {
      font-size: 14px;
      color: var(--u-main-color);
      padding: var(--spacing-sm);
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--u-tips-color);
}
</style>
