# 斯蒂芬森博客微信小程序

斯蒂芬森博客微信小程序端，基于 UniApp + Vue3 开发。

## 技术栈

- **框架**: UniApp + Vue3 (Composition API)
- **状态管理**: Pinia
- **UI 组件**: uView UI
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
```

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
