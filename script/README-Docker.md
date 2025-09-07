# 🐳 Sidifensen Blog Docker 部署指南

## 📋 目录

- [Docker Compose 简介](#docker-compose-简介)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [服务说明](#服务说明)
- [环境配置](#环境配置)
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
│   ├── docker-compose.dev.yml      # 开发环境配置
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
# 复制环境变量示例文件
cp script/env.example .env

# 编辑环境变量
vim .env
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

**开发环境**

```bash
# 进入 script 目录
cd script

# 启动基础服务（数据库、Redis、MinIO、RabbitMQ）
docker-compose -f docker-compose.dev.yml up -d

# 或者使用开发环境配置（仅启动基础服务）
docker-compose -f docker-compose.dev.yml up -d
```

#### 方式三：服务器部署

**一键部署到生产服务器：**

```bash
# 在服务器上运行
cd script/deploy
sudo ./deploy.sh
```

**Git Hook 自动部署：**

```bash
# 设置 Git Hook（在服务器上）
cp script/deploy/post-receive-hook.sh /path/to/repo.git/hooks/post-receive
chmod +x /path/to/repo.git/hooks/post-receive

# 之后每次推送代码到 main 分支时会自动部署
git push origin main
```

详细的服务器部署说明请参考：[deploy/README.md](deploy/README.md)

### 5. 访问服务

- **后端 API**: http://localhost:5000
- **管理端前端**: http://localhost:8000
- **用户端前端**: http://localhost:7000
- **MinIO 控制台**: http://localhost:9001
- **RabbitMQ 管理界面**: http://localhost:15672

## 🛠 服务说明

### 核心服务

| 服务           | 端口       | 描述              | 管理界面               |
| -------------- | ---------- | ----------------- | ---------------------- |
| mysql          | 3306       | MySQL 数据库      | -                      |
| redis          | 6379       | Redis 缓存        | -                      |
| minio          | 9000/9001  | MinIO 对象存储    | http://localhost:9001  |
| rabbitmq       | 5672/15672 | RabbitMQ 消息队列 | http://localhost:15672 |
| backend        | 5000       | Spring Boot 后端  | -                      |
| frontend-admin | 8000       | Vue 管理端        | http://localhost:8000  |
| frontend-user  | 7000       | Vue 用户端        | http://localhost:7000  |

### 默认账号

- **MinIO**: admin / minioadmin123
- **RabbitMQ**: admin / admin123
- **MySQL**: root / root123456

## ⚙️ 环境配置

### 环境变量说明

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=sidifensen_blog
MYSQL_USER=sidifensen
MYSQL_PASSWORD=sidifensen123
MYSQL_PORT=3306                    # 自定义 MySQL 端口

# Redis 配置
REDIS_PASSWORD=redis123
REDIS_PORT=6379                    # 自定义 Redis 端口

# MinIO 配置
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin123
MINIO_API_PORT=9000                # 自定义 MinIO API 端口
MINIO_CONSOLE_PORT=9001            # 自定义 MinIO 控制台端口

# RabbitMQ 配置
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin123
RABBITMQ_PORT=5672                 # 自定义 RabbitMQ 服务端口
RABBITMQ_MANAGEMENT_PORT=15672     # 自定义 RabbitMQ 管理端口

# 邮件配置
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=your-email-password

# 阿里云配置
ALIYUN_ACCESS_KEY_ID=your-access-key-id
ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret
```

### 端口自定义说明

所有服务端口都支持通过环境变量自定义：

| 服务          | 环境变量                   | 默认端口 | 说明                 |
| ------------- | -------------------------- | -------- | -------------------- |
| MySQL         | `MYSQL_PORT`               | 3306     | 数据库服务端口       |
| Redis         | `REDIS_PORT`               | 6379     | 缓存服务端口         |
| MinIO API     | `MINIO_API_PORT`           | 9000     | 对象存储 API 端口    |
| MinIO 控制台  | `MINIO_CONSOLE_PORT`       | 9001     | 对象存储管理界面端口 |
| RabbitMQ      | `RABBITMQ_PORT`            | 5672     | 消息队列服务端口     |
| RabbitMQ 管理 | `RABBITMQ_MANAGEMENT_PORT` | 15672    | 消息队列管理界面端口 |

**自定义端口示例：**

```bash
# 在 .env 文件中修改端口
MYSQL_PORT=3307
REDIS_PORT=6380
MINIO_API_PORT=9002
MINIO_CONSOLE_PORT=9003
RABBITMQ_PORT=5673
RABBITMQ_MANAGEMENT_PORT=15673
```

### 配置文件

- `application-docker.yaml`: Docker 环境下的 Spring Boot 配置
- `docker-compose.yml`: 生产环境配置
- `docker-compose.dev.yml`: 开发环境配置

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

### 开发环境

```bash
# 启动开发环境
docker-compose -f docker-compose.dev.yml up -d

# 只启动基础服务
docker-compose -f docker-compose.dev.yml up -d mysql redis minio rabbitmq

# 查看开发环境日志
docker-compose -f docker-compose.dev.yml logs -f
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

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看最近100行日志
docker-compose logs --tail=100 backend
```

### 性能优化

1. **资源限制**

   ```yaml
   services:
     backend:
       deploy:
         resources:
           limits:
             memory: 1G
             cpus: "0.5"
   ```

2. **健康检查**
   ```yaml
   services:
     backend:
       healthcheck:
         test: ["CMD", "curl", "-f", "http://localhost:5000/actuator/health"]
         interval: 30s
         timeout: 10s
         retries: 3
   ```

## 📚 更多资源

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 官方文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 指南](https://spring.io/guides/gs/spring-boot-docker/)
- [Vue.js Docker 部署](https://vuejs.org/guide/scaling-up/deployment.html)

## 🤝 贡献

如果您在使用过程中遇到问题或有改进建议，欢迎提交 Issue 或 Pull Request。
