<div align="center">
  <h1>🚀 Sidifensen Blog</h1>
  <p>现代化社区系统 | 前后端分离架构</p>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5.13-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.10.2-409EFF?logo=element)](https://element-plus.org/)

  <p>
    <a href="#项目概述">项目概述</a> •
    <a href="#系统架构">系统架构</a> •
    <a href="#技术架构">技术架构</a> •
    <a href="#项目结构">项目结构</a> •
    <a href="#功能特性">功能特性</a> •
    <a href="#快速开始">快速开始</a> •
    <a href="#部署指南">部署指南</a>
  </p>
</div>

---

<div align="center">

**🌐 官网地址**: [https://www.sidifensen.com](https://www.sidifensen.com)

**🔧 后台管理**: [https://admin.sidifensen.com](https://admin.sidifensen.com)

**📦 Gitee 仓库**: [https://gitee.com/sidifensen/sidifensen_blog](https://gitee.com/sidifensen/sidifensen_blog)

**💻 GitHub 仓库**: [https://github.com/sidifensen/sidifensen_blog](https://github.com/sidifensen/sidifensen_blog)

</div>

---

## 📋 项目概述

**Sidifensen Blog** 是一个功能完整的现代化社区系统，采用前后端分离架构设计。系统包含用户端展示界面和管理端后台系统，提供了从内容创作到用户交互的完整社区解决方案。

## 🏛️ 系统架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              客户端层 (Client Layer)                        │
├─────────────┬─────────────┬─────────────┬─────────────┬────────────────────┤
│   Web 用户端  │   Web 管理端  │   微信小程序   │  Electron 桌面 │   Capacitor App  │
│  (Vue 3)    │  (Vue 3)    │  (UniApp)   │   (Electron) │    (iOS/Android)  │
└──────┬──────┴──────┬──────┴──────┬──────┴────────────────┴────────┬────────┘
       │             │            │                                  │
       └─────────────┴────────────┴──────────────────────────────────┘
       │                         │                                  │
       ▼                         ▼                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            网关层 (Gateway Layer)                           │
│                          Nginx / Caddy 反向代理                              │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            接口层 (API Layer) - :5000                       │
│                         Spring Boot REST API (43个控制器)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  用户系统    │  │  内容系统    │  │  社交系统    │  │  会员系统    │    │
│  │  /user      │  │  /article   │  │  /follow     │  │  /vip        │    │
│  │  /oauth     │  │  /column    │  │  /like       │  │  /pay        │    │
│  │  /auth      │  │  /comment   │  │  /favorite   │  │              │    │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  消息系统    │  │  AI 服务     │  │  相册系统    │  │  系统管理    │    │
│  │  /message   │  │  /ai         │  │  /album      │  │  /admin      │    │
│  │  /ws        │  │              │  │  /photo      │  │  /system     │    │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘    │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         ▼                         ▼                         ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   数据层        │     │   缓存层        │     │   消息队列      │
│   MySQL 8.4    │     │   Redis 7.x     │     │   RabbitMQ 3.x  │
│   33个数据表   │     │   热榜/会话/令牌 │     │   异步/邮件/审核 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         │
         ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   对象存储      │     │   AI 能力       │     │   第三方服务    │
│   MinIO (S3)   │     │   DeepSeek API  │     │   支付宝/微信   │
│   图片/文件存储 │     │   摘要/客服      │     │   OAuth登录     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### ✨ 项目亮点

- 🎨 **现代化 UI**: 基于 Element Plus 的精美界面设计，支持暗黑模式
- 🔒 **安全可靠**: Spring Security + JWT 认证，阿里云内容安全检测
- ⚡ **高性能**: Redis 缓存 + RabbitMQ 异步处理
- 🤖 **AI 赋能**: DeepSeek API 提供摘要、标题生成、标签推荐、评论回复建议与流式智能客服
- 💎 **VIP 会员**: 完整会员体系 + 支付宝支付 + AI 配额管理
- 💬 **实时通信**: WebSocket 私信系统，支持消息撤回、已读状态、正在输入状态
- 🔔 **通知中心**: 实时消息通知，点赞、评论、关注、收藏全面追踪
- 📢 **公告系统**: 官方公告发布，支持置顶、优先级设置
- 📱 **多端支持**: Web、小程序、桌面端、移动端 App
- 🔧 **易于扩展**: 模块化架构，支持功能定制

## 🛠️ 技术架构

<table>
<tr>
<td valign="top" width="50%">

### 🔧 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.4.0 | 基础框架 |
| **Java** | 21 | 开发语言 |
| **Spring Security** | 6.x | 安全框架 |
| **MyBatis Plus** | 3.5.14 | ORM框架 |
| **MySQL** | 8.4.x | 主数据库 |
| **Redis** | 7.x | 缓存中间件 |
| **RabbitMQ** | 3.4.0 | 消息队列 |
| **MinIO** | 8.3.6 | 对象存储 |
| **Spring WebSocket** | 6.x | 实时通信 |
| **Spring AI** | 1.0.0-M5 | AI服务 |
| **JustAuth** | 1.16.7 | OAuth登录 |
| **阿里云内容安全** | 2.0.6 | 内容审核 |


| 工具 | 版本 |
|------|------|
| Lombok | 1.18.38 |
| Hutool | 6.3.0 |
| FastJSON | 2.0.52 |
| EasyCaptcha | 1.6.2 |

</td>
<td valign="top" width="50%">

### 🎨 前端技术栈

#### 用户端 (`sidifensen_user`)

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.13 | 核心框架 |
| **Vite** | 6.2.4 | 构建工具 |
| **Element Plus** | 2.10.2 | UI组件库 |
| **Pinia** | 3.0.1 | 状态管理 |
| **Vue Router** | 4.5.0 | 路由管理 |
| **Axios** | 1.10.0 | HTTP客户端 |
| **AiEditor** | 1.4.0 | 富文本编辑 |
| **ECharts** | 5.6.0 | 图表库 |
| **Electron** | 36.x | 桌面应用 |
| **Capacitor** | 8.x | 移动端打包 |

#### 管理端 (`sidifensen_admin`)

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.13 | 核心框架 |
| **Vite** | 6.2.4 | 构建工具 |
| **Element Plus** | 2.10.2 | UI组件库 |
| **Pinia** | 3.0.1 | 状态管理 |
| **ECharts** | 5.6.0 | 数据可视化 |
| **@antv/g2** | 5.4.8 | 高级图表 |
| **XLSX** | 0.18.5 | Excel处理 |

#### 微信小程序端 (`sidifensen_miniprogram`)

| 技术 | 版本 | 说明 |
|------|------|------|
| **UniApp** | 3.0.0 | 跨平台框架 |
| **Vue** | 3.4.21 | 核心框架 |
| **Pinia** | 2.1.7 | 状态管理 |
| **uView Pro** | 0.5.16 | UI组件库 |
| **Vite** | 5.2.8 | 构建工具 |

</td>
</tr>
</table>

## 📁 项目结构

```
sidifensen_blog/
├── script/                                         # 部署脚本和配置
│   ├── prod/                                       #   生产环境配置
│   │   ├── jenkins/                                #     Jenkins CI/CD 配置
│   │   ├── ssl/                                    #     HTTPS SSL 证书配置
│   │   └── dev/                                    #     Docker Compose 配置
│   └── dev/                                        #   开发环境配置
│
├── sidifensen_blog_backend/                        # 后端服务 (Spring Boot + Java 21)
│   └── src/main/java/com/sidifensen/
│       ├── aspect/                                 #   AOP 切面 (限流/日志/监控)
│       ├── config/                                 #   配置类
│       ├── controller/                             #   REST API 控制器 (43个)
│       ├── domain/                                 #   数据模型
│       │   ├── entity/                             #     实体类 (33个)
│       │   ├── dto/                                #     数据传输对象
│       │   ├── vo/                                 #     视图对象
│       │   ├── enums/                              #     枚举类 (21个)
│       │   └── constants/                          #     常量类
│       ├── exception/                              #   全局异常处理
│       ├── mapper/                                 #   MyBatis Plus Mapper
│       ├── minio/                                  #   MinIO 对象存储
│       ├── rabbitmq/                               #   RabbitMQ 消息队列
│       ├── redis/                                  #   Redis 缓存组件
│       ├── security/                               #   Spring Security + JWT
│       ├── service/impl/                          #   业务逻辑服务
│       ├── task/                                   #   定时任务
│       ├── utils/                                  #   通用工具类
│       └── websocket/                              #   WebSocket 实时通信
│
├── sidifensen_blog_frontend/                       # 前端应用 (Vue 3 + Vite)
│   ├── sidifensen_admin/                           #   管理端后台
│   │   └── src/
│   │       ├── api/                                #     API 接口 (23个模块)
│   │       ├── components/                         #     组件 (布局/图表/卡片)
│   │       ├── composables/                        #     组合式函数
│   │       ├── router/                             #     动态路由
│   │       ├── stores/                             #     Pinia 状态管理
│   │       └── views/                              #     页面组件
│   │
│   └── sidifensen_user/                            #   用户端前台
│       └── src/
│           ├── api/                                #     API 接口 (16个模块)
│           ├── components/                         #     可复用组件 (28个)
│           ├── composables/                        #     组合式函数
│           ├── router/                             #     路由配置
│           ├── stores/                             #     Pinia 状态管理 (4个)
│           └── views/                              #     页面组件 (16个目录)
│       ├── electron/                              #     Electron 桌面应用
│       └── android/                               #     Capacitor Android
│
├── sidifensen_blog_miniprogram/                    # 微信小程序端
│   └── src/
│       ├── api/                                   #     API 接口 (14个模块)
│       ├── components/                            #     公共组件
│       ├── pages/                                 #     页面 (17个)
│       ├── store/                                 #     Pinia 状态管理
│       └── utils/                                #     工具函数
│
├── sql/                                           # 数据库初始化脚本
└── img/                                           # 项目截图
```

## ⭐ 功能特性

### 🔐 用户系统

| 功能 | 说明 |
|------|------|
| 多种登录 | 账号密码 + 第三方 OAuth (GitHub, Gitee, QQ) |
| 权限管理 | 基于角色的访问控制 (RBAC) |
| 安全防护 | JWT 认证 + Spring Security |
| 邮件验证 | 注册验证码 + 密码重置 + 邮箱修改 |
| 用户主页 | 个人主页 + 关注/粉丝系统 |
| 创作中心 | 文章管理 + 专栏管理 + 评论管理 |

### 💬 社交互动

| 功能 | 说明 |
|------|------|
| 私信聊天 | WebSocket 实时聊天 + 消息撤回 + 在线状态 |
| 通知中心 | 点赞/评论/关注/收藏 + 分类筛选 + 已读管理 |
| 实时推送 | WebSocket 消息通知 + 未读数量提示 |

### 📝 内容管理

| 功能 | 说明 |
|------|------|
| 富文本编辑 | Markdown + 所见即所得 (AiEditor) |
| 文章系统 | 发布 + 草稿箱 + 回收站 + 审核机制 |
| 专栏系统 | 专栏创建 + 文章分类管理 |
| 标签系统 | 文章标签 + 标签管理 |
| 图片管理 | MinIO 存储 + 阿里云安全检测 |
| 内容审核 | 自动审核 + 人工审核双模式 |
| SEO 优化 | 友好 URL + Meta 信息 |

### 📢 公告系统

| 功能 | 说明 |
|------|------|
| 公告发布 | 标题 + 内容 + 优先级设置 |
| 公告管理 | 置顶 + 上下线 + 定时发布 |
| 多端展示 | 用户端 + 管理端 + 小程序 |

### 🤖 AI 能力

| 功能 | 说明 |
|------|------|
| 文章摘要 | 自动提取文章摘要 |
| 标题生成 | AI 智能生成标题 |
| 标签推荐 | 自动推荐文章标签 |
| 回复建议 | 评论回复建议 |
| 智能客服 | 流式输出客服机器人 |

### 💎 VIP 会员

| 功能 | 说明 |
|------|------|
| 会员套餐 | 多种套餐配置 |
| 支付对接 | 支付宝沙箱环境 |
| 权益管理 | AI 配额 + 会员专属内容 |
| 订单管理 | 创建 + 查询 + 异步回调 |

### 🔧 管理后台

| 功能 | 说明 |
|------|------|
| 仪表盘 | 数据统计 + ECharts 图表 |
| 用户管理 | 用户列表 + 角色权限 + 黑名单 |
| 内容管理 | 文章审核 + 评论管理 + 标签管理 |
| 文件管理 | 图片审核 + 相册管理 |
| 系统管理 | 菜单 + 权限 + 角色管理 |
| 日志管理 | 登录日志 + 访问日志 + 操作记录 |
| VIP 管理 | 套餐配置 + 订单管理 |
| 公告管理 | 公告发布 + 优先级设置 |

### ⚡ 性能优化

| 技术 | 说明 |
|------|------|
| Redis 缓存 | 热榜统计 + 会话管理 + 令牌存储 |
| RabbitMQ | 异步处理 + 邮件发送 + 内容审核 |
| 定时任务 | 热门统计 + 数据清理 |
| 代码分割 | 前端懒加载 + 按需加载 |
| AOP 限流 | API 限流保护，防止滥用 |
| 图片优化 | 压缩 + 懒加载 |

### 🚀 部署运维

| 技术 | 说明 |
|------|------|
| Docker | 容器化部署 + Docker Compose 编排 |
| CI/CD | Jenkins Pipeline 自动化构建部署 |
| SSL | HTTPS 证书配置 |
| 多环境 | 开发/测试/生产环境分离 |

---

## 🚀 快速开始

### 📋 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 21+ | 后端运行环境 |
| Node.js | 18+ | 前端构建环境 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存数据库 |
| RabbitMQ | 3.8+ | 消息队列 |
| MinIO | RELEASE.2025-06-05+ | 对象存储 |
| Docker | 20.0+ | 容器化部署 (推荐) |

### 🔧 后端启动

```bash
# 克隆项目
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog/sidifensen_blog_backend

# 复制环境配置
cp .env.example .env
# 编辑 .env 文件配置数据库、Redis、RabbitMQ 等

# 启动后端 (使用 dotenv)
mvn clean install
dotenv -- mvn spring-boot:run

# 或使用 IDE 直接运行 Main.java
```

### 🎨 前端启动

```bash
# 用户端
cd sidifensen_blog_frontend/sidifensen_user
npm install && npm run dev
# 访问 http://localhost:7000

# 管理端
cd sidifensen_blog_frontend/sidifensen_admin
npm install && npm run dev
# 访问 http://localhost:8000

# 小程序 (H5 模式)
cd sidifensen_blog_miniprogram
npm install && npm run dev:h5
# 访问 http://localhost:9000
```

### 🐳 Docker 方式 (推荐)

```bash
# 一键启动所有服务
cd script/dev && ./start.bat    # Windows
# cd script/dev && ./start.sh   # Linux/Mac
```

### 🌐 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 用户端 | http://localhost:7000 | 社区前台 |
| 管理端 | http://localhost:8000 | 后台管理 |
| 后端 API | http://localhost:5000 | REST API |
| MinIO | http://localhost:9001 | 对象存储 |
| RabbitMQ | http://localhost:15672 | 消息队列 |

---

## 🐳 部署指南

### 🤖 自动化部署 (推荐)

项目支持 **Jenkins + GitHub** CI/CD 自动化部署。

### 💻 生产环境部署

```bash
# Docker Compose 一键部署
cd script/prod
cp .env.example .env
vim .env  # 配置生产环境参数
./start.sh
```

### 📦 传统方式部署

```bash
# 后端
cd sidifensen_blog_backend
mvn clean package -DskipTests
java -jar target/sidifensen_blog_backend-1.0-SNAPSHOT.jar

# 前端
cd sidifensen_blog_frontend/sidifensen_user && npm run build
cd sidifensen_blog_frontend/sidifensen_admin && npm run build
```

---

## 📅 后续计划

### ✅ 已完成功能

- [x] 操作日志系统
- [x] Dashboard 仪表盘重构
- [x] 私信功能 (WebSocket)
- [x] 通知中心
- [x] VIP 会员体系
- [x] **公告系统**

### 🚧 计划中的功能

- [ ] 数据统计增强 (PV/UV、停留时长)
- [ ] 推荐系统 (基于用户兴趣的文章推荐)
- [ ] 全文搜索引擎 (Elasticsearch)

---

## 🤝 贡献指南

1. **Fork** 项目到你的 GitHub 账号
2. **创建特性分支**: `git checkout -b feature/amazing-feature`
3. **提交更改**: `git commit -m 'Add some amazing feature'`
4. **推送分支**: `git push origin feature/amazing-feature`
5. **创建 Pull Request**
