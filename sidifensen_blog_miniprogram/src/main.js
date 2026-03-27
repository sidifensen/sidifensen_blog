import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore } from './store/user'
// uv-ui components are loaded via easycom in pages.json
// import 'uv-ui/es/style.css'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  app.use(pinia)

  // 初始化用户登录状态
  const userStore = useUserStore()
  userStore.checkLogin()

  return {
    app,
    pinia
  }
}
