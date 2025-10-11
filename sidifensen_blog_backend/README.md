# Sidifensen Blog 后端项目

> 基于 Spring Boot 3.1.4 + Java 21 的现代化博客系统后端

## 📖 项目简介

功能完善的博客系统后端服务，采用 Spring Boot 3.1.4 + Java 21 构建，提供用户管理、文章管理、评论互动、相册管理等核心功能。

## ✨ 核心特性

- 🔐 **认证授权**：Spring Security + JWT，支持用户名/邮箱登录、OAuth 第三方登录（Gitee/GitHub/QQ）
- 📝 **文章管理**：文章发布、编辑、审核、专栏管理、标签分类
- 💬 **评论互动**：多级评论、点赞、收藏、浏览历史
- 📷 **相册管理**：基于 MinIO 的图片存储
- 🔒 **安全防护**：接口限流、IP 黑名单、内容审核、XSS 防护
- 🚀 **性能优化**：Redis 缓存、连接池优化、异步任务处理
- 📮 **消息队列**：RabbitMQ 实现邮件发送、审核通知

## 🛠 技术栈

### 核心框架

- Spring Boot 3.1.4
- Spring Security
- MyBatis-Plus 3.5.12

### 数据存储

- MySQL 8.x
- Redis
- MinIO

### 中间件

- RabbitMQ

### 第三方服务

- JustAuth 1.16.7（OAuth 登录）
- 阿里云内容安全（图片/文本审核）
- QQ 邮箱（邮件发送）

### 工具库

- Hutool 5.8.38
- FastJSON 2.0.50
- Lombok
- Easy Captcha 1.6.2

## 🚀 快速开始

### 前置要求

- **JDK 21**（必须）
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis 6.0+**
- **RabbitMQ 3.x+**（邮件功能需要）
- **MinIO**（图片上传需要）

### 环境配置

当前项目使用环境变量配置, 可以避免配置硬编码 ,在运行前需要配置好相关环境变量,

在 src/main/java/com/sidifensen/目录下创建 `.env` 文件,

然后用 idea 启动的话就右键 Main 方法选择->更多运行/调试->修改运行配置->在构建并运行右侧的修改选项里勾选环境变量, 并选择当前的.env

以下是.env 文件的示例：

```bash
# 数据库配置
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=sidifensen_blog
MYSQL_USERNAME=root
MYSQL_PASSWORD=root

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=

# RabbitMQ配置
RABBITMQ_HOST=localhost
RABBITMQ_PORT=30000
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# MinIO配置
MINIO_ENDPOINT=http://localhost:9000
MINIO_PUBLIC_POINT=http://localhost:9000
MINIO_ACCESS_KEY=minio
MINIO_SECRET_KEY=minio

# 邮件配置（QQ邮箱）
MAIL_USERNAME=your_email@qq.com
MAIL_PASSWORD=your_qq_mail_authorization_code

# JWT密钥
SIDIFENSEN_JWT_SECRET=your_jwt_secret_key

# OAuth配置（可选）
GITEE_CLIENT_ID=your_gitee_client_id
GITEE_CLIENT_SECRET=your_gitee_client_secret
GITEE_REDIRECT_URI=http://localhost:5000/oauth/gitee/callback

GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GITHUB_REDIRECT_URI=http://localhost:5000/oauth/github/callback

QQ_CLIENT_ID=your_qq_client_id
QQ_CLIENT_SECRET=your_qq_client_secret
QQ_REDIRECT_URI=http://localhost:5000/oauth/qq/callback

# 阿里云内容审核（可选）
ALIYUN_ACCESS_KEY_ID=your_aliyun_access_key_id
ALIYUN_ACCESS_KEY_SECRET=your_aliyun_access_key_secret

# 前端地址
FRONTEND_USER_HOST=http://localhost:7000/
FRONTEND_ADMIN_HOST=http://localhost:8000/

# 自动审核配置
SIDIFENSEN_PHOTO_AUTO_AUDIT=false
SIDIFENSEN_ARTICLE_AUTO_AUDIT=false
SIDIFENSEN_COMMENT_AUTO_AUDIT=false

# 运行环境
SPRING_PROFILES_ACTIVE=dev
```

### 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE sidifensen_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p sidifensen_blog < ../../sql/sidifensen_blog.sql
```

### 编译运行

```bash
# 编译打包
mvn clean package -DskipTests

