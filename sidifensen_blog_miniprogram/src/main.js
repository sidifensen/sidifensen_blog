import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore } from './store/user'
import uviewPro from 'uview-pro'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  app.use(pinia)
  app.use(uviewPro)

  // 初始化用户登录状态
  const userStore = useUserStore()
  userStore.checkLogin()

  return {
    app,
    pinia
  }
}
