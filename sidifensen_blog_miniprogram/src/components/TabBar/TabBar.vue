<script setup>
/**
 * 自定义底部导航栏组件
 */
import { ref, onMounted } from 'vue'

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

// 底部安全区域高度
const safeAreaBottom = ref(0)

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

// 初始化时获取当前页面和安全区域
onMounted(() => {
  updateCurrentIndex()
  // 获取安全区域信息
  const systemInfo = uni.getSystemInfoSync()
  if (systemInfo.safeAreaInsets) {
    safeAreaBottom.value = systemInfo.safeAreaInsets.bottom || 0
  }
})

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
  <view class="custom-tabbar" :style="{ paddingBottom: safeAreaBottom + 'px' }">
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
  // 使用 CSS 变量支持深色模式，同时保留回退值
  background: var(--u-bg-color, #ffffff);
  border-top: 1px solid var(--u-border-color, #e2e8f0);
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

  .tabbar-text {
    font-size: 12px;
    color: var(--u-tips-color, #64748b);
    transition: color 0.3s;
  }

  &.active .tabbar-text {
    color: var(--u-type-primary, #0891b2);
  }
}
</style>
