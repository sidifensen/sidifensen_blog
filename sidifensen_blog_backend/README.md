# Sidifensen Blog 后端项目

> 基于 Spring Boot 3.4.0 + Java 21 的现代化博客系统后端

## 📖 项目简介

功能完善的博客系统后端服务，采用 Spring Boot 3.4.0 + Java 21 构建，提供用户管理、文章管理、评论互动、相册管理等核心功能。

## ✨ 核心特性

- 🔐 **认证授权**：Spring Security + JWT，支持用户名/邮箱登录、OAuth 第三方登录（Gitee/GitHub/QQ）
- 📝 **文章管理**：文章发布、编辑、审核、专栏管理、标签分类
- 💬 **评论互动**：多级评论、点赞、收藏、浏览历史
- 💌 **私信系统**：WebSocket 实时通信、消息已读/未读、撤回删除、在线状态
- 🔔 **通知中心**：系统通知、互动提醒（点赞、评论、关注、收藏）、分类管理、已读状态
- 📷 **相册管理**：基于 MinIO 的图片存储
- 🔒 **安全防护**：接口限流、IP 黑名单、内容审核、XSS 防护
- 🚀 **性能优化**：Redis 缓存、连接池优化、异步任务处理
- 📮 **消息队列**：RabbitMQ 实现邮件发送、审核通知
- 🤖 **AI 能力**：DeepSeek API 智能提取文章摘要，配额管理、限流保护

## 🛠 技术栈

### 核心框架

- Spring Boot 3.4.0
- Spring Security 6.4.1
- MyBatis-Plus 3.5.14
- Spring AI 1.0.0
- Spring WebSocket 6.4.1

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
- DeepSeek API（AI 摘要提取）

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

# AI配置（DeepSeek API）
DEEPSEEK_API_KEY=your_deepseek_api_key
DEEPSEEK_API_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat

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

## 💬 私信功能

### 私信系统

项目集成了 WebSocket 实时通信，实现了完整的私信聊天功能。

#### 功能特性

- 💌 **实时通信**：基于 WebSocket 的实时消息推送，即时送达
- 📊 **会话管理**：会话列表展示，支持最后消息预览和未读计数
- 🔔 **消息状态**：已读/未读状态管理，实时更新
- ↩️ **消息撤回**：支持消息撤回功能（有时间限制）
- 🗑️ **消息删除**：支持删除单条消息或整个会话
- 👤 **在线状态**：实时显示用户在线/离线状态
- 📜 **历史记录**：分页加载聊天历史记录

#### API 接口

##### 1. 获取聊天历史

```http
GET /message/history?targetUserId=2&pageNum=1&pageSize=20
Authorization: <JWT Token>
```

**请求参数：**

- `targetUserId`: 目标用户 ID
- `pageNum`: 页码
- `pageSize`: 每页数量

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "data": [
      {
        "id": 1,
        "fromUserId": 1,
        "toUserId": 2,
        "content": "你好",
        "isRead": true,
        "isRevoked": false,
        "createTime": "2025-11-07 10:00:00"
      }
    ],
    "total": 50
  }
}
```

##### 2. 撤回消息

```http
PUT /message/revoke/{messageId}
Authorization: <JWT Token>
```

**路径参数：**

- `messageId`: 消息 ID

##### 3. 删除消息

```http
DELETE /message/{messageId}
Authorization: <JWT Token>
```

**路径参数：**

- `messageId`: 消息 ID

##### 4. 获取未读消息数

```http
GET /message/unread/count
Authorization: <JWT Token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

#### WebSocket 连接

##### 连接地址

```
ws://localhost:5000/ws/message
```

##### 连接参数

在建立 WebSocket 连接时，需要在 URL 中传递 JWT Token：

