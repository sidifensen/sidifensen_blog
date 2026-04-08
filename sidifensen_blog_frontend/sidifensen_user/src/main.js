import '@/assets/scss/base.scss'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 引入 ElementPlus 样式
// import "element-plus/dist/index.css";

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// pinia持久化插件
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 夜间模式
import 'element-plus/theme-chalk/dark/css-vars.css'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册svg全局组件
import SvgIcon from '@/components/SvgIcon.vue'
import 'virtual:svg-icons-register'
app.component('svg-icon', SvgIcon)

// 注册页面加载动画组件
import PageLoading from '@/components/PageLoading.vue'
app.component('PageLoading', PageLoading)

// 注册加载动画组件（部分页面使用）
import LoadingAnimation from '@/components/LoadingAnimation.vue'
app.component('LoadingAnimation', LoadingAnimation)

app.use(pinia)
app.use(router)

// 引入 darkStore
import { useDarkStore } from './stores/darkStore'
const darkStore = useDarkStore()
darkStore.initDarkMode()

app.mount('#app')
