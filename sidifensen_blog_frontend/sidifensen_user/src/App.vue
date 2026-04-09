<template>
  <router-view v-slot="{ Component }">
    <transition name="scale" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <!-- 智能客服悬浮按钮 -->
  <CustomerServiceFloatButton @click="handleCustomerServiceClick" />

  <!-- 智能客服对话窗口 -->
  <CustomerServiceChat ref="customerServiceChatRef" />
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import CustomerServiceFloatButton from '@/components/CustomerServiceFloatButton.vue'
import CustomerServiceChat from '@/components/CustomerServiceChat.vue'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const customerServiceChatRef = ref(null)

// 首屏加载动画：等 Vue Router 完成初始路由解析（包括所有懒加载 chunk 下载）后再移除
router.isReady().then(() => {
  // 给 Vue 一点时间开始渲染，再淡出加载动画，实现平滑衔接
  setTimeout(() => {
    const loadingEl = document.getElementById('app-loading')
    if (loadingEl) {
      loadingEl.style.opacity = '0'
      // 等 CSS transition 完成后移除 DOM
      setTimeout(() => loadingEl.remove(), 400)
    }
  }, 50)
})

// 处理客服按钮点击
const handleCustomerServiceClick = () => {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('智能客服需要登录后才能使用，是否前往登录？', '提示', {
      confirmButtonText: '前往登录',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        router.push('/login')
      })
      .catch(() => {})
    return
  }
  customerServiceChatRef.value?.open()
}
</script>

<style lang="scss">
// 文章广场页面专属背景覆盖 - 黑夜模式下覆盖全局背景色
.article-page-body {
  min-height: 100vh;
}

html.dark .article-page-body {
  --el-bg-color-page: #000000 !important;
  background-color: #000000 !important;
}

// 个人主页背景覆盖 - 黑夜模式下覆盖全局背景色
html.dark .user-homepage {
  background-color: #0f172a !important;
}
</style>
