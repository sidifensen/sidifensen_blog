import { defineConfig } from "vite";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import JavaScriptObfuscator from "javascript-obfuscator";
import vue from "@vitejs/plugin-vue";

import path from "path";
import { createSvgSpritePlugin } from "./src/utils/svgSpritePlugin";

const normalizePath = (filePath) => filePath.replaceAll("\\", "/");

const matchesExcludePattern = (filePath, pattern) => {
  const normalizedPath = normalizePath(filePath);
  const normalizedPattern = normalizePath(pattern);

  if (normalizedPattern.startsWith("**/")) {
    return normalizedPath.endsWith(normalizedPattern.slice(3));
  }

  if (normalizedPattern.includes("*")) {
    const plainPattern = normalizedPattern.replaceAll("*", "");
    return plainPattern ? normalizedPath.includes(plainPattern) : false;
  }

  return normalizedPath.endsWith(normalizedPattern);
};

// 生产构建阶段对 JS chunk 做混淆，避免继续依赖已过时的 Vite 插件实现
const createObfuscatorPlugin = (options = {}) => {
  const { exclude = [], ...obfuscatorOptions } = options;

  return {
    name: "vite:obfuscatefiles",
    apply: "build",
    generateBundle(_, bundle) {
      for (const [fileName, chunk] of Object.entries(bundle)) {
        if (chunk.type !== "chunk" || !chunk.code) {
          continue;
        }

        const sourceIds = [chunk.facadeModuleId, ...(chunk.moduleIds || [])].filter(Boolean);
        const shouldExclude = exclude.some((pattern) => {
          return sourceIds.some((sourceId) => matchesExcludePattern(sourceId, pattern));
        });

        if (shouldExclude) {
          continue;
        }

        chunk.code = JavaScriptObfuscator.obfuscate(chunk.code, {
          ...obfuscatorOptions,
          inputFileName: fileName,
        }).getObfuscatedCode();
      }
    },
  };
};

export default defineConfig(({ mode }) => ({
  server: {
    host: "0.0.0.0",
    port: 7000,
    open: true,
  },
  base: '/',
  plugins: [
    vue(),
    // vueDevTools(),
    // element-plus自动导入
    // 优化Element Plus的导入，指定样式导入方式为css
    AutoImport({
      resolvers: [ElementPlusResolver({ importStyle: "css" })],
      dts: false,
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: "css" })],
      dts: false,
    }),
    // 配置自定义icon
    createSvgSpritePlugin({
      // 这里将 src/assets/svg 目录注册为 svg 精灵图的来源目录
      iconDirs: [path.resolve(process.cwd(), "src/assets/svg")],
      // [name] 表示直接使用 SVG 文件名作为 symbol id
      symbolId: "[name]",
    }),
    // 暂时禁用代码混淆以排查问题
    // ...(process.env.NODE_ENV === "production"
    //   ? [
    //       createObfuscatorPlugin({
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
    //         exclude: ["**/disableDevtool.js"],
    //       }),
    //     ]
    //   : []),
  ],
  resolve: {
    alias: {
      "@": path.resolve(process.cwd(), "src"),
    },
  },
  build: {
    chunkSizeWarningLimit: 2000,
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
        manualChunks: {
          // Element Plus 单独打包
          'element-plus': ['element-plus'],
          // Vue 核心库单独打包
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
        },
      },
    },
  },
}))
