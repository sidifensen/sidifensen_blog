<script setup>
/**
 * 自定义底部导航栏组件
 */
import { ref } from 'vue'

// 导航项配置
const tabList = [
  {
    pagePath: '/pages/index/index',
    text: '首页',
    icon: 'home',
    activeIcon: 'home-fill'
  },
  {
    pagePath: '/pages/album/album',
    text: '相册',
    icon: 'photo',
    activeIcon: 'photo-fill'
  },
  {
    pagePath: '/pages/user/user',
    text: '我的',
    icon: 'account',
    activeIcon: 'account-fill'
  }
]

// 当前选中索引
const currentIndex = ref(0)

// 根据当前页面路径更新选中状态
function updateCurrentIndex() {
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1]
    const path = '/' + currentPage.route
    const index = tabList.findIndex(item => item.pagePath === path)
    if (index !== -1) {
      currentIndex.value = index
    }
  }
}

// 初始化时获取当前页面
updateCurrentIndex()

// 切换标签
function onTabChange(index) {
  if (index !== currentIndex.value) {
    currentIndex.value = index
    const item = tabList[index]
    uni.reLaunch({
      url: item.pagePath
    })
  }
}
</script>

<template>
  <view class="custom-tabbar">
    <view class="tabbar-content">
      <view
        v-for="(item, index) in tabList"
        :key="index"
        class="tabbar-item"
        :class="{ active: currentIndex === index }"
        @click="onTabChange(index)"
      >
        <u-icon :name="currentIndex === index ? item.activeIcon : item.icon" :size="22" />
        <text class="tabbar-text">{{ item.text }}</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.custom-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: var(--u-bg-white);
  border-top: 1px solid var(--u-border-color);
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

.tabbar-content {
  display: flex;
  height: 50px;
  align-items: center;
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;

  .tabbar-text {
    font-size: 12px;
    color: var(--u-tips-color);
    transition: color 0.3s;
  }

  &.active .tabbar-text {
    color: var(--u-type-primary);
  }
}
</style>
