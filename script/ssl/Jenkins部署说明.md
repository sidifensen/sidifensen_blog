# Jenkins SSL 部署配置说明

## 📋 概述

本文档说明如何在 Jenkins 自动化部署中使用 SSL 配置（`docker-compose-ssl.yml`、`nginx-ssl.conf`、`.env`）。

## 🔧 文件说明

### 1. 配置文件位置

所有 SSL 相关配置文件都在 `script/ssl/` 目录下：

```
script/ssl/
├── docker-compose-ssl.yml    # SSL 环境的 Docker Compose 配置
├── nginx-ssl.conf            # Nginx SSL 反向代理配置
├── env.example               # 环境变量配置示例文件
├── .env                      # 实际使用的环境变量文件（需要手动创建）
└── README-ssl.md            # SSL 配置详细文档
```

### 2. 文件作用

- **`docker-compose-ssl.yml`**: 定义所有服务的容器配置，包括 Nginx、后端、前端、MySQL、Redis、MinIO、RabbitMQ 等
- **`nginx-ssl.conf`**: Nginx 反向代理配置，处理 HTTPS 请求、SSL 证书、域名路由等
- **`env.example`**: 环境变量配置模板，包含所有可配置项
- **`.env`**: 实际部署时使用的环境变量文件（**包含敏感信息，不应提交到 Git**）

## 📝 配置步骤

### 步骤 1: 创建 .env 文件

在服务器上的 `script/ssl/` 目录下创建 `.env` 文件：

```bash
# 在服务器上执行
cd /opt/sidifensen_blog/script/ssl

# 复制示例文件
cp env.example .env

# 编辑配置文件（使用 vim 或其他编辑器）
vim .env
```

### 步骤 2: 配置环境变量

根据实际情况修改 `.env` 文件中的配置项：

#### 必需配置项

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your_strong_password
MYSQL_DATABASE=sidifensen_blog
MYSQL_USER=sidifensen
MYSQL_PASSWORD=your_strong_password

# Redis 配置
REDIS_USERNAME=sidifensen
REDIS_PASSWORD=your_strong_password

# MinIO 配置
MINIO_ACCESS_KEY=your_access_key
MINIO_SECRET_KEY=your_secret_key
MINIO_PUBLIC_POINT=https://image.sidifensen.com

# RabbitMQ 配置
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=your_strong_password

# JWT 密钥（生产环境必须修改）
SIDIFENSEN_JWT_SECRET=your_very_long_random_string

# 邮件配置（如果需要邮件功能）
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=your_email_password_or_auth_code

# OAuth 配置（如果需要第三方登录）
GITEE_CLIENT_ID=your_gitee_client_id
GITEE_CLIENT_SECRET=your_gitee_client_secret
GITEE_REDIRECT_URI=https://api.sidifensen.com/oauth/gitee/callback

GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GITHUB_REDIRECT_URI=https://api.sidifensen.com/oauth/github/callback

# 阿里云配置（如果需要内容安全检测）
ALIYUN_ACCESS_KEY_ID=your_access_key_id
ALIYUN_ACCESS_KEY_SECRET=your_access_key_secret

# AI 服务配置（如果需要 AI 功能）
DEEPSEEK_API_KEY=your_deepseek_api_key
```

#### 可选配置项

```bash
# 端口配置（如果默认端口被占用）
MYSQL_PORT=3306
REDIS_PORT=6379
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672
BACKEND_PORT=5000
ADMIN_PORT=8000
USER_PORT=7000

# 自动审核开关
SIDIFENSEN_PHOTO_AUTO_AUDIT=false
SIDIFENSEN_ARTICLE_AUTO_AUDIT=false
SIDIFENSEN_COMMENT_AUTO_AUDIT=false
```

### 步骤 3: 确保文件权限

```bash
# 设置 .env 文件权限（仅所有者可读写）
chmod 600 .env

# 确保文件所有者正确
chown root:root .env  # 或使用你的部署用户
```

## 🚀 Jenkins 部署流程

### 当前配置

Jenkinsfile 已经配置为使用 SSL 配置，**Jenkins 直接在服务器上执行部署**（无需 SSH 连接）：

1. **部署路径**: 代码会被部署到 `${DEPLOY_PATH}`（默认 `/opt/sidifensen_blog`）
2. **Docker Compose**: 使用 `script/ssl/docker-compose-ssl.yml`
3. **环境变量**: 从 `script/ssl/.env` 文件读取
4. **项目根目录**: 自动设置 `PROJECT_ROOT=${DEPLOY_PATH}`

### 部署步骤

Jenkins 部署时会自动执行以下操作：

```bash
# 1. 构建后端（生成 jar 包）
cd sidifensen_blog_backend
mvn clean package -DskipTests

# 2. 构建前端（生成 dist 目录）
cd sidifensen_blog_frontend/sidifensen_admin
npm ci && npm run build

cd sidifensen_blog_frontend/sidifensen_user
npm ci && npm run build

