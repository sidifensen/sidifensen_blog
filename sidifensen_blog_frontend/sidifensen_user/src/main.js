import "@/assets/scss/base.scss";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// 引入 ElementPlus 样式
import "element-plus/dist/index.css";

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

// 注册svg全局组件
import SvgIcon from "@/components/SvgIcon.vue";
import "virtual:svg-icons-register";

// pinia持久化插件
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 夜间模式
import "element-plus/theme-chalk/dark/css-vars.css";

const app = createApp(App);

import { enableAntiDebug } from '@/utils/antiDebug';
// 启用防调试功能
// enableAntiDebug();

// 引入并配置disable-devtool
import { setupDisableDevtool } from '@/utils/disableDevtool';
setupDisableDevtool();

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册svg全局组件
app.component("svg-icon", SvgIcon);

/* - 用途 ： 
 app.config.globalProperties 主要用于添加全局属性，方便在组件中访问全局工具和数据；
 app.component() 用于全局注册组件，让组件可以在整个应用中直接使用。
- 使用场景 ：如果需要在多个组件中共享工具函数、API 实例等，使用 app.config.globalProperties ；
如果有一些通用组件需要在多个地方使用，使用 app.component() 进行全局注册。 */

app.use(pinia);
app.use(router);

// 添加路由守卫来动态修改页面标题
// router.beforeEach((to, from, next) => {
//   if (to.meta.title) {
//     document.title = to.meta.title;
//   }
//   next();
// });

// 引入 darkStore
import { useDarkStore } from "./stores/darkStore";
const darkStore = useDarkStore();
darkStore.initDarkMode();

app.mount("#app");
