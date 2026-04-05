<template>
  <router-view v-slot="{ Component }">
    <transition name="scale" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <!-- 智能客服悬浮按钮 -->
  <CustomerServiceFloatButton @click="customerServiceChatRef?.open()" />

  <!-- 智能客服对话窗口 -->
  <CustomerServiceChat ref="customerServiceChatRef" />
</template>

<script setup>
import { ref } from 'vue'
import CustomerServiceFloatButton from '@/components/CustomerServiceFloatButton.vue'
import CustomerServiceChat from '@/components/CustomerServiceChat.vue'

const customerServiceChatRef = ref(null)
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