# 运行
java -jar target/sidifensen_blog_backend-1.0-SNAPSHOT.jar
```

或使用 IDEA 直接运行 `Main.java`

### 验证

```bash
curl http://localhost:5000/actuator/health
```

## 🔐 登录认证

### 支持的登录方式

1. **用户名/邮箱 + 密码**（需验证码）
2. **管理员登录**（无需验证码）
3. **OAuth 登录**（Gitee/GitHub/QQ）

### 登录流程

#### 1. 获取验证码

```http
GET /user/checkCode
```

返回：验证码图片（Base64）和 key

#### 2. 用户登录

```http
POST /user/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "123456",
  "rememberMe": false,
  "checkCodeKey": "checkCode:uuid-xxx",
  "checkCode": "8"
}
```

返回：JWT Token

#### 3. 访问需要认证的接口需要在请求头加上 Authorization: 用户登录的 jwt

```http
GET /article/my/list?pageNum=1&pageSize=10
Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token 说明

- **有效期**：rememberMe=true（7 天），false（1 天）
- **携带方式**：请求头 `Authorization`
- **验证失败**：返回 401 Unauthorized

## 🐳 Docker 部署

项目提供了两个 Docker 相关文件来简化部署流程：

### 📄 Dockerfile

Docker 镜像构建配置文件，用于创建后端应用的 Docker 镜像。

**主要配置：**

- 基础镜像：`eclipse-temurin:21-alpine`（JDK 21）
- 时区设置：`Asia/Shanghai`（中国标准时间）
- 工作目录：`/app`
- 应用端口：`5000`
- JAR 文件：`target/sidifensen_blog_backend-1.0-SNAPSHOT.jar`

### 🔧 docker.sh

一键部署脚本，自动完成镜像构建和容器启动。

**主要功能：**

1. 检查 JAR 文件是否存在
2. 停止并删除旧容器
3. 删除旧镜像
4. 构建新镜像
5. 启动新容器并映射日志目录到宿主机 `./logs`

**使用方法：**

```bash
# 1. 先编译项目
mvn clean package -DskipTests

# 2. 运行部署脚本
chmod +x docker.sh
./docker.sh
```

> 💡 **提示**：使用 `docker.sh` 脚本时，日志会自动映射到项目根目录的 `logs/` 文件夹，方便查看和管理。

### 使用 docker-compose（推荐）

如需同时部署所有服务（MySQL、Redis、RabbitMQ、MinIO 等），使用项目根目录的 docker-compose：

```bash
cd ../../script
docker-compose up -d
```

### 手动构建（不推荐）

```bash
# 编译
mvn clean package -DskipTests

# 构建镜像
docker build -t sidifensen-blog-backend:latest .

# 运行
docker run -d \
  --name sidifensen-blog-backend \
  -p 5000:5000 \
  -e MYSQL_HOST=mysql \
  -e REDIS_HOST=redis \
  sidifensen-blog-backend:latest
```

## 📝 日志说明

### 日志文件位置

日志文件存放在项目根目录的 `logs/` 文件夹中：

```
sidifensen_blog/
├── logs/
│   ├── sidifensen-blog.log              # 当前普通日志
│   ├── sidifensen-blog.2025-10-11.0.log # 历史日志（按日期归档）
│   ├── sidifensen-blog-error.log        # 当前错误日志
│   └── sidifensen-blog-error.2025-10-11.0.log # 历史错误日志
```

### 日志配置

- **保留期限**：30 天
- **单文件大小**：最大 100MB
- **滚动策略**：按日期和文件大小自动滚动
- **日志级别**：
  - 开发环境（dev）：INFO
  - 生产环境（prod）：INFO（Spring/MyBatis 为 WARN）

### 修改日志存储位置

如需修改日志存放路径，编辑 `src/main/resources/logback-spring.xml`：

```xml
<!-- 修改第 8 行 -->
<property name="LOG_HOME" value="./logs"/>
```

## 🔧 常见问题

### 1. 数据库连接失败

检查：MySQL 是否启动、配置是否正确、端口是否开放

### 2. Redis 连接失败

检查：Redis 是否启动、密码是否配置

### 3. Token 相关错误

- **请先登录**：未携带 Authorization 或 Token 为空
- **登录过期**：Token 过期，需重新登录
- **账号被禁用**：联系管理员

### 4. RabbitMQ/MinIO 未配置

**问题**：项目能否在不配置的情况下运行？

**回答**：❌ 不能。RabbitMQ 和 MinIO 是硬依赖。

**解决方案**：

```bash
# 推荐：使用 Docker Compose
cd ../../script
docker-compose up -d

# 或单独启动
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
docker run -d --name minio -p 9000:9000 -p 9001:9001 minio/minio server /data
```

## 🛠 调试建议

### Apifox 调试

1. 获取验证码：`GET /user/checkCode`
2. 登录获取 Token：`POST /user/login`
3. 设置环境变量：`token = {{返回的Token}}`
4. 使用请求头：`Authorization: {{token}}`

## 📄 许可证

MIT License

---

**版本**：1.0-SNAPSHOT  
**最后更新**：2025-10-11
