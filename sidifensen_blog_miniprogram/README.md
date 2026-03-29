# 斯蒂芬森博客微信小程序

斯蒂芬森博客是一款个人博客小程序，提供文章阅读服务。内容涵盖编程技术、生活随笔。

斯蒂芬森博客微信小程序端，基于 UniApp + Vue3 开发。

## 技术栈

- **框架**: UniApp + Vue3 (Composition API)
- **状态管理**: Pinia
- **UI 组件**: uView Pro
- **样式**: SCSS
- **构建**: Vite

## 开发命令

```bash
# 安装依赖
npm install

# 运行微信小程序
npm run dev:mp-weixin

# 运行 H5
npm run dev:h5

# 构建微信小程序
npm run build:mp-weixin

# 构建 H5
npm run build:h5

# 构建 App（uni-app CLI）
npx uni build -p app
```

## App 打包说明

### CLI 打包（推荐）

uni-app CLI 打包不依赖 HBuilderX，使用系统 Node.js：

```bash
# 进入项目目录
cd sidifensen_blog_miniprogram

# 安装依赖
npm install

# 构建 App（WebView 资源包）
npx uni build -p app
```

构建产物在 `dist/build/app/` 目录。

### 生成 APK

1. 下载安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)
2. 安装 [Android Studio](https://developer.android.google.cn/studio)
3. 将 `dist/build/app/` 导入 Android Studio
4. 配置签名并 Build APK

### HBuilderX 打包注意事项

1. HBuilderX 使用自带的 Node.js 环境，可能与系统 Node 不兼容
2. 如遇 `node_modules缺少编译器模块`，在项目目录执行 `npm install`
3. AppID 需要在 [DCloud 开发者中心](https://dev.dcloud.net.cn) 创建应用获取
4. 路径包含空格的解决方法：创建软链接 `mklink /D C:\nodejs "D:\Program Files\nodejs"`

## 项目结构

```
src/
├── api/                # API 请求模块
├── components/         # 公共组件
├── pages/              # 页面
├── static/             # 静态资源
├── store/              # 状态管理
├── styles/             # 全局样式
├── utils/              # 工具函数
├── App.vue             # 应用入口
├── main.js             # 主入口
├── manifest.json       # 应用配置
└── pages.json          # 页面路由
```

## 注意事项

1. 微信小程序需要在微信公众平台注册并获取 AppID
2. 后端 API 地址配置在 `src/utils/request.js` 中
3. VIP 支付使用支付宝，需要配置支付宝相关参数
