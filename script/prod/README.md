# Jenkins 自动化部署指南

本文档介绍 Sidifensen Blog 项目在 Linux 服务器上的 Jenkins 自动化部署流程。

## 📋 目录

- [快速开始](#-快速开始)
- [架构说明](#-架构说明)
- [环境准备](#-环境准备)
- [部署流程](#-部署流程)
- [SSL 证书配置](#-ssl-证书配置)
- [故障排查](#-故障排查)

---

## 🚀 快速开始

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- JDK 21
- Node.js 20+
- Maven 3.9+

### 一键部署

```bash
# 1. 克隆项目
git clone https://gitee.com/sidifensen/sidifensen_blog.git
cd sidifensen_blog/script/prod

# 2. 配置环境变量
cp .env.example .env
vim .env  # 编辑配置

# 3. 申请 SSL 证书（首次部署）
cd ssl
sudo bash get_ssl_cert.sh

# 4. 启动服务
cd ..
docker-compose -f docker-compose-ssl.yml up -d
```

---

## 🏗️ 架构说明

### 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | Vue 3 + Vite + Element Plus |
| **后端** | Spring Boot 3 + Spring Security 6 |
| **数据库** | MySQL 8.0 + Redis 7 |
| **消息队列** | RabbitMQ 3 |
| **对象存储** | MinIO |
| **AI 服务** | DeepSeek API |

### 服务架构

```
                    ┌─────────────────┐
                    │   Nginx (SSL)   │
                    │  443 / 80 端口   │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
   ┌─────┴─────┐     ┌──────┴──────┐    ┌───────┴───────┐
   │ 前端用户端  │     │  前端管理端   │    │   后端 API    │
   │  :7000    │     │   :8000     │    │   :5000      │
   └───────────┘     └─────────────┘    └───────┬───────┘
                                                 │
                    ┌────────────────────────────┼────────────────────────────┐
                    │                            │                            │
              ┌─────┴─────┐            ┌─────────┴─────────┐        ┌─────────┴─────────┐
              │   MySQL   │            │      Redis        │        │      MinIO        │
              │   :3306   │            │      :6379        │        │      :9000        │
              └───────────┘            └───────────────────┘        └───────────────────┘
```

### 域名规划

| 域名 | 用途 | 端口 |
|------|------|------|
| `sidifensen.com` | 用户端首页 | 443 |
| `www.sidifensen.com` | 用户端首页 | 443 |
| `admin.sidifensen.com` | 管理后台 | 443 |
| `api.sidifensen.com` | API 接口 | 443 |
| `image.sidifensen.com` | 图片存储 (MinIO) | 443 |

---

## 📦 环境准备

### 1. 配置环境变量

```bash
cd /opt/sidifensen_blog/script/prod
cp .env.example .env
vim .env
```

### 2. 必需配置项

```bash
# ===== 数据库配置 =====
MYSQL_ROOT_PASSWORD=<强密码>
MYSQL_DATABASE=sidifensen_blog
MYSQL_USER=sidifensen
MYSQL_PASSWORD=<强密码>

# ===== Redis 配置 =====
REDIS_USERNAME=sidifensen
REDIS_PASSWORD=<强密码>

# ===== MinIO 配置 =====
MINIO_ACCESS_KEY=<访问密钥>
MINIO_SECRET_KEY=<秘密密钥>
MINIO_PUBLIC_POINT=https://image.sidifensen.com

# ===== RabbitMQ 配置 =====
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=<强密码>

# ===== JWT 密钥（生产环境必须修改）=====
SIDIFENSEN_JWT_SECRET=<随机长字符串>

# ===== 邮件配置 =====
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=<邮箱授权码>

# ===== OAuth 配置 =====
GITEE_CLIENT_ID=<Gitee Client ID>
GITEE_CLIENT_SECRET=<Gitee Client Secret>
GITEE_REDIRECT_URI=https://api.sidifensen.com/oauth/gitee/callback

# ===== AI 服务配置 =====
DEEPSEEK_API_KEY=<DeepSeek API Key>
```

### 3. 设置文件权限

```bash
chmod 600 .env
chown root:root .env
```

---

## 🔄 部署流程

### Jenkins 自动部署

Jenkinsfile 配置的自动化部署流程：

```
┌─────────────────┐
│  1. 代码检出     │
│  (Git Checkout) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  2. 后端构建     │
│  (Maven Build)  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  3. 前端构建     │
│  (Npm Build)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  4. 打包产物     │
│  (Package)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  5. 部署到服务器  │
│  (Docker Deploy)│
└─────────────────┘
```

### 手动部署步骤

```bash
# 1. 构建后端
cd sidifensen_blog_backend
mvn clean package -DskipTests

# 2. 构建前端
cd sidifensen_blog_frontend/sidifensen_admin
npm ci && npm run build

cd sidifensen_blog_frontend/sidifensen_user
npm ci && npm run build

# 3. 启动服务
cd /opt/sidifensen_blog/script/prod
docker-compose -f docker-compose-ssl.yml up -d --build

# 4. 查看日志
docker-compose logs -f
```

### 服务管理命令

```bash
# 启动所有服务
docker-compose -f docker-compose-ssl.yml up -d

# 停止所有服务
docker-compose -f docker-compose-ssl.yml down

# 重启服务
docker-compose -f docker-compose-ssl.yml restart

# 查看服务状态
docker-compose -f docker-compose-ssl.yml ps

# 查看特定服务日志
docker-compose -f docker-compose-ssl.yml logs -f backend
docker-compose -f docker-compose-ssl.yml logs -f frontend-user
docker-compose -f docker-compose-ssl.yml logs -f frontend-admin
docker-compose -f docker-compose-ssl.yml logs -f nginx-gateway
```

---

## 🔒 SSL 证书配置

### 申请证书

```bash
cd /opt/sidifensen_blog/script/prod/ssl
sudo bash get_ssl_cert.sh
```

按照交互式菜单提示：
1. 选择 `[1] 首次设置 SSL 证书`
2. 输入邮箱地址
3. 选择要申请的域名
4. 确认配置并获取证书

### 证书管理

```bash
# 查看证书状态
sudo bash get_ssl_cert.sh
# 选择 [2] SSL 证书管理
# 选择 [1] 检查证书状态

# 手动续期证书
sudo bash get_ssl_cert.sh
# 选择 [2] SSL 证书管理
# 选择 [3] 手动续期证书

# 设置自动续期
sudo bash get_ssl_cert.sh
# 选择 [3] 设置自动续期
```

### 证书文件位置

```
/etc/letsencrypt/live/sidifensen.com/
├── cert.pem      # 证书文件
├── fullchain.pem # 完整证书链
├── privkey.pem   # 私钥文件
└── chain.pem     # 证书链
```

---

## 🐛 故障排查

### 常见问题

#### Q1: 容器启动失败，提示端口被占用

```bash
# 检查端口占用
netstat -tlnp | grep <端口号>

# 修改 .env 中的端口配置
MYSQL_PORT=3307  # 改为其他可用端口
```

#### Q2: SSL 证书申请失败

```bash
# 检查域名解析
nslookup sidifensen.com

# 检查 80 端口是否开放
netstat -tlnp | grep :80

# 检查防火墙
firewall-cmd --list-ports
firewall-cmd --add-port=80/tcp --permanent
firewall-cmd --reload
```

#### Q3: 数据库连接失败

```bash
# 检查 MySQL 容器状态
docker-compose ps | grep mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 测试数据库连接
docker-compose exec mysql mysql -u sidifensen -p
```

#### Q4: 后端服务无法启动

```bash
# 查看后端日志
docker-compose logs backend

# 检查 JVM 内存
docker stats

# 检查配置文件
docker-compose exec backend cat /app/application.yml
```

#### Q5: 前端页面空白

```bash
# 检查前端容器状态
docker-compose ps | grep frontend

# 查看前端日志
docker-compose logs frontend-user
docker-compose logs frontend-admin

# 检查 API 连接
curl https://api.sidifensen.com/actuator/health
```

### 日志文件位置

```bash
# 应用日志
docker-compose logs -f backend > /var/log/sidifensen/backend.log

# Nginx 访问日志
docker-compose exec nginx-gateway tail -f /var/log/nginx/access.log

# 错误日志
docker-compose exec nginx-gateway tail -f /var/log/nginx/error.log
```

---

## 📚 相关文档

| 文档 | 路径 |
|------|------|
| Jenkins 详细配置 | `jenkins/Jenkins 部署指南.md` |
| Gitea 配置指南 | `jenkins/Gitea 配置指南.md` |
| SSL 证书管理 | `ssl/get_ssl_cert.sh` |
| 环境变量示例 | `.env.example` |

---

## 🔗 相关链接

- [项目主页](https://www.sidifensen.com)
- [Gitee 仓库](https://gitee.com/sidifensen/sidifensen_blog)
- [GitHub 仓库](https://github.com/sidifensen/sidifensen_blog)

---

**最后更新**: 2026-03-15
