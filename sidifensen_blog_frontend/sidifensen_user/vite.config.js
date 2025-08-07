import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";

export default defineConfig({
  server: {
    host: "0.0.0.0",
    port: 7000,
    open: true,
  },
  plugins: [
    // element-plus自动导入
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    // element-plus组件自动注册
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    vue(),
    // vueDevTools(),
    // 配置自定义icon
    createSvgIconsPlugin({
      //这行代码的作用是将项目根目录下的 src/assets/svg 目录作为图标文件的查找目录
      //path.resolve() 用于解析为绝对路径，process.cwd() 表示当前工作目录，即项目的根目录
      iconDirs: [path.resolve(process.cwd(), "src/assets/svg")],
      symbolId: "[name]", //'[name]' 是一个占位符，表示使用 SVG 文件的名称作为符号 ID
    }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
});
