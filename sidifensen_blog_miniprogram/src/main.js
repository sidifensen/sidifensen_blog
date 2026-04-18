import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useUserStore } from './store/user'
import uviewPro from 'uview-pro'

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()
  let uviewOptions

  // #ifdef MP-WEIXIN
  uviewOptions = {
    theme: {
      // 微信小程序端固定亮色，避免真机受系统主题或本地缓存影响出现灰底。
      defaultDarkMode: 'light',
      isForce: true
    }
  }
  // #endif

  app.use(pinia)
  app.use(uviewPro, uviewOptions)

  // 初始化用户登录状态
  const userStore = useUserStore()
  userStore.checkLogin()

  return {
    app,
    pinia
  }
}
