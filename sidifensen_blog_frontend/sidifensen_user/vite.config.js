import { defineConfig } from "vite";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import JavaScriptObfuscator from "javascript-obfuscator";
import { VitePWA } from "vite-plugin-pwa";

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
  base: mode === 'electron' ? './' : '/',
  plugins: [
    vue(),
    // Electron 模式下禁用 PWA/Service Worker，避免缓存干扰 API 请求
    ...(mode !== 'electron' ? [
      VitePWA({
        registerType: "autoUpdate",
        includeAssets: ["favicon.ico", "icons/*.png"],
        manifest: {
          name: "斯蒂芬森社区",
          short_name: "斯蒂芬森",
          description: "斯蒂芬森社区",
          theme_color: "#ffffff",
          background_color: "#ffffff",
          display: "standalone",
          orientation: "portrait",
          scope: "/",
          start_url: "/",
          icons: [
            { "src": "/icons/web-app-manifest-192x192.png", "sizes": "192x192", "type": "image/png" },
            { "src": "/icons/web-app-manifest-512x512.png", "sizes": "512x512", "type": "image/png" },
            { "src": "/icons/web-app-manifest-512x512.png", "sizes": "512x512", "type": "image/png", "purpose": "maskable" },
            { "src": "/icons/apple-touch-icon.png", "sizes": "180x180", "type": "image/png", "purpose": "any" }
          ]
        },
        workbox: {
          globPatterns: ["**/*.{js,css,html,ico,png,svg,woff2}"],
          maximumFileSizeToCacheInBytes: 5 * 1024 * 1024,
          runtimeCaching: [
            {
              urlPattern: /^https:\/\/fonts\.googleapis\.com\/.*/i,
              handler: "CacheFirst",
              options: {
                cacheName: "google-fonts-cache",
                expiration: { maxEntries: 10, maxAgeSeconds: 60 * 60 * 24 * 365 },
              },
            },
            {
              urlPattern: /^https:\/\/fonts\.gstatic\.com\/.*/i,
              handler: "CacheFirst",
              options: {
                cacheName: "gstatic-fonts-cache",
                expiration: { maxEntries: 10, maxAgeSeconds: 60 * 60 * 24 * 365 },
              },
            },
            {
              urlPattern: /\/api\/.*/i,
              handler: "NetworkOnly",
              options: {
                cacheName: "api-cache",
                expiration: { maxEntries: 0 },
              },
            },
          ],
        },
      }),
    ] : []),
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
}))
