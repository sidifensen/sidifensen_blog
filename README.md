# Sidifensen Blog 个人博客系统

## 项目概述

**sidifensen_blog** 是一个完整的个人博客系统，采用前后端分离的架构设计。这是一个功能丰富的现代化博客平台，包含了用户端和管理端两个前端应用。

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.1.4 (Java 21)
- **安全**: Spring Security
- **数据库**: MySQL 8.1.0 + MyBatis-Plus 3.5.3.1
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **文件存储**: MinIO
- **模板引擎**: Thymeleaf
- **其他工具**: 
  - Lombok (代码简化)
  - Hutool (工具库)
  - FastJSON (JSON处理)
  - JustAuth (第三方登录)
  - 阿里云图片内容安全检测

### 前端技术栈
- **框架**: Vue 3.5.13 + Vite 6.2.4
- **UI组件库**: Element Plus 2.10.2
- **状态管理**: Pinia 3.0.1
- **路由**: Vue Router 4.5.0
- **HTTP客户端**: Axios 1.10.0
- **样式**: Sass
- **图标**: Element Plus Icons + SVG Icons

> 所有技术栈均使用当前最新稳定版本，确保系统性能和安全性。

## 项目结构

```
sidifensen_blog/
├── sidifensen_blog_backend/          # 后端服务 (Spring Boot)
│   ├── src/main/java/com/sidifensen/
│   │   ├── controller/               # 控制器层
│   │   ├── service/                  # 业务逻辑层
│   │   ├── mapper/                   # 数据访问层
│   │   ├── domain/                   # 实体类
│   │   ├── security/                 # 安全配置
│   │   ├── redis/                    # Redis配置
│   │   ├── minio/                    # 文件存储配置
│   │   ├── rabbitmq/                 # 消息队列
│   │   └── utils/                    # 工具类
│   └── pom.xml                       # Maven配置
├── sidifensen_blog_frontend/         # 前端应用
│   ├── sidifensen_user/              # 用户端 (Vue 3)
│   └── sidifensen_admin/             # 管理端 (Vue 3)
└── sql/                              # 数据库脚本
    ├── sidifensen_blog.sql           # 主数据库结构
    └── console.sql                   # 控制台相关
```

## 主要功能特性

### 后端功能
1. **用户认证与授权**: Spring Security + JWT
2. **内容管理**: 博客文章、分类、标签管理
3. **文件管理**: MinIO对象存储
4. **缓存系统**: Redis缓存提升性能
5. **消息队列**: RabbitMQ异步处理
6. **第三方登录**: JustAuth支持多种OAuth
7. **内容安全**: 阿里云图片内容检测
8. **邮件服务**: 验证码、通知邮件
9. **验证码**: 图形验证码生成

### 前端功能
1. **用户端**: 博客展示、文章阅读、用户交互
2. **管理端**: 内容管理、用户管理、系统配置
3. **响应式设计**: 支持多设备访问
4. **现代化UI**: Element Plus组件库
5. **状态持久化**: Pinia状态管理

## 项目特色

1. **完整的功能体系**: 从内容管理到用户交互的完整博客生态
2. **安全性**: 集成Spring Security和内容安全检测
3. **性能优化**: Redis缓存、RabbitMQ异步处理
4. **可扩展性**: 模块化设计，易于功能扩展
5. **开发体验**: 完善的开发工具链和配置
6. **前后端分离**: 采用现代化前后端分离架构，提高开发效率

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

### 后端启动
```bash
cd sidifensen_blog_backend
mvn spring-boot:run
```

### 前端启动
```bash
# 用户端
cd sidifensen_blog_frontend/sidifensen_user
npm install
npm run dev

# 管理端
cd sidifensen_blog_frontend/sidifensen_admin
npm install
npm run dev
```

### 数据库初始化
1. 创建MySQL数据库：`sidifensen_blog`
2. 执行SQL脚本：`sql/sidifensen_blog.sql`
3. 根据需要执行控制台相关脚本：`sql/console.sql`

## 部署教程

### 环境准备
1. 确保Docker已正确安装并运行
2. 准备好数据库、Redis、RabbitMQ等服务
3. 配置好MinIO存储服务和阿里云图片安全检测服务（如使用）

### 后端部署
1. 配置 `application.yml` 中的数据库、Redis、RabbitMQ等连接信息
2. 使用 `mvn clean package` 打包项目，生成JAR文件
3. 将打包完成的JAR文件、`docker.sh`脚本及`Dockerfile`放置于同一目录下
4. 执行以下命令为脚本添加执行权限：
   ```bash
   chmod +x docker.sh
   ```
5. 运行脚本完成部署：
   ```bash
   ./docker.sh
   ```

### 前端部署
1. 使用 `npm run build` 构建生产版本，生成`dist`文件夹
2. 将构建生成的`dist`文件夹、`default.conf`配置文件、`docker.sh`脚本及`Dockerfile`放置于同一目录下
3. 执行以下命令为脚本添加执行权限：
   ```bash
   chmod +x docker.sh
   ```
4. 运行脚本完成部署：
   ```bash
   ./docker.sh
   ```

> 注意：根据实际情况调整配置文件中的参数。

## 访问应用
部署完成后，可以通过以下地址访问应用：
- 用户端: http://localhost:7000
- 管理端: http://localhost:8000

如果使用了自定义端口或域名，请相应调整访问地址。

## 开发说明

### 后端开发
- 遵循Spring Boot最佳实践
- 使用MyBatis-Plus简化数据库操作
- 代码规范：遵循Java编码规范，使用Lombok减少样板代码
- API设计：RESTful风格API，返回统一格式的响应

### 前端开发
- 使用Vue 3 Composition API进行组件开发
- 状态管理：使用Pinia管理应用状态
- 代码规范：遵循ESLint规则，使用Prettier格式化代码
- 支持JavaScript开发

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交GitHub Issue
- 发送邮件至项目维护者

---

**这是一个设计精良、功能完整的个人博客系统，适合作为学习Spring Boot和Vue 3全栈开发的参考项目，也可以直接部署使用。**
