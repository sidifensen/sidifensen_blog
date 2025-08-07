import "@/assets/scss/base.scss";
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

const app = createApp(App);

import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
app.use(ElementPlus);

const pinia = createPinia();

import * as ElementPlusIconsVue from "@element-plus/icons-vue";
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(pinia);
app.use(router);

app.mount("#app");
