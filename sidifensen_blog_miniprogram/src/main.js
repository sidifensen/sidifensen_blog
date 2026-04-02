import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore } from './store/user'
import { useAppStore } from './store/app'
import uviewPro from 'uview-pro'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  app.use(pinia)
  app.use(uviewPro)

  // 初始化用户登录状态
  const userStore = useUserStore()
  userStore.checkLogin()

  // 初始化主题状态
  const appStore = useAppStore()
  appStore.initTheme()

  return {
    app,
    pinia
  }
}
