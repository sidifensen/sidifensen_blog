<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { watch, onMounted } from 'vue'
import { useTheme } from 'uview-pro'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'

const userStore = useUserStore()
const appStore = useAppStore()
const { darkMode } = useTheme()

onLaunch(() => {
  // 检查登录状态
  userStore.checkLogin()
})

onShow(() => {
})

onHide(() => {
})

// 监听主题变化，更新导航栏和 TabBar 样式
watch(darkMode, (newVal) => {
  const isDark = newVal === 'dark'
  appStore.initTheme()

  // 更新原生导航栏颜色（仅支持 H5 和部分小程序）
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  if (currentPage) {
    // H5 环境直接操作 DOM
    if (typeof window !== 'undefined') {
      const pageHead = document.querySelector('.uni-page-head')
      if (pageHead) {
        pageHead.style.backgroundColor = isDark ? '#111827' : '#ffffff'
        pageHead.style.color = isDark ? '#ffffff' : '#000000'
        const title = pageHead.querySelector('.uni-page-head__title')
        if (title) {
          title.style.color = isDark ? '#ffffff' : '#000000'
        }
      }
    }
  }
}, { immediate: true })
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import '@/styles/common.scss';
/* uView Pro 全局样式 - CSS 变量由 uview-pro 的 JS 在运行时动态设置在 :root 上 */
@import 'uview-pro/index.scss';
</style>
