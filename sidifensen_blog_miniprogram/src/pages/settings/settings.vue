<script setup>
import { ref } from 'vue'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'

const appStore = useAppStore()
const userStore = useUserStore()

// 深色模式
const isDark = ref(appStore.isDark)

// 深色模式开关
function onDarkModeChange(e) {
  const dark = e.detail.value
  appStore.setTheme(dark ? 'dark' : 'light')
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
  uni.showToast({ title: '关于页面', icon: 'none' })
}

/**
 * 跳转用户协议
 */
function goToAgreement() {
  uni.showToast({ title: '用户协议', icon: 'none' })
}

/**
 * 跳转隐私政策
 */
function goToPrivacy() {
  uni.showToast({ title: '隐私政策', icon: 'none' })
}
</script>

<template>
  <view class="settings-page">
    <!-- 主题设置 -->
    <view class="settings-group card">
      <view class="settings-title">显示</view>
      <view class="settings-item">
        <view class="settings-label">
          <text class="label-icon">&#xe8f2;</text>
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
          <text class="label-icon">&#xe8f3;</text>
          <text>关于我们</text>
        </view>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
    </view>

    <!-- 其他 -->
    <view class="settings-group card">
      <view class="settings-title">其他</view>
      <view class="settings-item" @click="goToAgreement">
        <view class="settings-label">
          <text class="label-icon">&#xe8f4;</text>
          <text>用户协议</text>
        </view>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
      <view class="settings-item" @click="goToPrivacy">
        <view class="settings-label">
          <text class="label-icon">&#xe8f5;</text>
          <text>隐私政策</text>
        </view>
        <uv-icon name="arrow-right" color="var(--text-muted)" size="16px" />
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn">
      <button @click="handleLogout">退出登录</button>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>斯蒂芬森博客 v1.0.0</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--spacing-lg);
}

.settings-group {
  margin-bottom: var(--spacing-md);
  padding: 0;

  .settings-title {
    font-size: 13px;
    color: var(--text-muted);
    padding: var(--spacing-md) var(--spacing-lg);
    border-bottom: 1px solid var(--border);
  }

  .settings-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: var(--spacing-md) var(--spacing-lg);

    &:active {
      background: var(--bg-card-hover);
    }

    .settings-label {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);

      .label-icon {
        font-size: 18px;
        color: var(--text-regular);
      }

      text {
        font-size: 15px;
        color: var(--text-primary);
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
    background: var(--bg-card);
    color: var(--color-error);
    border: none;
    border-radius: var(--radius-md);
    font-size: 15px;
  }
}

.version-info {
  text-align: center;
  margin-top: var(--spacing-xl);
  font-size: 12px;
  color: var(--text-muted);
}
</style>