```javascript
const token = "your_jwt_token";
const ws = new WebSocket(`ws://localhost:5000/ws/message?token=${token}`);
```

##### 消息格式

**发送消息：**

```json
{
  "type": "PRIVATE_MESSAGE",
  "toUserId": 2,
  "content": "你好"
}
```

**接收消息：**

```json
{
  "type": "PRIVATE_MESSAGE",
  "data": {
    "id": 1,
    "fromUserId": 1,
    "toUserId": 2,
    "content": "你好",
    "createTime": "2025-11-07 10:00:00"
  }
}
```

**在线状态通知：**

```json
{
  "type": "USER_ONLINE",
  "userId": 2
}
```

```json
{
  "type": "USER_OFFLINE",
  "userId": 2
}
```

**消息已读通知：**

```json
{
  "type": "MESSAGE_READ",
  "fromUserId": 2,
  "toUserId": 1
}
```

**消息撤回通知：**

```json
{
  "type": "MESSAGE_REVOKE",
  "messageId": 1
}
```

#### 技术实现

- **框架**: Spring WebSocket 6.4.1
- **协议**: WebSocket（基于 STOMP 协议的文本消息）
- **会话管理**: 内存中的 ConcurrentHashMap 管理用户会话
- **消息持久化**: MySQL 存储所有聊天记录
- **状态同步**: Redis 缓存用户在线状态和未读计数

#### 配置说明

WebSocket 相关配置已在 `WebSocketConfig.java` 中完成，无需额外配置。默认端点：

- WebSocket 端点：`/ws/message`
- 允许跨域：所有来源（生产环境建议限制）

## 🔔 通知中心

### 通知系统

项目提供了完整的通知系统，用户可以接收各种互动通知（点赞、评论、关注、收藏）和系统通知。

#### 功能特性

- 📬 **多类型通知**：支持系统通知、点赞、评论、关注、收藏等多种通知类型
- 🔔 **实时推送**：基于 WebSocket 的实时通知推送
- 📊 **分类筛选**：支持按通知类型筛选查看
- 🔍 **已读管理**：支持标记已读/未读，批量标记已读
- 📜 **历史记录**：分页查看所有历史通知
- 🔢 **未读统计**：实时显示未读通知数量

#### API 接口

##### 1. 获取通知列表

```http
GET /notification/list?pageNum=1&pageSize=20&type=
Authorization: <JWT Token>
```

**请求参数：**

- `pageNum`: 页码（必填）
- `pageSize`: 每页数量（必填）
- `type`: 通知类型（可选，不传则查询全部）
  - `SYSTEM`: 系统通知
  - `LIKE`: 点赞通知
  - `COMMENT`: 评论通知
  - `FOLLOW`: 关注通知
  - `FAVORITE`: 收藏通知

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "data": [
      {
        "id": 1,
        "userId": 1,
        "type": "LIKE",
        "title": "点赞通知",
        "content": {
          "triggerUserId": 2,
          "triggerUserName": "张三",
          "triggerUserAvatar": "https://example.com/avatar.jpg",
          "targetId": 123,
          "targetType": "ARTICLE",
          "targetTitle": "我的第一篇文章",
          "actionTime": "2025-11-08 10:00:00"
        },
        "isRead": false,
        "createTime": "2025-11-08 10:00:00"
      }
    ],
    "total": 50
  }
}
```

##### 2. 标记通知已读

```http
PUT /notification/read/{notificationId}
Authorization: <JWT Token>
```

**路径参数：**

- `notificationId`: 通知 ID

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功"
}
```

##### 3. 批量标记已读

```http
PUT /notification/read/batch
Authorization: <JWT Token>
Content-Type: application/json

