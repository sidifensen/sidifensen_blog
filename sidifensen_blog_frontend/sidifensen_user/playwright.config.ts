import { defineConfig, devices } from '@playwright/test'

export default defineConfig({
  // 测试基础 URL
  baseURL: 'http://localhost:7000',

  // 全局超时配置
  timeout: 30000,
  expect: {
    timeout: 10000,
  },

  // 测试目录
  testDir: './tests',

  // 输出目录
  outputDir: './test-results',

  // 全局前置/后置钩子
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',

  // 全局环境变量
  use: {
    // 基础 URL
    baseURL: 'http://localhost:7000',

    // 追踪
    trace: 'on-first-retry',

    // 截图
    screenshot: 'only-on-failure',

    // 视频
    video: 'retain-on-failure',
  },

  // 项目配置
  projects: [
    // Chromium
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    // Firefox
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    // WebKit
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    // 移动端模拟
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
  ],

  // Web 服务器配置（自动启动开发服务器）
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:7000',
    reuseExistingServer: true,
    timeout: 120000,
  },
})
