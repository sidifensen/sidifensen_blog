<div align="center">
  <h1>🚀 Sidifensen Blog</h1>
  <p>现代化个人博客/论坛系统 | 前后端分离架构</p>
  
  ![Java](https://img.shields.io/badge/Java-21-orange.svg)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.4-brightgreen.svg)
  ![Vue.js](https://img.shields.io/badge/Vue.js-3.5.13-4FC08D.svg)
  ![Element Plus](https://img.shields.io/badge/Element%20Plus-2.10.2-409EFF.svg)

  <p>
    <a href="#-功能特性">功能特性</a> •
    <a href="#-技术架构">技术架构</a> •
    <a href="#-快速开始">快速开始</a> •
    <a href="#-部署指南">部署指南</a> •
    <a href="#-项目结构">项目结构</a>
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

**Sidifensen Blog** 是一个功能完整的现代化个人博客/论坛系统，采用前后端分离架构设计。系统包含用户端展示界面和管理端后台系统，提供了从内容创作到用户交互的完整博客解决方案。

### ✨ 项目亮点

- 🎨 **现代化 UI**: 基于 Element Plus 的精美界面设计
- 🔒 **安全可靠**: Spring Security + JWT 认证，阿里云内容安全检测
- ⚡ **高性能**: Redis 缓存 + RabbitMQ 异步处理
- 📱 **响应式设计**: 完美适配桌面端和移动端
- 🔧 **易于扩展**: 模块化架构，支持功能定制
- 🚀 **开箱即用**: Docker 一键部署，快速上线

## 🛠️ 技术架构

<table>
<tr>
<td valign="top" width="50%">

### 🔧 后端技术栈

- **核心框架**: Spring Boot 3.1.4
- **开发语言**: Java 21
- **安全框架**: Spring Security + JWT
- **数据库**: MySQL 8.1.0
- **ORM 框架**: MyBatis-Plus 3.5.12
- **缓存中间件**: Redis 3.4.0
- **消息队列**: RabbitMQ 3.4.0
- **文件存储**: MinIO 8.3.6
- **模板引擎**: Thymeleaf 3.4.0
- **邮件服务**: Spring Mail
- **第三方登录**: JustAuth 1.16.7
- **内容安全**: 阿里云图片内容检测 2.0.6
- **监控运维**: Spring Actuator
- **切面编程**: Spring AOP
- **工具库**:
  - Lombok 1.18.38 (代码简化)
  - Hutool 5.8.38 (Java 工具库)
  - FastJSON 2.0.50 (JSON 处理)
  - Easy-Captcha 1.6.2 (验证码生成)

</td>
<td valign="top" width="50%">

### 🎨 前端技术栈

#### 用户端

- **核心框架**: Vue 3.5.13
- **构建工具**: Vite 6.2.4
- **UI 组件库**: Element Plus 2.10.2
- **状态管理**: Pinia 3.0.1 + 持久化插件
- **路由管理**: Vue Router 4.5.0
- **HTTP 客户端**: Axios 1.10.0
- **样式预处理**: Sass Embedded 1.89.2
- **图标库**: Element Plus Icons 2.3.1 + SVG Icons
- **富文本编辑器**: AiEditor 1.4.0
- **工具库**: VueUse 13.5.0

#### 管理端（额外功能）

- **数据可视化**: ECharts 5.6.0
- **Excel 处理**: XLSX 0.18.5
- **文件保存**: FileSaver 2.0.5

#### 开发工具

- **代码混淆**: Vite Plugin Obfuscator
- **安全防护**: Disable DevTool 0.3.9
- **自动导入**: unplugin-auto-import + unplugin-vue-components
- **开发调试**: Vue DevTools 7.7.2

</td>
</tr>
</table>

### 🏗️ 架构特点

#### 📐 架构设计

- **前后端分离**: 前后端完全解耦，独立开发和部署
- **RESTful API**: 统一的接口设计和 `Result<T>` 响应格式
- **模块化设计**: 按业务功能拆分，支持独立扩展

#### ⚡ 性能优化

- **Redis 缓存**: 热点数据缓存，显著提升响应速度
- **RabbitMQ 异步**: 消息队列处理耗时任务，提高系统吞吐量
- **MyBatis-Plus**: Lambda 表达式构建查询，类型安全高效
- **定时任务**: 热门文章统计、数据清理等后台处理

#### 🔒 安全防护

- **Spring Security + JWT**: 无状态认证和 RBAC 权限控制
- **多重防护**: SQL 注入防护 + XSS 过滤 + 敏感信息加密
- **内容审核**: 阿里云内容安全服务自动审核
- **访问日志**: 登录日志、访问日志完整追踪

#### ☁️ 存储与部署

- **MinIO 对象存储**: 分布式文件存储，支持大容量管理
- **Docker 容器化**: 一键部署所有服务，环境一致性保障
- **多环境配置**: 开发、测试、生产环境分离
- **跨平台支持**: Linux、Windows、macOS 全平台部署

#### 🎨 前端工程化

- **Vue 3 + Vite**: Composition API 开发，快速构建和热更新
- **Pinia 状态管理**: 全局状态管理和持久化
- **响应式设计**: 完美适配桌面端、平板和移动端

## 📁 项目结构

```
sidifensen_blog/
│
├── README.md                         # 📖 项目说明文档
│
├── script/                           # 📜 部署脚本和配置
│   ├── deploy/                       # 自动化部署脚本
│   │   ├── deploy.sh                 # 一键部署脚本
│   │   └── deploy.md                 # 部署文档
│   ├── ssl/                          # HTTPS SSL 证书配置
│   │   ├── docker-compose-apps.yml   # SSL 应用编排
│   │   ├── docker-compose-backend.yml    # 后端 SSL 配置
│   │   ├── docker-compose-frontend.yml   # 前端 SSL 配置
│   │   ├── docker-compose-services.yml   # 服务 SSL 配置
│   │   ├── docker-compose-ssl.yml    # SSL 版本主编排文件
│   │   ├── env.example               # SSL 环境变量示例
│   │   ├── nginx-ssl.conf            # Nginx SSL 配置
│   │   ├── README-ssl.md             # SSL 部署说明
│   │   ├── ssl.sh                    # SSL 证书申请脚本
│   │   └── start.sh                  # SSL 版本启动脚本
│   ├── docker-compose-apps.yml       # 应用编排 (前后端)
│   ├── docker-compose-service.yml    # 基础服务编排 (MySQL/Redis/RabbitMQ/MinIO)
│   ├── docker-compose.yml            # Docker Compose 主编排文件
│   ├── env.example                   # 环境变量配置模板
│   ├── nginx.conf                    # Nginx 反向代理配置
│   ├── README-Docker.md              # Docker 部署详细说明
│   ├── start.bat                     # Windows 启动脚本
│   └── start.sh                      # Linux/Mac 启动脚本
│
├── sidifensen_blog_backend/          # 🔧 后端服务 (Spring Boot 3.1.4 + Java 21)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sidifensen/
│   │   │   │   ├── aspect/           # AOP 切面 (日志、权限等)
│   │   │   │   ├── config/           # 配置类
│   │   │   │   ├── controller/       # REST API 控制器层
│   │   │   │   ├── domain/           # 数据模型层
│   │   │   │   │   ├── constants/    # 常量类
│   │   │   │   │   ├── dto/          # 数据传输对象 (请求参数)
│   │   │   │   │   ├── entity/       # 实体类 (对应数据库表)
│   │   │   │   │   ├── enums/        # 枚举类
│   │   │   │   │   ├── result/       # 统一响应结果封装
│   │   │   │   │   └── vo/           # 视图对象 (响应数据)
│   │   │   │   ├── exception/        # 全局异常处理
│   │   │   │   ├── handler/          # 处理器
│   │   │   │   ├── mapper/           # MyBatis-Plus 数据访问层
│   │   │   │   ├── minio/            # MinIO 对象存储配置
│   │   │   │   ├── rabbitmq/         # RabbitMQ 消息队列配置
│   │   │   │   ├── redis/            # Redis 缓存配置
│   │   │   │   ├── security/         # Spring Security + JWT 配置
│   │   │   │   ├── service/          # 业务逻辑服务层
│   │   │   │   │   └── impl/         # 服务实现类
│   │   │   │   ├── task/             # 定时任务
│   │   │   │   └── utils/            # 通用工具类
│   │   │   └── resources/
│   │   │       ├── mapper/           # MyBatis XML 映射文件
│   │   │       ├── templates/        # Thymeleaf 邮件模板
│   │   │       ├── application.yaml  # 应用主配置文件
│   │   │       └── logback-spring.xml    # 日志配置
│   │   └── test/                     # 测试代码
│   ├── target/                       # Maven 编译输出目录
│   ├── docker.sh                     # Docker 部署脚本
│   ├── Dockerfile                    # Docker 镜像构建文件
│   └── pom.xml                       # Maven 项目配置文件
│
├── sidifensen_blog_frontend/         # 🎨 前端应用 (Vue 3 + Vite)
│   │
│   ├── sidifensen_admin/             # 🔧 管理端后台
│   │   ├── dist/                     # 构建产物
│   │   ├── node_modules/             # 依赖包
│   │   ├── public/                   # 公共资源
│   │   │   └── favicon.ico           # 网站图标
│   │   ├── src/
│   │   │   ├── api/                  # API 接口定义
│   │   │   ├── assets/               # 静态资源
│   │   │   │   ├── font/             # 字体文件
│   │   │   │   ├── scss/             # 全局样式
│   │   │   │   └── svg/              # SVG 图标
│   │   │   ├── components/           # 可复用组件
│   │   │   │   ├── 404.vue           # 404 页面
│   │   │   │   ├── Dark.vue          # 暗黑模式切换
│   │   │   │   ├── Layout.vue        # 后台布局
│   │   │   │   ├── ProjectLinks.vue  # 项目链接
│   │   │   │   └── SvgIcon.vue       # SVG 图标
│   │   │   ├── router/               # Vue Router 路由配置
│   │   │   ├── stores/               # Pinia 状态管理
│   │   │   │   ├── darkStore.js      # 暗黑模式状态
│   │   │   │   └── userStore.js      # 用户状态
│   │   │   ├── utils/                # 工具函数
│   │   │   ├── views/                # 页面组件
│   │   │   │   ├── account/          # 账户设置
│   │   │   │   ├── article/          # 文章管理
│   │   │   │   ├── column/           # 专栏管理
│   │   │   │   ├── comment/          # 评论管理
│   │   │   │   ├── home/             # 仪表盘
│   │   │   │   ├── link/             # 友链管理
│   │   │   │   ├── photo/            # 图片管理
│   │   │   │   ├── system/           # 系统管理
│   │   │   │   │   ├── blacklist/    # 黑名单管理
│   │   │   │   │   ├── menu/         # 菜单管理
│   │   │   │   │   ├── permission/   # 权限管理
│   │   │   │   │   ├── role/         # 角色管理
│   │   │   │   │   └── user/         # 用户管理
│   │   │   │   └── tag/              # 标签管理
│   │   │   ├── App.vue               # 根组件
│   │   │   └── main.js               # 应用入口
│   │   ├── default.conf              # Nginx 配置
│   │   ├── docker.sh                 # Docker 部署脚本
│   │   ├── Dockerfile                # Docker 镜像构建
│   │   ├── index.html                # HTML 入口文件
│   │   ├── jsconfig.json             # JS 配置
│   │   ├── package-lock.json         # 依赖锁定文件
│   │   ├── package.json              # 依赖管理
│   │   ├── README.md                 # 管理端说明
│   │   └── vite.config.js            # Vite 配置
│   │
│   └── sidifensen_user/              # 📱 用户端前台
│       ├── dist/                     # 构建产物
│       ├── node_modules/             # 依赖包
│       ├── public/                   # 公共资源
│       │   └── favicon.ico           # 网站图标
│       ├── src/
│       │   ├── api/                  # API 接口定义
│       │   ├── assets/               # 静态资源
│       │   │   ├── font/             # 字体文件
│       │   │   ├── img/              # 图片资源
│       │   │   ├── scss/             # 全局样式
│       │   │   └── svg/              # SVG 图标
│       │   ├── components/           # 可复用组件
│       │   │   ├── Comment/          # 评论组件
│       │   │   ├── Footer.vue        # 全局底部
│       │   │   ├── Header.vue        # 全局头部
│       │   │   ├── LoadingAnimation.vue  # 加载动画
│       │   │   └── SvgIcon.vue       # SVG 图标组件
│       │   ├── router/               # Vue Router 路由配置
│       │   ├── stores/               # Pinia 状态管理
│       │   │   ├── darkStore.js      # 暗黑模式状态
│       │   │   └── userStore.js      # 用户状态
│       │   ├── utils/                # 工具函数
│       │   ├── views/                # 页面组件
│       │   │   ├── Account/          # 账户设置
│       │   │   ├── Article/          # 文章详情
│       │   │   ├── Column/           # 专栏
│       │   │   ├── Creation/         # 创作中心
│       │   │   ├── Home/             # 首页
│       │   │   ├── Layout/           # 布局组件
│       │   │   ├── Login/            # 登录页
│       │   │   ├── Photo/            # 相册
│       │   │   ├── Search/           # 搜索页
│       │   │   └── User/             # 用户主页
│       │   ├── App.vue               # 根组件
│       │   └── main.js               # 应用入口
│       ├── default.conf              # Nginx 配置
│       ├── docker.sh                 # Docker 部署脚本
│       ├── Dockerfile                # Docker 镜像构建
│       ├── index.html                # HTML 入口文件
│       ├── jsconfig.json             # JS 配置
│       ├── package-lock.json         # 依赖锁定文件
│       ├── package.json              # 依赖管理
│       ├── README.md                 # 用户端说明
│       └── vite.config.js            # Vite 配置
│
├── sql/                              # 🗄️ 数据库脚本
│   ├── console.sql                   # 控制台相关 SQL
│   └── sidifensen_blog.sql           # 完整数据库结构和初始数据
│
└── .gitignore                        # Git 忽略配置
```

### 📂 核心目录说明

#### 后端 (`sidifensen_blog_backend/`)

采用经典的 **MVC 三层架构** + **领域驱动设计 (DDD)**：

- **Controller 层**: 处理 HTTP 请求，参数校验，调用 Service
- **Service 层**: 核心业务逻辑，事务控制
- **Mapper 层**: 数据库操作，使用 MyBatis-Plus
- **Domain 层**: 领域模型，包含实体、DTO、VO、枚举等
- **Security 层**: 认证授权，JWT Token 管理
- **Utils 层**: 工具类，如时间处理、加密解密、文件上传等

#### 前端 (`sidifensen_blog_frontend/`)

基于 **Vue 3 Composition API** 的模块化架构：

- **User 端**: 面向普通用户的博客前台，注重用户体验和内容展示
- **Admin 端**: 面向管理员的后台系统，注重功能管理和数据操作
- **API 层**: 封装所有后端接口调用，统一请求响应处理
- **Stores 层**: Pinia 状态管理，支持持久化
- **Router 层**: 路由配置，权限控制
- **Utils 层**: 工具函数，如时间格式化、权限判断等

#### 部署 (`script/`)

提供 **多种部署方式**，灵活适配不同场景：

- **Docker Compose**: 推荐方式，一键启动所有服务
- **传统部署**: 手动部署前后端和依赖服务
- **SSL 支持**: HTTPS 证书配置和自动续期
- **跨平台**: Windows、Linux、macOS 全平台支持

## ⭐ 功能特性

<table>
<tr>
<td valign="top" width="50%">

### 🔐 用户系统

- **多种登录方式**: 账号密码 + 第三方 OAuth 登录 (GitHub, Gitee)
- **权限管理**: 基于角色的访问控制 (RBAC)
- **安全防护**: JWT 认证 + Spring Security
- **邮件验证**: 注册验证码 + 密码重置 + 邮箱修改
- **图形验证码**: 防止恶意注册和登录
- **用户主页**: 个人主页展示 + 关注/粉丝系统
- **创作中心**: 文章管理 + 专栏管理 + 评论管理

### 📝 内容管理

- **富文本编辑**: 支持 Markdown + 所见即所得编辑 (AiEditor)
- **文章系统**: 文章发布 + 草稿箱 + 回收站 + 审核机制
- **专栏系统**: 专栏创建 + 文章分类管理
- **标签系统**: 文章标签 + 标签管理
- **图片管理**: MinIO 对象存储 + 图片安全检测
- **内容审核**: 阿里云内容安全自动审核
- **SEO 优化**: 友好的 URL 结构和 Meta 信息

### 🎨 用户界面

- **响应式设计**: 完美适配桌面、平板、手机
- **暗黑模式**: 支持明暗主题切换
- **加载动画**: 优雅的加载和过渡效果
- **无限滚动**: 流畅的内容浏览体验
- **搜索功能**: 全文搜索和标签筛选
- **用户交互**: 点赞 + 收藏 + 评论 + 关注
- **历史记录**: 浏览历史

</td>
<td valign="top" width="50%">

### 🔧 管理后台

- **仪表盘**: 数据统计和系统监控 + ECharts 图表展示
- **用户管理**: 用户列表、角色权限管理、黑名单管理
- **内容管理**: 文章审核、评论管理、标签管理
- **文件管理**: 图片审核、相册管理、批量操作
- **系统管理**: 菜单管理、权限管理、角色管理
- **日志管理**: 登录日志、访问日志、操作记录
- **系统设置**: 站点配置、邮件配置、安全设置

### ⚡ 性能优化

- **Redis 缓存**: 热点数据缓存，提升响应速度
- **异步处理**: RabbitMQ 消息队列处理耗时任务
- **定时任务**: 热门文章统计、数据清理
- **代码分割**: 前端代码按需加载
- **图片优化**: 图片压缩和懒加载
- **AOP 切面**: 统一日志记录和性能监控

### 🚀 部署运维

- **Docker 支持**: 一键容器化部署 + Docker Compose 编排
- **多环境配置**: 开发、测试、生产环境分离
- **SSL 支持**: HTTPS 证书配置和自动续期
- **日志管理**: 结构化日志记录和分析
- **监控告警**: 应用性能监控
- **备份恢复**: 数据库自动备份机制
- **Windows 支持**: 提供 Windows 批处理脚本

</td>
</tr>
</table>

## 🚀 快速开始

### 📋 环境要求

| 组件              | 版本要求                     | 说明              |
| ----------------- | ---------------------------- | ----------------- |
| ☕ JDK            | 21+                          | 后端运行环境      |
| 🟢 Node.js        | 18+                          | 前端构建环境      |
| 🐬 MySQL          | 8.0+                         | 主数据库          |
| 🔴 Redis          | 6.0+                         | 缓存数据库        |
| 🐰 RabbitMQ       | 3.8+                         | 消息队列          |
| ☁️ MinIO          | RELEASE.2025-04-08T15-41-24Z | 对象存储          |
| 🐳 Docker         | 20.0+                        | 容器化部署 (推荐) |
| 🐳 Docker Compose | 1.29+                        | 容器编排          |

### 💾 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE sidifensen_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入数据结构
mysql -u root -p sidifensen_blog < sql/sidifensen_blog.sql
```

> 💡 **提示**: 如果使用 Docker Compose 部署，数据库会自动初始化，无需手动执行以上步骤。

### 🔧 后端启动

#### 传统方式启动

```bash
# 克隆项目
git clone https://github.com/your-username/sidifensen_blog.git
cd sidifensen_blog/sidifensen_blog_backend

# 配置数据库连接
# 编辑 src/main/resources/application-dev.yaml 文件
# 修改数据库、Redis、RabbitMQ 连接信息

# 启动后端服务
mvn clean install
mvn spring-boot:run

# 或者使用 IDE 直接运行 SidifensenBlogBackendApplication.java
```

#### Docker Compose 方式启动（推荐）

```bash
# 克隆项目
git clone https://github.com/your-username/sidifensen_blog.git
cd sidifensen_blog

# 复制环境配置文件
# Windows:
copy script\env.example .env
# Linux/Mac:
# cp script/env.example .env

# 根据需要修改 .env 文件中的配置
# vim .env

# 启动所有服务
# Windows:
cd script && start.bat
# Linux/Mac:
# cd script && ./start.sh
```

#### 一键启动脚本

项目提供了便捷的启动脚本：

- **Windows**: `script/start.bat` - Windows 批处理脚本，支持环境检查、服务启动、日志查看等功能
- **Linux/Mac**: `script/start.sh` - Shell 脚本，提供相同的功能

### 🎨 前端启动

#### 传统方式启动

```bash
# 用户端启动
cd sidifensen_blog_frontend/sidifensen_user
npm install
npm run dev
# 访问 http://localhost:5173

# 管理端启动 (新开终端)
cd sidifensen_blog_frontend/sidifensen_admin
npm install
npm run dev
# 访问 http://localhost:5174
```

#### Docker Compose 方式启动（推荐）

使用 Docker Compose 启动时，前端应用会自动构建并启动，无需手动执行以下步骤。

### 🌐 访问应用

启动成功后，可通过以下地址访问：

#### 传统方式启动

- 📱 **用户端**: http://localhost:5173 (博客前台)
- 🔧 **管理端**: http://localhost:5174 (后台管理)
- 🔌 **后端 API**: http://localhost:8080 (REST API)

#### Docker Compose 方式启动（推荐）

- 📱 **用户端**: http://localhost:7000 (博客前台)
- 🔧 **管理端**: http://localhost:8000 (后台管理)
- 🔌 **后端 API**: http://localhost:5000 (REST API)
- ☁️ **MinIO 控制台**: http://localhost:9001 (对象存储管理)
- 🐰 **RabbitMQ 控制台**: http://localhost:15672 (消息队列管理)

> 💡 **提示**: 默认端口可以在 `.env` 文件中修改。

## 🐳 部署指南

### 🔧 生产环境部署

#### 1️⃣ 环境准备

```bash
# 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.0.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 2️⃣ 使用 Docker Compose 一键部署（推荐）

```bash
# 克隆项目
git clone https://github.com/your-username/sidifensen_blog.git
cd sidifensen_blog

# 复制环境配置文件
# Linux/Mac:
cp script/env.example .env
# Windows:
# copy script\env.example .env

# 根据生产环境修改 .env 文件中的配置
vim .env

# 一键启动所有服务
# Windows:
cd script && start.bat
# Linux/Mac:
# cd script && ./start.sh
```

#### 3️⃣ SSL 证书配置（可选）

项目支持 HTTPS 部署，提供了 SSL 证书配置：

```bash
# 进入 SSL 配置目录
cd script/ssl

# 复制 SSL 环境配置
cp env.example .env

# 配置 SSL 证书信息
vim .env

# 启动 SSL 版本
./start.sh
```

#### 4️⃣ 传统方式部署

##### 后端部署

```bash
cd sidifensen_blog_backend

# 1. 修改生产环境配置
vim src/main/resources/application-prod.yaml

# 2. 构建 JAR 包
mvn clean package -DskipTests

# 3. 构建 Docker 镜像并启动
chmod +x docker.sh
./docker.sh

# 或者手动执行
docker build -t sidifensen-blog-backend .
docker run -d -p 8080:8080 --name sidifensen-blog-backend sidifensen-blog-backend
```

##### 前端部署

```bash
# 用户端部署
cd sidifensen_blog_frontend/sidifensen_user
npm run build
chmod +x docker.sh
./docker.sh

# 管理端部署
cd ../sidifensen_admin
npm run build
chmod +x docker.sh
./docker.sh
```

### 🌐 访问地址

部署完成后，可通过以下地址访问：

| 服务        | 地址                     | 说明          |
| ----------- | ------------------------ | ------------- |
| 📱 用户端   | http://your-domain:7000  | 博客前台展示  |
| 🔧 管理端   | http://your-domain:8000  | 后台管理系统  |
| 🔌 后端 API | http://your-domain:5000  | REST API 接口 |
| ☁️ MinIO    | http://your-domain:9001  | 对象存储管理  |
| 🐰 RabbitMQ | http://your-domain:15672 | 消息队列管理  |

### ⚙️ 配置说明

**重要配置项：**

- 数据库连接信息
- Redis 连接配置
- RabbitMQ 连接配置
- MinIO 存储配置
- 阿里云内容安全配置
- 邮件服务配置
- OAuth 第三方登录配置

> 💡 **提示**: 所有配置项都可以在 `.env` 文件中修改，详细说明请参考 `script/env.example` 文件。

## 🤝 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

### 📝 提交流程

1. **Fork 项目** 到你的 GitHub 账号
2. **创建特性分支**: `git checkout -b feature/amazing-feature`
3. **提交更改**: `git commit -m 'Add some amazing feature'`
4. **推送分支**: `git push origin feature/amazing-feature`
5. **创建 Pull Request**

### 🐛 问题报告

发现 Bug 或有新功能建议？请通过 [GitHub Issues](https://github.com/your-username/sidifensen_blog/issues) 提交。

**Bug 报告请包含：**

- 🔍 详细的问题描述
- 🔄 重现步骤
- 💻 运行环境信息
- 📸 截图或错误日志

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源协议。

## 📞 联系方式

<div align="center">

**如有问题或建议，欢迎通过以下方式联系：**

[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black?logo=github)](https://github.com/sidifensen/sidifensen_blog/issues)
[![Email](https://img.shields.io/badge/Email-Contact-blue?logo=gmail)](mailto:sidifensen@example.com)

---

### 🌟 如果这个项目对你有帮助，请给一个 Star ⭐

**💡 这是一个设计精良、功能完整的现代化博客系统，适合：**

- 🎓 学习 Spring Boot + Vue 3 全栈开发
- 🚀 快速搭建个人博客网站
- 📚 作为企业级项目开发参考
- 🔧 二次开发和功能扩展

</div>
