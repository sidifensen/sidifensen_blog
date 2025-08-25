import "@/assets/scss/base.scss";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// 引入 ElementPlus 样式
// import "element-plus/dist/index.css";

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";


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
// 启用禁用开发者工具功能
// setupDisableDevtool();

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册svg全局组件
import SvgIcon from "@/components/SvgIcon.vue";
import "virtual:svg-icons-register";
app.component("svg-icon", SvgIcon);

// 注册加载动画组件
import LoadingAnimation from '@/components/LoadingAnimation.vue';
app.component('LoadingAnimation', LoadingAnimation);


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
