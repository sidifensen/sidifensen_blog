import "@/assets/scss/base.scss";
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";

const app = createApp(App);
import router from "./router";
app.use(router);

// 引入并配置disable-devtool
// 只在生产环境启用禁用开发者工具功能
import { setupDisableDevtool } from "@/utils/disableDevtool";
if (import.meta.env.PROD) {
  // setupDisableDevtool();
}

// 注册svg全局组件
import SvgIcon from "@/components/SvgIcon.vue";
import "virtual:svg-icons-register";
// 注册svg全局组件
app.component("svg-icon", SvgIcon);

// 夜间模式
import "element-plus/theme-chalk/dark/css-vars.css";

// pinia持久化插件
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
app.use(pinia);

import ElementPlus from "element-plus";

// 导入中文语言包
import zhCn from "element-plus/dist/locale/zh-cn.mjs";
app.use(ElementPlus, {
  locale: zhCn,
});

import * as ElementPlusIconsVue from "@element-plus/icons-vue";
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 引入 darkStore
import { useDarkStore } from "./stores/darkStore";
const darkStore = useDarkStore();
darkStore.initDarkMode();

app.mount("#app");
