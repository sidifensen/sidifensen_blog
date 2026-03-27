<script setup>
/**
 * 自定义底部导航栏组件
 * 使用 SVG 图标实现，兼容 H5 和小程序
 */
import { ref } from 'vue'

// 导航项配置
const tabList = [
  {
    pagePath: '/pages/index/index',
    text: '首页',
    icon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/></svg>`,
    activeIcon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/></svg>`
  },
  {
    pagePath: '/pages/album/album',
    text: '相册',
    icon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M22 16V4c0-1.1-.9-2-2-2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2zm-11-4l2.03 2.71L16 11l4 5H8l3-4zM2 6v14c0 1.1.9 2 2 2h14v-2H4V6H2z"/></svg>`,
    activeIcon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M22 16V4c0-1.1-.9-2-2-2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2zm-11-4l2.03 2.71L16 11l4 5H8l3-4zM2 6v14c0 1.1.9 2 2 2h14v-2H4V6H2z"/></svg>`
  },
  {
    pagePath: '/pages/user/user',
    text: '我的',
    icon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>`,
    activeIcon: `<svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>`
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
    uni.switchTab({
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
        <view class="tabbar-icon" :class="{ active: currentIndex === index }">
          <view v-if="currentIndex === index" v-html="item.activeIcon"></view>
          <view v-else v-html="item.icon"></view>
        </view>
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
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
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
}

.tabbar-icon {
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;

  &.active {
    color: #0891b2;
  }
}

.tabbar-text {
  font-size: 12px;
  color: #64748b;

  .active & {
    color: #0891b2;
  }
}
</style>
