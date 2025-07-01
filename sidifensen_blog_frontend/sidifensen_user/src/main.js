import '@/assets/scss/base.scss'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// 注册svg全局组件
import SvgIcon from '@/components/SvgIcon/index.vue';
import 'virtual:svg-icons-register';
// 注册全局工具
import VueCookies from 'vue-cookies';
import Request from '@/utils/Request';
import Message from '@/utils/Message';

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册svg全局组件
app.component('svg-icon', SvgIcon);

/* - 用途 ： 
 app.config.globalProperties 主要用于添加全局属性，方便在组件中访问全局工具和数据；
 app.component() 用于全局注册组件，让组件可以在整个应用中直接使用。
- 使用场景 ：如果需要在多个组件中共享工具函数、API 实例等，使用 app.config.globalProperties ；
如果有一些通用组件需要在多个地方使用，使用 app.component() 进行全局注册。 */
app.config.globalProperties.VueCookies = VueCookies;
app.config.globalProperties.Request = Request;
app.config.globalProperties.Message = Message;

app.use(createPinia())
app.use(router)

app.mount('#app')
