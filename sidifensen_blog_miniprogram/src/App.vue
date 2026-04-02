<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onLaunch(() => {
  // 检查登录状态
  userStore.checkLogin()
})

onShow(() => {
})

onHide(() => {
})

// TabBar 页面列表
const tabBarPages = ['pages/index/index', 'pages/album/album', 'pages/user/user']

// 更新导航栏和 TabBar 主题色
function updateNavigationBarAndTabBarTheme() {
  const html = document.documentElement
  const isDark = html.classList.contains('u-theme-dark')

  // 更新导航栏颜色 - 直接操作 DOM
  const pageHead = document.querySelector('.uni-page-head')
  if (pageHead) {
    if (isDark) {
      pageHead.style.backgroundColor = '#111827'
      pageHead.style.color = '#ffffff'
      const title = pageHead.querySelector('.uni-page-head__title')
      if (title) {
        title.style.color = '#ffffff'
      }
    } else {
      pageHead.style.backgroundColor = '#ffffff'
      pageHead.style.color = '#000000'
      const title = pageHead.querySelector('.uni-page-head__title')
      if (title) {
        title.style.color = '#000000'
      }
    }
  }

  // 获取当前页面路径
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const currentPath = currentPage?.route || ''

  // 只在 TabBar 页面更新 TabBar 样式
  if (tabBarPages.some(page => currentPath.includes(page))) {
    if (isDark) {
      uni.setTabBarStyle({
        color: '#9aa1af',
        selectedColor: '#8ab4ff',
        backgroundColor: '#111827',
        borderStyle: 'black'
      })
    } else {
      uni.setTabBarStyle({
        color: '#64748b',
        selectedColor: '#0891b2',
        backgroundColor: '#ffffff',
        borderStyle: 'black'
      })
    }
  }
}

// 监听 HTML 元素的 class 变化，自动更新导航栏和 tabbar
if (typeof window !== 'undefined') {
  // 监听主题 class 变化
  const classObserver = new MutationObserver(() => {
    updateNavigationBarAndTabBarTheme()
  })
  classObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })

  // 监听页面切换（uni-page-head 的出现）
  const pageObserver = new MutationObserver((mutations) => {
    mutations.forEach(mutation => {
      mutation.addedNodes.forEach(node => {
        if (node.nodeType === 1 && (node.classList?.contains('uni-page-head') || node.querySelector?.('.uni-page-head'))) {
          updateNavigationBarAndTabBarTheme()
        }
      })
    })
  })

  // 初始同步并开始观察
  const initAndObserve = () => {
    updateNavigationBarAndTabBarTheme()
    // 观察 body 下新增的 uni-page-head
    pageObserver.observe(document.body, { childList: true, subtree: true })
  }

  // 使用 requestAnimationFrame 确保在下一帧前执行
  requestAnimationFrame(initAndObserve)

  // 监听页面显示事件（页面切换时）
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
      setTimeout(updateNavigationBarAndTabBarTheme, 100)
    }
  })
}
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import '@/styles/common.scss';
/* uView Pro 全局样式 - CSS 变量由 uview-pro 的 JS 在运行时动态设置在 :root 上 */
@import 'uview-pro/index.scss';
</style>
