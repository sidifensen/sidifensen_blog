import "@/assets/scss/base.scss";
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";

const app = createApp(App);
import router from "./router";
app.use(router);

// 注册svg全局组件
import SvgIcon from "@/components/SvgIcon.vue";
import "virtual:svg-icons-register";
// 注册svg全局组件
app.component("svg-icon", SvgIcon);

const pinia = createPinia();
app.use(pinia);

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
app.use(ElementPlus);

import * as ElementPlusIconsVue from "@element-plus/icons-vue";
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.mount("#app");
