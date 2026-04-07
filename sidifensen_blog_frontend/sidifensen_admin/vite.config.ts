// VitePlus 统一工具链配置
import { defineConfig } from 'vite-plus'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'
import { createSvgSpritePlugin } from './src/utils/svgSpritePlugin.js'
import { fileURLToPath } from 'url'
import { dirname } from 'path'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

export default defineConfig({
  // 标准 Vite 配置
  base: '/',
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver({ importStyle: 'css' })],
      dts: false,
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: 'css' })],
      dts: false,
    }),
    createSvgSpritePlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/svg')],
      symbolId: '[name]',
    }),
  ],
  server: {
    host: '0.0.0.0',
    port: 8000,
    open: true,
  },
  build: {
    chunkSizeWarningLimit: 2000,
    rollupOptions: {
      output: {
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
      },
    },
  },

  // ================================================
  // VitePlus 扩展配置
  // ================================================

  // 格式化配置 (Oxfmt)
  // 控制代码风格、缩进、引号等格式规则
  fmt: {
    // 忽略检查的文件/目录（通常为构建产物或第三方代码）
    ignorePatterns: [
      'dist/**', // 构建输出目录
      'node_modules/**', // 第三方依赖
      '*.min.js', // 压缩的 JS 文件
      '.env', // 环境变量文件
      '.env.*', // 环境变量变体（如 .env.local）
      'coverage/**', // 测试覆盖率报告
      'public/**', // 静态资源目录
    ],

    // 单行字符数限制，超过则换行（默认 100）
    printWidth: 100,
    // 缩进宽度（默认 2）
    tabWidth: 2,
    // 是否使用 Tab 缩进（默认 false，使用空格）
    useTabs: false,
    // 是否使用单引号（默认 true）
    singleQuote: true,
    // 是否添加分号（默认 false）
    semi: false,
    // 行尾逗号模式：'all' | 'es5' | 'none'（默认 'all'）
    trailingComma: 'all',
    // 文件末尾是否添加空行（默认 true）
    insertFinalNewline: true,
    // 是否在 JSX 属性中使用双引号（默认 false）
    jsxSingleQuote: false,
    // Markdown 文本换行模式：'preserve' | 'always' | 'never'（默认 'preserve'）
    proseWrap: 'preserve',
    // 行尾符：'lf' | 'crlf' | 'auto'（默认 'lf'，Unix 风格）
    endOfLine: 'lf',
  },

  // 代码检查配置 (Oxlint)
  // 控制 ESLint 规则和检查行为
  lint: {
    // 忽略检查的文件/目录
    ignorePatterns: [
      'dist/**', // 构建输出目录
      'node_modules/**', // 第三方依赖
    ],

    // 是否将警告视为错误（默认 false）
    // warningsAsErrors: false,

    // 规则覆盖，针对特定文件或目录定制规则
    // ruleOverrides: [
    //   // 对测试文件放宽某些规则
    //   {
    //     files: ['**/*.spec.js', '**/*.test.js'],
    //     rules: {
    //       'no-unused-vars': 'off',
    //     },
    //   },
    // ],
  },

  // 测试配置 (Vitest)
  test: {
    environment: 'jsdom',
    globals: true,
    css: true,
    setupFiles: ['./src/tests/setup.js'],
  },

  // Git 暂存区检查配置
  staged: {
    '*': 'vp check --fix',
  },
})
