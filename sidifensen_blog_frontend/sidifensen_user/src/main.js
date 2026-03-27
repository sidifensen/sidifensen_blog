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

// 状态栏高度适配
import { StatusBar, Style } from "@capacitor/status-bar";

const app = createApp(App);

// 设置状态栏样式并获取高度
const setupStatusBar = async () => {
  try {
    await StatusBar.setStyle({ style: Style.Dark });
    await StatusBar.setBackgroundColor({ color: "#ffffff" });
  } catch (e) {
    // 非 Capacitor 环境忽略
  }
};
setupStatusBar();

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册svg全局组件
import SvgIcon from "@/components/SvgIcon.vue";
import "virtual:svg-icons-register";
app.component("svg-icon", SvgIcon);

// 注册加载动画组件
import LoadingAnimation from "@/components/LoadingAnimation.vue";
app.component("LoadingAnimation", LoadingAnimation);

app.use(pinia);
app.use(router);

// 引入 darkStore
import { useDarkStore } from "./stores/darkStore";
const darkStore = useDarkStore();
darkStore.initDarkMode();

app.mount("#app");
