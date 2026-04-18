<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { getMyInfo } from '@/api/user'
import { getUserArticleStatistics } from '@/api/article'
import { formatCount } from '@/utils/format'
import TabBar from '@/components/TabBar/TabBar.vue'
import defaultAvatar from '@/static/default-avatar.svg'

const userStore = useUserStore()

// 头像加载失败状态
const avatarError = ref(false)

// 用户信息
const userInfo = ref({
  avatar: '',
  nickname: '未登录',
  bio: '',
  articleCount: 0,
  followerCount: 0,
  followingCount: 0
})

/**
 * 更新用户信息显示
 */
async function updateUserInfo() {
  avatarError.value = false
  if (userStore.isLoggedIn) {
    try {
      // 并行调用用户信息和文章统计接口
      const [infoRes, statsRes] = await Promise.all([
        getMyInfo(),
        getUserArticleStatistics()
      ])
      const info = infoRes.data || infoRes
      const stats = statsRes.data || statsRes
      userStore.updateUserInfo(info)
      userInfo.value = {
        avatar: info.avatar || '',
        nickname: info.nickname || '用户',
        bio: info.introduction || '',
        articleCount: stats.publishedCount || stats.totalCount || 0,
        followerCount: info.fansCount || 0,
        followingCount: info.followCount || 0
      }
    } catch (err) {
      console.error('获取用户信息失败', err)
      // 接口失败时使用本地缓存
      userInfo.value = {
        avatar: userStore.userInfo?.avatar || '',
        nickname: userStore.userInfo?.nickname || '用户',
        bio: userStore.userInfo?.introduction || '',
        articleCount: 0,
        followerCount: userStore.userInfo?.fansCount || 0,
        followingCount: userStore.userInfo?.followCount || 0
      }
    }
  } else {
    userInfo.value = {
      avatar: '',
      nickname: '未登录',
      bio: '',
      articleCount: 0,
      followerCount: 0,
      followingCount: 0
    }
  }
}

/**
 * 跳转到登录页
 */
function goToLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}

/**
 * 跳转到消息页
 */
function goToMessage() {
  uni.navigateTo({ url: '/pages/message/message' })
}

/**
 * 跳转到设置页
 */
function goToSettings() {
  uni.navigateTo({ url: '/pages/settings/settings' })
}

/**
 * 跳转到我的文章
 */
function goToMyArticles() {
  if (!userStore.isLoggedIn) {
    goToLogin()
    return
  }
  uni.navigateTo({ url: '/pages/user/articles' })
}

/**
 * 跳转到我的收藏
 */
function goToMyFavorites() {
  if (!userStore.isLoggedIn) {
    goToLogin()
    return
  }
  uni.navigateTo({ url: '/pages/user/favorites' })
}

/**
 * 跳转到我的相册
 */
function goToMyAlbums() {
  if (!userStore.isLoggedIn) {
    goToLogin()
    return
  }
  uni.navigateTo({ url: '/pages/user/albums' })
}

onMounted(() => {
  updateUserInfo()
})

// 每次页面显示时都更新用户信息
onShow(() => {
  updateUserInfo()
})
</script>

<template>
  <view class="user-page">
    <!-- 用户信息卡片 -->
    <view class="user-card card">
      <view class="user-info" @click="!userStore.isLoggedIn && goToLogin()">
        <view class="avatar-wrapper">
          <image v-if="userInfo.avatar && !avatarError" class="avatar-img" :src="userInfo.avatar" mode="aspectFill" @error="avatarError = true" />
          <image v-else class="avatar-img" :src="defaultAvatar" mode="aspectFill" />
        </view>
        <view class="user-detail">
          <view class="nickname">{{ userInfo.nickname }}</view>
          <view v-if="userInfo.bio" class="bio">{{ userInfo.bio }}</view>
          <view v-else-if="!userStore.isLoggedIn" class="bio tip">点击登录</view>
        </view>
      </view>

      <!-- 用户数据 -->
      <view class="user-stats">
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.articleCount) }}</view>
          <view class="stat-label">文章</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.followerCount) }}</view>
          <view class="stat-label">粉丝</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ formatCount(userInfo.followingCount) }}</view>
          <view class="stat-label">关注</view>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-list card">
      <view class="menu-item" @click="goToMyArticles">
        <view class="menu-icon"><u-icon name="file-text" color="var(--u-type-primary)" size="20px" /></view>
        <text class="menu-text">我的文章</text>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
      <view class="menu-item" @click="goToMyFavorites">
        <view class="menu-icon"><u-icon name="star" color="var(--u-type-warning)" size="20px" /></view>
        <text class="menu-text">我的收藏</text>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
      <view class="menu-item" @click="goToMyAlbums">
        <view class="menu-icon"><u-icon name="grid" color="var(--u-type-success)" size="20px" /></view>
        <text class="menu-text">我的相册</text>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
      <view class="menu-item" @click="goToMessage">
        <view class="menu-icon"><u-icon name="chat" color="var(--u-type-error)" size="20px" /></view>
        <text class="menu-text">消息通知</text>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
    </view>

    <!-- 设置入口 -->
    <view class="menu-list card">
      <view class="menu-item" @click="goToSettings">
        <view class="menu-icon"><u-icon name="setting" color="var(--text-regular)" size="20px" /></view>
        <text class="menu-text">设置</text>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
    </view>

    <!-- 自定义底部导航栏 -->
    <TabBar />
  </view>
</template>

<style lang="scss" scoped>
.user-page {
  padding: var(--spacing-lg);
  padding-bottom: calc(60px + var(--spacing-lg));
  background: var(--u-bg-color);
  min-height: 100vh;
}

/* 用户卡片 */
.user-card {
  .user-info {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);

    .avatar-wrapper {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      overflow: hidden;
      flex-shrink: 0;

      .avatar-img {
        width: 100%;
        height: 100%;
      }
    }

    .user-detail {
      flex: 1;

      .nickname {
        font-size: 18px;
        font-weight: 600;
        color: var(--u-main-color);
      }

      .bio {
        font-size: 14px;
        color: var(--u-tips-color);
        margin-top: 4px;

        &.tip {
          color: var(--u-type-primary);
        }
      }
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;
    padding-top: var(--spacing-md);
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

    .stat-divider {
      width: 1px;
      background: var(--u-border-color);
    }
  }
}

/* 菜单列表 */
.menu-list {
  margin-top: var(--spacing-md);
  padding: 0;

  .menu-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: var(--spacing-md) var(--spacing-lg);

    &:not(:last-child)::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: var(--spacing-lg);
      right: var(--spacing-lg);
      height: 1px;
      background: var(--u-border-color);
    }

    .menu-icon {
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: var(--spacing-md);
    }

    .menu-text {
      flex: 1;
      font-size: 15px;
      color: var(--u-main-color);
    }
  }
}
</style>
