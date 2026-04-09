<script setup>
import { RouterView } from 'vue-router'
import router from './router'

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
</script>

<template>
  <RouterView />
</template>

<style lang="scss" scoped></style>
