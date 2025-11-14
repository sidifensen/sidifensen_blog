import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
// import vueDevTools from "vite-plugin-vue-devtools"; // 已禁用，避免在构建环境中访问 localStorage

import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";

// 代码混淆
import { viteObfuscateFile } from "vite-plugin-obfuscator";

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: "0.0.0.0",
    port: 8000,
    open: true,
  },
  plugins: [
    vue(),
    // vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver({ importStyle: "css" })],
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: "css" })],
    }),
    createSvgIconsPlugin({
      //这行代码的作用是将项目根目录下的 src/assets/svg 目录作为图标文件的查找目录
      //path.resolve() 用于解析为绝对路径，process.cwd() 表示当前工作目录，即项目的根目录
      iconDirs: [path.resolve(process.cwd(), "src/assets/svg")],
      symbolId: "[name]", //'[name]' 是一个占位符，表示使用 SVG 文件的名称作为符号 ID
    }),
    // // 生产环境启用代码混淆
    // ...(process.env.NODE_ENV === "production"
    //   ? [
    //       viteObfuscateFile({
    //         // 混淆选项 - 调整为更温和的设置以避免样式问题
    //         compact: false, // 压缩代码
    //         controlFlowFlattening: false, // 禁用控制流扁平化，避免破坏样式逻辑
    //         deadCodeInjection: false, // 不注入死代码
    //         debugProtection: false, // 不启用调试保护
    //         disableConsoleOutput: true, // 禁用console输出
    //         identifierNamesGenerator: "hexadecimal", // 标识符生成方式
    //         renameGlobals: false, // 不重命名全局变量
    //         rotateStringArray: false, // 旋转字符串数组
    //         selfDefending: false, // 启用自保护
    //         stringArray: false, // 字符串数组
    //         stringArrayEncoding: ["base64"], // 字符串数组编码
    //         stringArrayThreshold: 0.75, // 字符串数组阈值
    //         transformObjectKeys: false, // 不转换对象键，避免影响样式类名
    //         unicodeEscapeSequence: false, // 不使用Unicode转义序列
    //         // 排除disableDevtool.js文件
    //         exclude: ['**/disableDevtool.js']
    //       }),
    //     ]
    //   : []),
  ],
  resolve: {
    alias: {
      "@": path.resolve(process.cwd(), "src"),
    },
  },
  base: "/",
  build: {
    // 调整chunk大小警告阈值为1000kB
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        assetFileNames: (assetInfo) => {
          let extType = assetInfo.name.split(".").at(1);
          if (/png|jpe?g|svg|gif|tiff|bmp|ico/i.test(extType)) {
            extType = "img";
          }
          return `assets/${extType}/[name]-[hash][extname]`;
        },
        chunkFileNames: "assets/js/[name]-[hash].js",
        entryFileNames: "assets/js/[name]-[hash].js",
      },
    },
  },
});
