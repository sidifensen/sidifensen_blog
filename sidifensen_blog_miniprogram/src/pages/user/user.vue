<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

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
function updateUserInfo() {
  if (userStore.isLoggedIn && userStore.userInfo) {
    userInfo.value = {
      avatar: userStore.userInfo.avatar || '',
      nickname: userStore.userInfo.nickname || '用户',
      bio: userStore.userInfo.introduction || '',
      articleCount: userStore.userInfo.articleCount || 0,
      followerCount: userStore.userInfo.fansCount || 0,
      followingCount: userStore.userInfo.followCount || 0
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
 * 跳转到 VIP 页
 */
function goToVip() {
  uni.navigateTo({ url: '/pages/vip/vip' })
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
        <uv-avatar :src="userInfo.avatar" size="60px" />
        <view class="user-detail">
          <view class="nickname">{{ userInfo.nickname }}</view>
          <view v-if="userInfo.bio" class="bio">{{ userInfo.bio }}</view>
          <view v-else class="bio tip">点击登录</view>
        </view>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="18px" />
      </view>

      <!-- 用户数据 -->
      <view class="user-stats">
        <view class="stat-item">
          <view class="stat-value">{{ userInfo.articleCount }}</view>
          <view class="stat-label">文章</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ userInfo.followerCount }}</view>
          <view class="stat-label">粉丝</view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-value">{{ userInfo.followingCount }}</view>
          <view class="stat-label">关注</view>
        </view>
      </view>
    </view>

    <!-- VIP 卡片 -->
    <view class="vip-card card" @click="goToVip">
      <view class="vip-left">
        <uv-icon name="crown" color="#fbbf24" size="32px" />
        <view class="vip-info">
          <view class="vip-title">VIP 会员</view>
          <view class="vip-desc">开通享更多权益</view>
        </view>
      </view>
      <view class="vip-action">
        <text class="vip-btn">立即开通</text>
        <uv-icon name="arrow-right" color="var(--color-primary)" size="16px" />
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-list card">
      <view class="menu-item" @click="goToMyArticles">
        <uv-icon name="file-text" color="var(--color-primary)" size="20px" />
        <text class="menu-text">我的文章</text>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
      <view class="menu-divider"></view>
      <view class="menu-item" @click="goToMyFavorites">
        <uv-icon name="star" color="var(--color-warning)" size="20px" />
        <text class="menu-text">我的收藏</text>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
      <view class="menu-divider"></view>
      <view class="menu-item" @click="goToMyAlbums">
        <uv-icon name="photo" color="var(--color-success)" size="20px" />
        <text class="menu-text">我的相册</text>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
      <view class="menu-divider"></view>
      <view class="menu-item" @click="goToMessage">
        <uv-icon name="bell" color="var(--color-error)" size="20px" />
        <text class="menu-text">消息通知</text>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
    </view>

    <!-- 设置入口 -->
    <view class="menu-list card">
      <view class="menu-item" @click="goToSettings">
        <uv-icon name="setting" color="var(--text-regular)" size="20px" />
        <text class="menu-text">设置</text>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.user-page {
  padding: var(--spacing-lg);
  background: var(--bg-page);
  min-height: 100vh;
}

/* 用户卡片 */
.user-card {
  .user-info {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);

    .user-detail {
      flex: 1;

      .nickname {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .bio {
        font-size: 14px;
        color: var(--text-muted);
        margin-top: 4px;

        &.tip {
          color: var(--color-primary);
        }
      }
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;
    padding-top: var(--spacing-md);
    border-top: 1px solid var(--border);

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .stat-label {
        font-size: 12px;
        color: var(--text-muted);
        margin-top: 4px;
      }
    }

    .stat-divider {
      width: 1px;
      background: var(--border);
    }
  }
}

/* VIP 卡片 */
.vip-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #0891b2, #0e7490);
  color: #ffffff;
  margin-top: var(--spacing-md);

  .vip-left {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);

    .vip-icon {
      font-size: 32px;
    }

    .vip-title {
      font-size: 16px;
      font-weight: 600;
    }

    .vip-desc {
      font-size: 12px;
      opacity: 0.8;
    }
  }

  .vip-action {
    display: flex;
    align-items: center;
    gap: 4px;

    .vip-btn {
      font-size: 14px;
      font-weight: 500;
    }
  }
}

/* 菜单列表 */
.menu-list {
  margin-top: var(--spacing-md);
  padding: 0;

  .menu-item {
    display: flex;
    align-items: center;
    padding: var(--spacing-md) var(--spacing-lg);
    gap: var(--spacing-md);

    .menu-text {
      flex: 1;
      font-size: 15px;
      color: var(--text-primary);
    }
  }

  .menu-divider {
    height: 1px;
    background: var(--border);
    margin-left: 52px;
  }
}
</style>
