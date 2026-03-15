# 🐳 Sidifensen Blog Docker 部署指南

## 📋 目录

- [Docker Compose 简介](#docker-compose-简介)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [服务说明](#服务说明)
- [环境配置](#环境配置)
- [部署步骤](#部署步骤)
- [常用命令](#常用命令)
- [故障排除](#故障排除)

## 🐳 Docker Compose 简介

Docker Compose 是一个用于定义和运行多容器 Docker 应用程序的工具。它使用 YAML 文件来配置应用程序的服务，然后通过一个命令就可以创建并启动所有服务。

### 优势

- **一键部署**：通过 `docker-compose up` 启动整个应用栈
- **服务编排**：自动处理服务间的依赖关系
- **环境隔离**：每个服务运行在独立的容器中
- **配置管理**：统一的配置文件管理
- **数据持久化**：通过数据卷保存数据

## 📁 项目结构

```
sidifensen_blog/
├── script/                     # 脚本目录
│   ├── docker-compose.yml          # 生产环境配置
│   ├── docker-compose-service.yml  # 基础服务配置（MySQL、Redis、MinIO、RabbitMQ）
│   ├── docker-compose-apps.yml     # 应用服务配置（后端+前端）
│   ├── env.example                 # 环境变量示例
│   ├── start.sh                    # Linux/Mac 启动脚本
│   ├── start.bat                   # Windows 启动脚本
│   ├── README-Docker.md           # Docker 部署文档
│   └── deploy/                     # 服务器部署脚本
│       ├── deploy.sh              # 一键部署脚本
│       ├── post-receive-hook.sh   # Git Hook 自动部署
│       └── README.md              # 部署脚本说明
├── sidifensen_blog_backend/   # 后端服务
│   ├── Dockerfile
│   └── src/main/resources/
│       └── application-docker.yaml
├── sidifensen_blog_frontend/  # 前端服务
│   ├── sidifensen_admin/
│   │   └── Dockerfile
│   └── sidifensen_user/
│       └── Dockerfile
└── sql/
    └── sidifensen_blog.sql    # 数据库初始化脚本
```

## 🚀 快速开始

### 1. 环境准备

确保已安装：

- Docker (20.10+)
- Docker Compose (2.0+)
- Git (用于代码管理和部署)

#### Git 安装指南

**Ubuntu/Debian:**

```bash
sudo apt update
sudo apt install git
```

**CentOS/RHEL/Fedora:**

```bash
# CentOS/RHEL
sudo yum install git
# 或者使用 dnf (较新版本)
sudo dnf install git

# Fedora
sudo dnf install git
```

**macOS:**

```bash
# 使用 Homebrew
brew install git

# 或者从官网下载安装包
# https://git-scm.com/download/mac
```

**Windows:**

```bash
# 使用 Chocolatey
choco install git

# 或者从官网下载安装包
# https://git-scm.com/download/win
```

**验证安装：**

```bash
git --version
```

### 2. 克隆项目

```bash
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog
```

### 3. 配置环境变量

```bash
# Linux/macOS
cp script/dev/.env.example .env
vim .env

# Windows
copy script/dev/.env.example .env
notepad .env
```

### 4. 构建并启动服务

#### 方式一：使用启动脚本（推荐）

```bash
# 进入 script 目录
cd script

# Linux/Mac 用户
./start.sh

# Windows 用户
start.bat
```

启动脚本提供了交互式菜单，包括：

- 启动生产环境（完整服务）
- 启动开发环境（仅基础服务）
- 启动应用服务（后端+前端）
- 停止所有服务
- 查看服务状态和日志
- 重启服务
- 清理数据等功能

#### 方式二：手动命令

**生产环境**

```bash
# 进入 script 目录
cd script

# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

**开发环境（仅基础服务）**

```bash
# 进入 script 目录
cd script

# 启动四个基础服务：MySQL、Redis、MinIO、RabbitMQ
docker-compose -f dev/docker-compose-service.yml up -d

# 查看基础服务状态
docker-compose -f dev/docker-compose-service.yml ps

# 此时可以在 IDE 中启动后端和前端应用进行开发
```

**应用服务**

```bash
# 进入 script 目录
cd script

# 只启动应用服务（后端+前端，需要基础服务已运行）
docker-compose -f dev/docker-compose-apps.yml up -d --build

# 查看应用服务状态
docker-compose -f dev/docker-compose-apps.yml ps

# 查看应用服务日志
docker-compose -f dev/docker-compose-apps.yml logs -f
```

#### 方式三：服务器部署

**一键部署到生产服务器：**

```bash
# 在服务器上运行
cd script/jenkins
sudo ./deploy.sh
```

**Git Hook 自动部署：**

```bash
# 设置 Git Hook（在服务器上）
cp script/jenkins/post-receive-hook.sh /path/to/repo.git/hooks/post-receive
chmod +x /path/to/repo.git/hooks/post-receive

# 之后每次推送代码到 main 分支时会自动部署
git push origin main
```

详细的服务器部署说明请参考：[deploy/README.md](deploy/README.md)

### 5. 访问服务

- **后端 API**: http://localhost:${BACKEND_PORT}（默认 5000）
- **管理端前端**: http://localhost:${ADMIN_PORT}（默认 8000）
- **用户端前端**: http://localhost:${USER_PORT}（默认 7000）
- **MinIO 控制台**: http://localhost:${MINIO_CONSOLE_PORT}（默认 9001）
- **RabbitMQ 管理界面**: http://localhost:${RABBITMQ_MANAGEMENT_PORT}（默认 15672）

## 🛠 服务说明

项目包含以下服务和配置文件：

### Docker Compose 配置文件

1. **docker-compose.yml** - 生产环境完整配置

   - 包含所有服务：数据库、缓存、消息队列、对象存储、后端、前端
   - 适用于生产环境部署

2. **docker-compose-service.yml** - 基础服务配置

   - **仅包含四个基础服务**：MySQL、Redis、MinIO、RabbitMQ
   - **不包含应用服务**：后端和前端需要在 IDE 中单独启动
   - 适用于本地开发调试，提供必要的基础设施支持

3. **docker-compose.apps.yml** - 应用服务配置
   - 只包含应用服务：后端、前端管理端、前端用户端
   - 依赖外部基础服务，适用于应用服务的独立部署和更新

### 服务详情

### 1. MySQL 数据库

- **镜像**：mysql:8.0
- **端口**：`${MYSQL_PORT}:3306`（默认 3306）
- **数据卷**：mysql_data
- **环境变量**：
  - MYSQL_ROOT_PASSWORD：MySQL root 用户密码
  - MYSQL_DATABASE：应用使用的数据库名称
  - MYSQL_USER：应用连接数据库的用户名
  - MYSQL_PASSWORD：应用连接数据库的密码

### 2. Redis 缓存

- **镜像**：redis:7-alpine
- **端口**：`${REDIS_PORT}:6379`（默认 6379）
- **数据卷**：redis_data
- **环境变量**：
  - REDIS_USERNAME：Redis 用户名
  - REDIS_PASSWORD：Redis 访问密码

### 3. MinIO 对象存储

- **镜像**：minio/minio:RELEASE.2025-04-08T15-41-24Z
- **API 端口**：`${MINIO_API_PORT}:9000`（默认 9000）
- **控制台端口**：`${MINIO_CONSOLE_PORT}:9001`（默认 9001）
- **数据卷**：minio_data
- **环境变量**：
  - MINIO_ROOT_USER：MinIO 访问密钥
  - MINIO_ROOT_PASSWORD：MinIO 密钥
  - MINIO_PUBLIC_POINT：MinIO 公网访问地址

### 4. RabbitMQ 消息队列

- **镜像**：rabbitmq:3-management-alpine
- **服务端口**：`${RABBITMQ_PORT}:5672`（默认 5672）
- **管理界面端口**：`${RABBITMQ_MANAGEMENT_PORT}:15672`（默认 15672）
- **数据卷**：rabbitmq_data
- **环境变量**：
  - RABBITMQ_DEFAULT_USER：RabbitMQ 管理员用户名
  - RABBITMQ_DEFAULT_PASS：RabbitMQ 管理员密码

### 5. 后端服务

- **构建上下文**：../sidifensen_blog_backend
- **端口**：`${BACKEND_PORT}:5000`（默认 5000）
- **环境变量**：数据库、Redis、MinIO、RabbitMQ 等连接配置

### 6. 前端管理端

- **构建上下文**：../sidifensen_blog_frontend/sidifensen_admin
- **端口**：`${ADMIN_PORT}:80`（默认 8000）

### 7. 前端用户端

- **构建上下文**：../sidifensen_blog_frontend/sidifensen_user
- **端口**：`${USER_PORT}:80`（默认 7000）

## 🚀 部署步骤

### 1. 本地开发环境部署

#### 方式一：完整 Docker 环境（推荐用于测试）

```bash
# 1. 克隆项目
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog

# 2. 配置环境变量
cp script/dev/.env.example .env
# 编辑 .env 文件，修改必要的配置

# 3. 启动完整服务
cd script
./start.sh  # Linux/Mac
# 或
start.bat   # Windows

# 选择 "1. 启动生产环境 (完整服务)"
```

#### 方式二：开发环境（推荐用于开发）

```bash
# 1. 克隆项目
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog

# 2. 配置环境变量
cp script/dev/.env.example .env
# 编辑 .env 文件，修改必要的配置

# 3. 启动基础服务
cd script
./start.sh  # Linux/Mac
# 或
start.bat   # Windows

# 选择 "2. 启动开发环境 (仅基础服务)"

# 4. 在 IDE 中启动后端应用
# 5. 在终端中启动前端应用
cd sidifensen_blog_frontend/sidifensen_admin
npm run dev

cd sidifensen_blog_frontend/sidifensen_user
npm run dev
```

### 2. 生产服务器部署

#### 方式一：使用预构建文件部署

```bash
# 1. 在本地构建项目
# 后端构建
cd sidifensen_blog_backend
mvn clean package -DskipTests

# 前端构建
cd sidifensen_blog_frontend/sidifensen_admin
npm run build

cd sidifensen_blog_frontend/sidifensen_user
npm run build

# 2. 将构建文件传输到服务器
# 传输后端 target 文件夹
scp -r sidifensen_blog_backend/target user@server:/path/to/sidifensen_blog/sidifensen_blog_backend/

# 传输前端 dist 文件夹
scp -r sidifensen_blog_frontend/sidifensen_admin/dist user@server:/path/to/sidifensen_blog/sidifensen_blog_frontend/sidifensen_admin/
scp -r sidifensen_blog_frontend/sidifensen_user/dist user@server:/path/to/sidifensen_blog/sidifensen_blog_frontend/sidifensen_user/

# 3. 在服务器上启动服务
ssh user@server
cd /path/to/sidifensen_blog/script
./start.sh
# 选择 "1. 启动生产环境 (完整服务)"
```

#### 方式二：服务器直接构建部署

```bash
# 1. 在服务器上克隆项目
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog

# 2. 配置环境变量
cp script/dev/.env.example .env
# 编辑 .env 文件，修改生产环境配置

# 3. 启动服务（会自动构建）
cd script
./start.sh
# 选择 "1. 启动生产环境 (完整服务)"
```

#### 方式三：分步部署（推荐用于生产环境）

```bash
# 1. 在服务器上克隆项目
git clone https://github.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog

# 2. 配置环境变量
cp script/dev/.env.example .env
# 编辑 .env 文件，修改生产环境配置

# 3. 先启动基础服务
cd script
docker-compose -f dev/docker-compose-service.yml up -d

# 4. 等待基础服务启动完成（约 2-3 分钟）
docker-compose -f dev/docker-compose-service.yml ps

# 5. 启动应用服务
docker-compose -f dev/docker-compose-apps.yml up -d --build

# 6. 检查所有服务状态
docker-compose -f dev/docker-compose.yml ps
```

### 3. 部署验证

部署完成后，可以通过以下方式验证服务是否正常运行：

```bash
# 检查服务状态
docker-compose ps

# 检查服务日志
docker-compose logs -f

# 测试服务访问
curl http://localhost:5000/actuator/health  # 后端健康检查
curl http://localhost:8000                  # 管理端前端
curl http://localhost:7000                  # 用户端前端
```

### 4. 部署注意事项

1. **环境变量配置**：生产环境务必修改 `.env` 文件中的默认密码和密钥
2. **端口配置**：确保服务器防火墙开放相应端口
3. **资源要求**：建议服务器至少 2GB 内存，2 核 CPU
4. **数据备份**：定期备份数据库和 MinIO 数据
5. **SSL 证书**：生产环境建议配置 HTTPS

## ⚙️ 环境配置

项目使用 `.env` 文件来管理环境变量，这样可以方便地在不同环境（开发、测试、生产）之间切换配置。

### 1. 创建 .env 文件

复制 `env.example` 文件并重命名为 `.env`：

```bash
# Linux/macOS
cp script/dev/.env.example .env

# Windows
copy script/dev/.env.example .env
```

### 2. 配置环境变量

编辑 `.env` 文件，根据您的实际需求修改以下配置：

- **Spring Boot 配置**：修改激活的配置文件（dev/docker/prod）
- **数据库配置**：修改 MySQL 的 root 密码、数据库名、用户名和密码
- **Redis 配置**：修改 Redis 的用户名和访问密码
- **MinIO 配置**：修改 MinIO 的访问密钥和密钥，以及公网访问地址
- **RabbitMQ 配置**：修改 RabbitMQ 的默认用户名和密码
- **邮件配置**：填入真实的邮箱地址和密码或授权码
- **OAuth 配置**：填入 Gitee 和 GitHub 的客户端 ID 和密钥
- **前端地址配置**：根据实际部署情况修改前端地址
- **阿里云配置**：填入真实的阿里云 AccessKey ID 和密钥
- **应用自定义配置**：修改 JWT 密钥、自动审核开关等自定义配置
- **端口配置**：根据需要修改各服务的端口

> ⚠️ **安全提醒**：`.env` 文件包含敏感信息，不应提交到 Git 仓库。生产环境请务必修改所有默认密码。

### 3. 环境变量说明

`.env` 文件中的主要配置项说明：

| 变量名                        | 说明                       | 默认值                    |
| ----------------------------- | -------------------------- | ------------------------- |
| SPRING_PROFILES_ACTIVE        | Spring Boot 激活的配置文件 | docker                    |
| MYSQL_ROOT_PASSWORD           | MySQL root 用户密码        | root                      |
| MYSQL_DATABASE                | 应用使用的数据库名称       | sidifensen_blog           |
| MYSQL_USER                    | 应用连接数据库的用户名     | sidifensen                |
| MYSQL_PASSWORD                | 应用连接数据库的密码       | sidifensen123             |
| REDIS_USERNAME                | Redis 用户名               | sidifensen                |
| REDIS_PASSWORD                | Redis 访问密码             | sidifensen123             |
| MINIO_ROOT_USER               | MinIO 访问密钥             | minioadmin                |
| MINIO_ROOT_PASSWORD           | MinIO 密钥                 | minioadmin123             |
| MINIO_PUBLIC_POINT            | MinIO 公网访问地址         | http://localhost:9000     |
| RABBITMQ_DEFAULT_USER         | RabbitMQ 管理员用户名      | admin                     |
| RABBITMQ_DEFAULT_PASS         | RabbitMQ 管理员密码        | admin123                  |
| MAIL_USERNAME                 | 发送邮件的邮箱地址         | your-email@qq.com         |
| MAIL_PASSWORD                 | 邮箱密码或授权码           | your-email-password       |
| GITEE_CLIENT_ID               | Gitee OAuth 客户端 ID      | your-gitee-client-id      |
| GITEE_CLIENT_SECRET           | Gitee OAuth 客户端密钥     | your-gitee-client-secret  |
| GITHUB_CLIENT_ID              | GitHub OAuth 客户端 ID     | your-github-client-id     |
| GITHUB_CLIENT_SECRET          | GitHub OAuth 客户端密钥    | your-github-client-secret |
| SIDIFENSEN_JWT_SECRET         | JWT 令牌签名密钥           | sidifensen                |
| SIDIFENSEN_PHOTO_AUTO_AUDIT   | 图片自动审核开关           | false                     |
| SIDIFENSEN_ARTICLE_AUTO_AUDIT | 文章自动审核开关           | true                      |

> 📝 更多配置项说明请参考 `env.example` 文件中的详细注释。

## 📝 常用命令

### 基础操作

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启所有服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service_name]

# 进入容器
docker-compose exec [service_name] /bin/bash
```

### 构建和更新

```bash
# 重新构建并启动
docker-compose up -d --build

# 只构建不启动
docker-compose build

# 强制重新构建
docker-compose build --no-cache
```

### 数据管理

```bash
# 查看数据卷
docker volume ls

# 删除数据卷（会删除所有数据）
docker-compose down -v

# 备份数据库
docker-compose exec mysql mysqldump -u root -p sidifensen_blog > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -u root -p sidifensen_blog < backup.sql
```

### 开发环境（仅基础服务）

```bash
# 启动四个基础服务
docker-compose -f dev/docker-compose-service.yml up -d

# 查看基础服务状态
docker-compose -f dev/docker-compose-service.yml ps

# 查看基础服务日志
docker-compose -f dev/docker-compose-service.yml logs -f

# 停止基础服务
docker-compose -f dev/docker-compose-service.yml down
```

> 💡 **开发环境说明**：
>
> - 只启动 MySQL、Redis、MinIO、RabbitMQ 四个基础服务
> - 后端应用需要在 IDE 中单独启动（如 IntelliJ IDEA、VS Code）
> - 前端应用需要在终端中单独启动（如 `npm run dev`）
> - 适合本地开发调试，可以直接在 IDE 中打断点调试

### 应用服务部署

```bash
# 启动应用服务（需要基础服务已运行）
docker-compose -f dev/docker-compose-apps.yml up -d --build

# 停止应用服务
docker-compose -f dev/docker-compose-apps.yml down

# 重启应用服务
docker-compose -f dev/docker-compose-apps.yml restart

# 查看应用服务日志
docker-compose -f dev/docker-compose-apps.yml logs -f

# 单独重启某个应用服务
docker-compose -f dev/docker-compose-apps.yml restart backend
docker-compose -f dev/docker-compose-apps.yml restart frontend-admin
docker-compose -f dev/docker-compose-apps.yml restart frontend-user
```

### 组合使用示例

```bash
# 场景1：本地开发环境（推荐）
# 1. 启动基础服务
docker-compose -f dev/docker-compose-service.yml up -d
# 2. 在 IDE 中启动后端应用（Spring Boot）
# 3. 在终端中启动前端应用（npm run dev）

# 场景2：完整 Docker 开发环境
# 1. 启动基础服务
docker-compose -f dev/docker-compose-service.yml up -d
# 2. 启动应用服务
docker-compose -f dev/docker-compose-apps.yml up -d --build

# 场景3：只更新应用服务
# 停止应用服务
docker-compose -f dev/docker-compose-apps.yml down
# 重新构建并启动
docker-compose -f dev/docker-compose-apps.yml up -d --build

# 场景4：生产环境一键部署
docker-compose up -d --build
```

## 🔧 故障排除

### 常见问题

1. **端口冲突**

   ```bash
   # 检查端口占用
   netstat -tulpn | grep :3306

   # 修改 docker-compose.yml 中的端口映射
   ports:
     - "3307:3306"  # 改为其他端口
   ```

2. **服务启动失败**

   ```bash
   # 查看详细日志
   docker-compose logs [service_name]

   # 检查服务状态
   docker-compose ps
   ```

3. **数据库连接失败**

   ```bash
   # 检查数据库是否启动
   docker-compose exec mysql mysql -u root -p

   # 检查网络连接
   docker-compose exec backend ping mysql
   ```

4. **前端构建失败**

   ```bash
   # 检查 Node.js 版本
   docker-compose exec frontend-admin node --version

   # 重新构建前端
   docker-compose build --no-cache frontend-admin
   ```

5. **MinIO 控制台登录网络错误**

   如果在 MinIO 控制台登录时遇到 "unable to login due to network error" 错误：

   **问题原因：**

   - 容器内部无法正确解析 localhost 域名
   - MinIO 控制台与 API 服务之间的网络连接问题

   **解决方案：**

   ```bash
   # 在 MinIO 容器内添加 hosts 记录
   docker exec -it sidifensen-minio /bin/bash -c "echo '127.0.0.1 localhost' >> /etc/hosts"

   # 验证修改是否成功
   docker exec -it sidifensen-minio cat /etc/hosts
   ```

   修复完成后即可正常登录 MinIO 控制台。

6. **环境变量未生效**

   如果发现修改了 `.env` 文件中的配置但未生效：

   ```bash
   # 确保 .env 文件位于 script 目录的上级目录（项目根目录）
   # 检查环境变量名称是否与 docker-compose.yml 中的定义一致

   # 重启服务使配置生效
   docker-compose down && docker-compose up -d

   # 验证环境变量是否正确传递给容器
   docker-compose exec backend env | grep MYSQL
   ```

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看最近100行日志
docker-compose logs --tail=100 backend
```

## 📚 更多资源

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 官方文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 指南](https://spring.io/guides/gs/spring-boot-docker/)
- [Vue.js Docker 部署](https://vuejs.org/guide/scaling-up/deployment.html)

## 🤝 贡献

如果您在使用过程中遇到问题或有改进建议，欢迎提交 Issue 或 Pull Request。