# 3. 复制构建产物到部署目录
# 复制后端 jar 包
cp sidifensen_blog_backend/target/*.jar ${DEPLOY_PATH}/sidifensen_blog_backend/target/

# 复制前端 dist 目录
cp -r sidifensen_blog_frontend/sidifensen_admin/dist ${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_admin/
cp -r sidifensen_blog_frontend/sidifensen_user/dist ${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_user/

# 4. 复制部署脚本和配置文件（如果存在）
cp -r script/ssl/* ${DEPLOY_PATH}/script/ssl/

# 5. 进入 SSL 配置目录
cd ${DEPLOY_PATH}/script/ssl

# 6. 检查 .env 文件（如果不存在会从 env.example 复制）
# 7. 设置 PROJECT_ROOT 环境变量
export PROJECT_ROOT=${DEPLOY_PATH}

# 8. 停止旧容器
docker-compose -f docker-compose-ssl.yml --env-file .env down

# 9. 启动新容器
docker-compose -f docker-compose-ssl.yml --env-file .env up -d --build
```

### 部署特点

- ✅ **本地部署**: Jenkins 直接在服务器上运行，无需 SSH 连接
- ✅ **直接复制**: 构建产物直接复制到部署目录，无需打包上传
- ✅ **自动化**: 所有步骤自动执行，包括文件复制和容器重启

## ⚠️ 重要注意事项

### 1. .env 文件位置

**`.env` 文件必须放在服务器上的 `script/ssl/` 目录下**，与 `docker-compose-ssl.yml` 同级：

```
/opt/sidifensen_blog/
├── script/
│   └── ssl/
│       ├── docker-compose-ssl.yml  ✅
│       ├── nginx-ssl.conf          ✅
│       ├── env.example             ✅
│       └── .env                    ✅ 必须存在
```

### 2. .env 文件安全

- **❌ 不要将 `.env` 文件提交到 Git 仓库**
- **✅ 在服务器上手动创建和编辑**
- **✅ 设置适当的文件权限（600）**
- **✅ 定期备份 `.env` 文件**

### 3. 首次部署

如果是首次部署，需要：

1. **创建 `.env` 文件**（从 `env.example` 复制）
2. **配置所有必需的环境变量**
3. **确保 SSL 证书已申请**（参考 `README-ssl.md`）
4. **确保域名解析正确**

### 4. SSL 证书

SSL 证书需要单独申请和管理：

```bash
# 在服务器上执行
cd /opt/sidifensen_blog/script/ssl
sudo bash ssl.sh

# 选择 [1] 首次设置SSL证书
# 按照提示完成证书申请
```

详细说明请参考 `README-ssl.md`。

## 🔍 验证配置

### 检查 .env 文件

```bash
# 检查文件是否存在
ls -la /opt/sidifensen_blog/script/ssl/.env

# 检查文件权限
stat /opt/sidifensen_blog/script/ssl/.env
```

### 检查环境变量

```bash
# 在服务器上测试 docker-compose 配置
cd /opt/sidifensen_blog/script/ssl
export PROJECT_ROOT=/opt/sidifensen_blog
docker-compose -f docker-compose-ssl.yml --env-file .env config
```

### 检查服务状态

```bash
# 查看容器状态
cd /opt/sidifensen_blog/script/ssl
docker-compose -f docker-compose-ssl.yml --env-file .env ps

# 查看服务日志
docker-compose -f docker-compose-ssl.yml --env-file .env logs -f
```

## 📚 相关文档

- **SSL 配置详细文档**: `script/ssl/README-ssl.md`
- **Jenkins 部署指南**: `script/deploy/Jenkins部署指南.md`
- **环境变量示例**: `script/ssl/env.example`

## 🆘 常见问题

### Q1: Jenkins 部署时提示找不到 .env 文件

**A**: 确保在服务器上的 `script/ssl/` 目录下创建了 `.env` 文件：

```bash
cd /opt/sidifensen_blog/script/ssl
cp env.example .env
vim .env  # 编辑配置
```

### Q2: 容器启动失败，提示环境变量未设置

**A**: 检查 `.env` 文件中是否配置了所有必需的环境变量，特别是：

- `MYSQL_ROOT_PASSWORD`
- `MYSQL_PASSWORD`
- `REDIS_PASSWORD`
- `MINIO_ACCESS_KEY`
- `MINIO_SECRET_KEY`
- `SIDIFENSEN_JWT_SECRET`

### Q3: 如何更新环境变量

**A**: 修改 `.env` 文件后，重启相关服务：

```bash
cd /opt/sidifensen_blog/script/ssl
docker-compose -f docker-compose-ssl.yml --env-file .env restart
```

### Q4: PROJECT_ROOT 环境变量是什么

**A**: `PROJECT_ROOT` 是项目根目录的路径，Jenkins 部署时会自动设置为 `${DEPLOY_PATH}`（默认 `/opt/sidifensen_blog`）。这个变量用于：

- 构建 Docker 镜像时的上下文路径
- 挂载 SQL 初始化脚本

### Q5: Jenkins 需要配置 SSH 密钥吗

**A**: 不需要。由于 Jenkins 直接在服务器上运行，所有操作都在本地执行，无需配置 SSH 密钥或服务器连接信息。只需要确保：

- Jenkins 用户有权限访问部署目录（可能需要 sudo 权限）
- Jenkins 用户有权限执行 docker-compose 命令

### Q6: 如何切换回非 SSL 配置

**A**: 修改 Jenkinsfile 中的部署脚本，将：

```bash
cd ${DEPLOY_PATH}/script/ssl
docker-compose -f docker-compose-ssl.yml --env-file .env ...
```

改为：

```bash
cd ${DEPLOY_PATH}/script
docker-compose -f docker-compose.yml ...
```

### Q7: Jenkins 用户权限问题

**A**: 如果 Jenkins 用户没有足够权限，可以：

1. **配置 sudo 权限**（推荐）：

   ```bash
   # 编辑 sudoers 文件
   sudo visudo

   # 添加以下内容（允许 Jenkins 用户无密码执行 docker 命令）
   jenkins ALL=(ALL) NOPASSWD: /usr/bin/docker-compose, /usr/bin/docker
   ```

2. **将 Jenkins 用户添加到 docker 组**：

   ```bash
   sudo usermod -aG docker jenkins
   # 需要重启 Jenkins 服务才能生效
   ```

3. **修改部署目录权限**：
   ```bash
   sudo chown -R jenkins:jenkins /opt/sidifensen_blog
   ```

---

**最后更新**: 2024 年
