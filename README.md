<div align="center">
  <h1>🚀 Sidifensen Blog</h1>
  <p>现代化个人博客系统 | 前后端分离架构</p>
  
  ![Java](https://img.shields.io/badge/Java-21-orange.svg)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.9-brightgreen.svg)
  ![Vue.js](https://img.shields.io/badge/Vue.js-3.5.13-4FC08D.svg)
  ![Element Plus](https://img.shields.io/badge/Element%20Plus-2.10.2-409EFF.svg)
  ![License](https://img.shields.io/badge/License-MIT-blue.svg)
  
  <p>
    <a href="#-功能特性">功能特性</a> •
    <a href="#-技术架构">技术架构</a> •
    <a href="#-快速开始">快速开始</a> •
    <a href="#-部署指南">部署指南</a> •
    <a href="#-项目结构">项目结构</a>
  </p>
</div>

---

## 📋 项目概述

**Sidifensen Blog** 是一个功能完整的现代化个人博客系统，采用前后端分离架构设计。系统包含用户端展示界面和管理端后台系统，提供了从内容创作到用户交互的完整博客解决方案。

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

- **核心框架**: Spring Boot 3.3.9
- **开发语言**: Java 21
- **安全框架**: Spring Security + JWT
- **数据库**: MySQL 8.1.0
- **ORM 框架**: MyBatis-Plus 3.5.9
- **缓存中间件**: Redis
- **消息队列**: RabbitMQ
- **文件存储**: MinIO RELEASE.2025-04-08T15-41-24Z
- **模板引擎**: Thymeleaf
- **第三方登录**: JustAuth 1.16.22
- **内容安全**: 阿里云图片内容检测
- **工具库**:
  - Lombok 1.18.36 (代码简化)
  - Hutool 5.8.32 (工具库)
  - FastJSON 2.0.49 (JSON 处理)
  - Easy-Captcha 1.6.2 (验证码)

</td>
<td valign="top" width="50%">

### 🎨 前端技术栈

- **核心框架**: Vue 3.5.13
- **构建工具**: Vite 6.2.4
- **UI 组件库**: Element Plus 2.10.2
- **状态管理**: Pinia 3.0.1
- **路由管理**: Vue Router 4.5.0
- **HTTP 客户端**: Axios 1.10.0
- **样式预处理**: Sass
- **图标库**: Element Plus Icons + SVG Icons
- **富文本编辑器**: AiEditor 1.4.0
- **开发工具**:
  - Vue DevTools 7.7.2
  - Auto Import 19.3.0
  - TypeScript 支持
  - 代码混淆保护

</td>
</tr>
</table>

### 🏗️ 架构特点

- **微服务架构**: 前后端完全分离，独立部署
- **RESTful API**: 标准化的 API 接口设计
- **响应式设计**: 支持多终端适配
- **高可用部署**: Docker 容器化部署
- **安全防护**: 多层安全防护机制

## 📁 项目结构

```
📦 sidifensen_blog/
├── 🔧 sidifensen_blog_backend/              # 后端服务 (Spring Boot)
│   ├── 📂 src/main/java/com/sidifensen/
│   │   ├── 🎮 controller/                   # REST API 控制器
│   │   ├── 💼 service/                      # 业务逻辑服务层
│   │   ├── 🗃️ mapper/                       # MyBatis 数据访问层
│   │   ├── 📋 domain/                       # 数据实体类
│   │   ├── 🔐 security/                     # Spring Security 配置
│   │   ├── 🚀 redis/                        # Redis 缓存配置
│   │   ├── 📁 minio/                        # MinIO 文件存储配置
│   │   ├── 📨 rabbitmq/                     # RabbitMQ 消息队列
│   │   └── 🛠️ utils/                        # 通用工具类
│   ├── 📂 src/main/resources/
│   │   ├── ⚙️ application*.yaml             # 应用配置文件
│   │   ├── 📝 logback-spring.xml            # 日志配置
│   │   ├── 🗂️ mapper/                       # MyBatis XML 映射文件
│   │   └── 📧 templates/                    # Thymeleaf 邮件模板
│   ├── 🐳 Dockerfile                        # Docker 构建文件
│   ├── 📜 docker.sh                         # Docker 部署脚本
│   └── 📋 pom.xml                           # Maven 依赖配置
├── 🎨 sidifensen_blog_frontend/             # 前端应用目录
│   ├── 👥 sidifensen_user/                  # 用户端界面 (Vue 3)
│   │   ├── 📂 src/
│   │   │   ├── 🔌 api/                      # API 接口封装
│   │   │   ├── 🎭 components/               # 可复用组件
│   │   │   ├── 📄 views/                    # 页面视图组件
│   │   │   ├── 🧭 router/                   # 路由配置
│   │   │   ├── 🗃️ stores/                   # Pinia 状态管理
│   │   │   └── 🛠️ utils/                    # 前端工具函数
│   │   ├── 🐳 Dockerfile                    # Docker 构建文件
│   │   ├── 📜 docker.sh                     # Docker 部署脚本
│   │   └── 📦 package.json                  # npm 依赖配置
│   └── 🔧 sidifensen_admin/                 # 管理端后台 (Vue 3)
│       ├── 📂 src/                          # 源码目录 (结构同用户端)
│       ├── 🐳 Dockerfile                    # Docker 构建文件
│       ├── 📜 docker.sh                     # Docker 部署脚本
│       └── 📦 package.json                  # npm 依赖配置
├── 🗄️ sql/                                  # 数据库脚本
│   ├── 📊 sidifensen_blog.sql               # 主数据库结构和数据
│   └── 🖥️ console.sql                       # 控制台相关脚本
├── 📜 script/                               # 部署脚本和配置文件
│   ├── 🐳 docker-compose.yml                # Docker Compose 编排文件
│   ├── 📜 env.example                       # 环境变量配置示例
│   ├── 📜 start.sh                          # Linux 启动脚本
│   ├── 📜 start.bat                         # Windows 启动脚本
│   └── 📖 README-Docker.md                  # Docker 部署说明
└── 📖 README.md                             # 项目说明文档
```

## ⭐ 功能特性

<table>
<tr>
<td valign="top" width="50%">

### 🔐 用户系统

- **多种登录方式**: 账号密码 + 第三方 OAuth 登录
- **权限管理**: 基于角色的访问控制 (RBAC)
- **安全防护**: JWT 认证 + Spring Security
- **邮件验证**: 注册验证码 + 密码重置
- **图形验证码**: 防止恶意注册和登录

### 📝 内容管理

- **富文本编辑**: 支持 Markdown + 所见即所得编辑
- **文章分类**: 多级分类和标签系统
- **图片管理**: MinIO 对象存储 + 图片安全检测
- **内容审核**: 阿里云内容安全自动审核
- **SEO 优化**: 友好的 URL 结构和 Meta 信息

### 🎨 用户界面

- **响应式设计**: 完美适配桌面、平板、手机
- **暗黑模式**: 支持明暗主题切换
- **加载动画**: 优雅的加载和过渡效果
- **无限滚动**: 流畅的内容浏览体验
- **搜索功能**: 全文搜索和标签筛选

</td>
<td valign="top" width="50%">

### 🔧 管理后台

- **仪表盘**: 数据统计和系统监控
- **用户管理**: 用户列表、角色权限管理
- **内容管理**: 文章、分类、标签的 CRUD 操作
- **文件管理**: 图片上传、删除、批量操作
- **系统设置**: 站点配置、邮件配置等

### ⚡ 性能优化

- **Redis 缓存**: 热点数据缓存，提升响应速度
- **异步处理**: RabbitMQ 消息队列处理耗时任务
- **CDN 支持**: 静态资源 CDN 加速
- **代码分割**: 前端代码按需加载
- **图片优化**: 图片压缩和懒加载

### 🚀 部署运维

- **Docker 支持**: 一键容器化部署
- **多环境配置**: 开发、测试、生产环境分离
- **日志管理**: 结构化日志记录和分析
- **监控告警**: 应用性能监控
- **备份恢复**: 数据库自动备份机制

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
cd script && docker-compose up -d
# Linux/Mac:
# cd script && docker-compose up -d
```

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
cd script && docker-compose up -d
```

#### 3️⃣ 传统方式部署

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

## 👨‍💻 开发说明

### 🔧 后端开发规范

```java
// 示例：统一返回格式
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @GetMapping
    public Result<PageInfo<Article>> getArticles(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return Result.success(articleService.getArticles(pageNum, pageSize));
    }
}
```

**开发要点：**

- 📋 遵循 Spring Boot 最佳实践和阿里巴巴 Java 开发手册
- 🗃️ 使用 MyBatis-Plus 简化数据库操作，支持分页和条件构造器
- 🏗️ 采用三层架构：Controller → Service → Mapper
- 📝 使用 Lombok 减少样板代码，提高开发效率
- 🔌 RESTful API 设计，统一返回格式 `Result<T>`
- ✅ 参数校验使用 `@Valid` 和 `@Validated` 注解
- 📊 集成 Swagger/OpenAPI 3.0 用于 API 文档生成

### 🎨 前端开发规范

```vue
<!-- 示例：组件开发模板 -->
<template>
  <div class="article-list">
    <el-card v-for="article in articles" :key="article.id">
      <h3>{{ article.title }}</h3>
      <p>{{ article.summary }}</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getArticles } from "@/api/article";

const articles = ref([]);

const loadArticles = async () => {
  const { data } = await getArticles();
  articles.value = data.list;
};

onMounted(() => {
  loadArticles();
});
</script>
```

**开发要点：**

- 🎯 使用 Vue 3 Composition API，代码更加清晰和可复用
- 🗃️ Pinia 状态管理，支持状态持久化
- 🎨 Element Plus 组件库，确保 UI 一致性
- 📱 响应式设计，使用 CSS Grid 和 Flexbox
- 🔧 Vite 构建工具，支持热更新和快速构建
- 📦 自动导入插件，减少重复的 import 语句
- 🔒 代码混淆和压缩，保护前端代码

### 🧪 测试和质量保证

- **单元测试**: JUnit 5 + Mockito (后端)，Vitest (前端)
- **集成测试**: TestContainers 用于数据库集成测试
- **代码质量**: SonarQube 代码质量检测
- **API 测试**: Postman 集合和自动化测试脚本

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