{
  "notificationIds": [1, 2, 3]
}
```

**请求参数：**

- `notificationIds`: 通知 ID 数组

##### 4. 全部标记已读

```http
PUT /notification/read/all
Authorization: <JWT Token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功"
}
```

##### 5. 获取未读通知数量

```http
GET /notification/unread/count
Authorization: <JWT Token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 10,
    "system": 2,
    "like": 3,
    "comment": 2,
    "follow": 1,
    "favorite": 2
  }
}
```

##### 6. 删除通知

```http
DELETE /notification/{notificationId}
Authorization: <JWT Token>
```

**路径参数：**

- `notificationId`: 通知 ID

#### WebSocket 通知推送

##### 消息格式

**实时通知推送：**

```json
{
  "type": "NOTIFICATION",
  "data": {
    "id": 1,
    "userId": 1,
    "type": "LIKE",
    "title": "点赞通知",
    "content": {
      "triggerUserId": 2,
      "triggerUserName": "张三",
      "targetId": 123,
      "targetType": "ARTICLE",
      "targetTitle": "我的第一篇文章"
    },
    "isRead": false,
    "createTime": "2025-11-08 10:00:00"
  }
}
```

**未读数量更新：**

```json
{
  "type": "UNREAD_COUNT",
  "count": 5
}
```

#### 通知触发场景

系统会在以下场景自动发送通知：

1. **点赞通知**：文章或评论被点赞时
2. **评论通知**：文章被评论或评论被回复时
3. **关注通知**：被其他用户关注时
4. **收藏通知**：文章被收藏时
5. **系统通知**：管理员发送系统公告、审核结果等

#### 技术实现

- **存储**: MySQL 存储所有通知记录
- **缓存**: Redis 缓存未读数量统计
- **推送**: WebSocket 实时推送新通知
- **消息队列**: RabbitMQ 异步处理通知生成，避免阻塞主流程

#### 数据模型

通知内容（`NotificationContentDto`）结构：

```java
{
  "triggerUserId": 2,           // 触发通知的用户ID
  "triggerUserName": "张三",     // 触发用户昵称
  "triggerUserAvatar": "...",   // 触发用户头像
  "targetId": 123,              // 目标对象ID（文章ID/评论ID等）
  "targetType": "ARTICLE",      // 目标类型（ARTICLE/COMMENT/USER）
  "targetTitle": "文章标题",     // 目标标题
  "actionTime": "2025-11-08"    // 操作时间
}
```

## 🤖 AI 功能

### AI 摘要提取

项目集成了 DeepSeek API，可以智能提取文章摘要，帮助用户快速生成文章概述。

#### 功能特性

- ✨ **智能提取**：基于 DeepSeek 大语言模型自动分析文章内容，生成精准摘要
- 📊 **配额管理**：每个用户每天最多调用 5 次（硬编码），防止滥用
- 🚦 **限流保护**：单个接口每小时最多调用 2 次，避免频繁请求
- 🔄 **实时查询**：支持查询当前用户的剩余配额

#### API 接口

##### 1. 提取文章摘要

```http
POST /ai/extractSummary
Authorization: <JWT Token>
Content-Type: application/json

{
  "content": "<h1>文章标题</h1><p>文章正文内容...</p>"
}
```

**请求参数：**

- `content`: 文章内容（HTML 格式）

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "这是一篇关于 Spring Boot 开发的文章，介绍了如何集成 AI 功能..."
}
```

**限流规则：**

- 每小时最多调用 2 次
- 超出限制返回：`AI摘要提取过于频繁，请1小时后再试`

##### 2. 查询配额

```http
GET /ai/quota
Authorization: <JWT Token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 3
}
```

**返回值说明：**

- 返回用户今日剩余的 AI 调用次数

#### 配置说明

在 `.env` 文件中配置 DeepSeek API：

```bash
# DeepSeek API 密钥
DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxx

# API 端点（默认使用 DeepSeek 官方地址）
DEEPSEEK_API_BASE_URL=https://api.deepseek.com

# 使用的模型
DEEPSEEK_MODEL=deepseek-chat
```

**配额说明：**

- 每个用户每天最多调用 5 次（代码中硬编码，不可配置）
- 如需修改配额，请在 `AiUsageServiceImpl.java` 中修改 `DAILY_LIMIT` 常量

#### 技术实现

- **框架**: Spring AI 1.0.0（OpenAI 兼容接口）
- **模型**: DeepSeek Chat（通过 OpenAI API 适配）
- **缓存**: Redis 存储用户配额信息
- **限流**: AOP 注解 `@RateLimit` 实现接口级限流

#### 获取 API Key

1. 访问 [DeepSeek 官网](https://platform.deepseek.com/)
2. 注册并登录账号
3. 在控制台创建 API Key
4. 将 Key 配置到 `.env` 文件中

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

### 5. AI 功能相关问题

#### AI 摘要提取失败

**可能原因**：

- DeepSeek API Key 未配置或无效
- API Key 余额不足
- 网络连接问题

**解决方案**：

1. 检查 `.env` 文件中的 `DEEPSEEK_API_KEY` 配置
2. 访问 [DeepSeek 控制台](https://platform.deepseek.com/) 检查余额
3. 确保服务器可以访问 `https://api.deepseek.com`

#### AI 配额用完

**问题**：提示"今日配额已用完"

**解决方案**：

- 配额每天 00:00 自动重置
- 如需修改配额限制，需在代码 `AiUsageServiceImpl.java` 中修改 `DAILY_LIMIT` 常量并重新部署
- 等待次日自动恢复配额

#### AI 接口限流

**问题**：提示"AI 摘要提取过于频繁，请 1 小时后再试"

**解决方案**：

- 单个接口每小时最多调用 2 次
- 等待 1 小时后重试
- 避免频繁调用，优化使用策略

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
