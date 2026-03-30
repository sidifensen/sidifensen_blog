<script setup>
import { computed } from 'vue'
import { useTheme } from 'uview-pro'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const { darkMode, toggleDarkMode } = useTheme()

// 图标大小
const iconSize = '18px'

// 深色模式（响应式）
const isDark = computed(() => darkMode.value === 'dark')

// 深色模式开关
function onDarkModeChange(e) {
  toggleDarkMode()
}

/**
 * 退出登录
 */
function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/index/index' })
        }, 1500)
      }
    }
  })
}

/**
 * 跳转关于页面
 */
function goToAbout() {
  uni.navigateTo({ url: '/pages/settings/about' })
}

/**
 * 跳转用户协议
 */
function goToAgreement() {
  uni.navigateTo({ url: '/pages/settings/agreement' })
}

/**
 * 跳转隐私政策
 */
function goToPrivacy() {
  uni.navigateTo({ url: '/pages/settings/privacy' })
}
</script>

<template>
  <view class="settings-page">
    <!-- 主题设置 -->
    <view class="settings-group card">
      <view class="settings-title">显示</view>
      <view class="settings-item">
        <view class="settings-label">
          <u-icon name="setting" :size="iconSize" color="var(--u-content-color)" />
          <text>深色模式</text>
        </view>
        <switch
          :checked="isDark"
          @change="onDarkModeChange"
        />
      </view>
    </view>

    <!-- 账号设置 -->
    <view class="settings-group card">
      <view class="settings-title">账号</view>
      <view class="settings-item" @click="goToAbout">
        <view class="settings-label">
          <u-icon name="file-text" :size="iconSize" color="var(--u-content-color)" />
          <text>关于我们</text>
        </view>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
    </view>

    <!-- 其他 -->
    <view class="settings-group card">
      <view class="settings-title">其他</view>
      <view class="settings-item" @click="goToAgreement">
        <view class="settings-label">
          <u-icon name="file-text" :size="iconSize" color="var(--u-content-color)" />
          <text>用户协议</text>
        </view>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
      <view class="settings-item" @click="goToPrivacy">
        <view class="settings-label">
          <u-icon name="file-text" :size="iconSize" color="var(--u-content-color)" />
          <text>隐私政策</text>
        </view>
        <u-icon name="arrow-right" color="var(--u-tips-color)" size="16px" />
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn">
      <button @click="handleLogout">退出登录</button>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>斯蒂芬森助手 v1.0.0</text>
      <text class="icp">粤 ICP 备 2024324512 号 -2</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding: var(--spacing-lg);
}

.settings-group {
  margin-bottom: var(--spacing-md);
  padding: 0;

  .settings-title {
    font-size: 13px;
    color: var(--u-tips-color);
    padding: var(--spacing-md) var(--spacing-lg);
    border-bottom: 1px solid var(--u-border-color);
  }

  .settings-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: var(--spacing-md) var(--spacing-lg);

    &:active {
      background: var(--u-bg-gray-light);
    }

    .settings-label {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);

      text {
        font-size: 15px;
        color: var(--u-main-color);
      }
    }
  }
}

.logout-btn {
  margin-top: var(--spacing-xl);
  padding: 0 var(--spacing-lg);

  button {
    width: 100%;
    height: 44px;
    line-height: 44px;
    background: var(--u-bg-white);
    color: var(--u-type-error);
    border: none;
    border-radius: var(--radius-md);
    font-size: 15px;
  }
}

.version-info {
  text-align: center;
  margin-top: var(--spacing-xl);
  font-size: 12px;
  color: var(--u-tips-color);

  .icp {
    display: block;
    margin-top: var(--spacing-xs);
  }
}
</style>
