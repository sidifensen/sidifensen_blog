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

1. **现代化技术栈**: 使用最新的Spring Boot 3和Vue 3
2. **完整的功能体系**: 从内容管理到用户交互的完整博客生态
3. **安全性**: 集成Spring Security和内容安全检测
4. **性能优化**: Redis缓存、RabbitMQ异步处理
5. **可扩展性**: 模块化设计，易于功能扩展
6. **开发体验**: 完善的开发工具链和配置

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
```bash
# 导入主数据库结构
mysql -u root -p < sql/sidifensen_blog.sql

# 导入控制台相关数据
mysql -u root -p < sql/console.sql
```

## 部署说明

### 后端部署
1. 配置 `application.yml` 中的数据库、Redis、RabbitMQ等连接信息
2. 配置MinIO存储服务
3. 配置阿里云图片安全检测服务
4. 使用 `mvn clean package` 打包
5. 运行生成的jar文件

### 前端部署
1. 使用 `npm run build` 构建生产版本
2. 将dist目录部署到Web服务器
3. 配置反向代理指向后端API

## 开发说明

### 后端开发
- 遵循Spring Boot最佳实践
- 使用MyBatis-Plus简化数据库操作
- 集成Spring Security保障系统安全
- 使用Redis提升系统性能
- 集成RabbitMQ处理异步任务

### 前端开发
- 使用Vue 3 Composition API
- Element Plus组件库提供统一UI风格
- Pinia进行状态管理
- Vite提供快速的开发体验
- 支持TypeScript开发

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
