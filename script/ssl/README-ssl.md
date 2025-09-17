# SSL 证书管理系统（交互式版本）

## 概述

这是一个基于 `ssl.sh` 脚本开发的交互式 SSL 证书管理系统，提供了友好的菜单界面和用户体验，让 SSL 证书的管理变得更加简单直观。。

## 主要特性

### 🎯 交互式界面

- 美观的菜单界面和颜色输出
- 直观的操作流程和用户提示
- 实时状态显示和进度反馈

### 🔧 功能完整

- **首次设置**: 为域名申请 Let's Encrypt 免费 SSL 证书
- **证书管理**: 检查状态、查看详情、手动续期
- **自动续期**: 设置定时任务，支持多种频率选择
- **系统信息**: 查看服务器和 Docker 环境信息

### 🛡️ 安全可靠

- 权限检查和依赖验证
- DNS 解析检查和端口占用检测
- 详细的错误处理和日志记录

## 使用方法

### 1. Linux/macOS 环境

```bash
# 进入SSL脚本目录
cd script/ssl

# 直接运行SSL管理脚本
sudo bash ssl.sh
```

### 2. Windows 环境

```cmd
# 进入SSL脚本目录
cd script\ssl

# 在命令行中运行（推荐使用Git Bash或WSL）
bash ssl.sh
```

### 3. Docker 服务管理

```bash
# 启动SSL生产环境
sudo bash start.sh  # 选择 1

# 停止服务
sudo bash start.sh  # 选择 2

# Windows环境
start.bat
```

## 功能说明

### 主菜单选项

1. **首次设置 SSL 证书**

   - 交互式域名选择
   - 邮箱地址配置
   - DNS 解析检查（可选跳过）
   - 自动配置 nginx 和 certbot
   - 实时进度显示

2. **SSL 证书管理**

   - 检查证书状态和有效期
   - 查看证书详细信息
   - 手动续期证书
   - 重启 Nginx 服务
   - 查看续期日志

3. **设置自动续期**

   - 多种续期频率选择：
     - 每周日凌晨 2 点（推荐）
     - 每月 1 号凌晨 2 点
     - 每天凌晨 3 点
     - 自定义 cron 表达式
   - 自动生成续期脚本
   - 集成日志记录

4. **系统信息**

   - 服务器基本信息
   - Docker 环境状态
   - 端口占用情况
   - 证书文件列表

5. **帮助信息**
   - 详细的使用指南
   - 故障排除方法
   - 注意事项说明

### 交互式特性

#### 🎨 美观的界面

- 彩色输出和特殊字符
- 清晰的分隔线和标题
- 状态图标（✓ ✗ ⚠）

#### 💬 友好的提示

- 详细的操作说明
- 确认对话框
- 错误信息和解决建议

#### 🔍 智能检查

- 自动检测现有证书
- DNS 解析验证
- 端口占用检查
- 依赖环境验证

## 配置说明

### 默认配置

```bash
DOMAINS=("sidifensen.com" "www.sidifensen.com" "admin.sidifensen.com" "image.sidifensen.com" "minio.sidifensen.com")
EMAIL="1848221808@qq.com"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-ssl.yml"
```

### 域名配置说明

系统支持以下域名的 SSL 证书管理：

- `sidifensen.com` - 主站域名
- `www.sidifensen.com` - WWW 主站
- `admin.sidifensen.com` - 管理后台
- `image.sidifensen.com` - MinIO 文件存储 API（图片、文件访问）
- `minio.sidifensen.com` - MinIO 管理控制台

**重要**: 所有域名使用同一个 SSL 证书，存储在 `/etc/letsencrypt/live/sidifensen.com/` 目录下。

### Docker Compose 配置

SSL 环境使用 `docker-compose-ssl.yml` 文件，包含以下服务：

- **nginx-gateway**: Nginx 反向代理，支持 SSL 终止和域名路由
- **backend**: Spring Boot 后端服务
- **frontend-admin**: 管理端前端
- **frontend-user**: 用户端前端
- **mysql**: 数据库服务
- **redis**: 缓存服务
- **minio**: 对象存储服务（支持 HTTPS 访问）
- **rabbitmq**: 消息队列服务

### Nginx 域名路由配置

系统通过 Nginx 反向代理实现多域名路由：

```nginx
# 主站和用户端
sidifensen.com, www.sidifensen.com → frontend-user:80

# 管理后台
admin.sidifensen.com → frontend-admin:80

# MinIO 文件存储 API
image.sidifensen.com → minio:9000

# MinIO 管理控制台
minio.sidifensen.com → minio:9001

# API 接口代理
/api/ → backend:5000
```

### 自定义配置

在脚本运行过程中，您可以：

- 修改邮箱地址
- 选择特定域名
- 添加自定义域名
- 设置续期频率

## 前端配置说明

### HTTPS 混合内容问题解决

在 HTTPS 环境中，前端应用必须通过 HTTPS 访问所有资源，包括 API 请求。我们通过以下方式解决：

#### 1. Nginx 反向代理配置

在 `nginx-ssl.conf` 中配置 API 代理：

```nginx
# API代理配置 - 后端服务
location /api/ {
    proxy_pass http://backend:5000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    # ... 其他配置
}
```

#### 2. MinIO HTTPS 配置

MinIO 对象存储服务通过专用域名提供 HTTPS 访问：

```nginx
# MinIO API (文件访问)
server {
    listen 443 ssl http2;
    server_name image.sidifensen.com;

    location / {
        proxy_pass http://minio:9000;
        # 支持大文件上传
        client_max_body_size 1000m;
        proxy_request_buffering off;
        # ... 其他配置
    }
}

# MinIO 控制台
server {
    listen 443 ssl http2;
    server_name minio.sidifensen.com;

    location / {
        proxy_pass http://minio:9001;
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        # ... 其他配置
    }
}
```

#### 3. 前端配置

在 `vite.config.js` 中配置环境变量：

```javascript
define: {
  "import.meta.env.VITE_BACKEND_SERVER": JSON.stringify(
    process.env.NODE_ENV === "production" ? "/api" : "http://localhost:5000"
  ),
}
```

#### 4. 后端 MinIO 配置

在 `application.yaml` 中配置 MinIO 地址：

```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000} # 内部连接地址
  publicPoint: ${MINIO_PUBLIC_POINT:http://localhost:9000} # 公网访问地址
```

Docker 环境变量：

```bash
MINIO_ENDPOINT=http://minio:9000                    # 后端内部访问
MINIO_PUBLIC_POINT=https://image.sidifensen.com    # 用户文件访问
MINIO_CONSOLE_PUBLIC_POINT=https://minio.sidifensen.com  # 管理控制台
```

#### 5. 请求流程

- **开发环境**: 前端 → `http://localhost:5000` (直接访问后端)
- **生产环境**: 前端 → `https://domain.com/api` → Nginx → `http://backend:5000` (通过代理)
- **文件访问**: 用户 → `https://image.sidifensen.com/file.jpg` → Nginx → `http://minio:9000`
- **MinIO 管理**: 管理员 → `https://minio.sidifensen.com` → Nginx → `http://minio:9001`

这样确保所有请求都通过 HTTPS，避免混合内容警告。

## 系统要求

### 必需依赖

- **Docker**: 用于运行 certbot 和 nginx 容器
- **Docker Compose**: 用于管理服务编排
- **OpenSSL**: 用于证书信息查看
- **curl**: 用于网络连接测试

### 权限要求

- **Root 权限**: 证书申请和系统配置需要管理员权限
- **网络访问**: 需要访问 Let's Encrypt 服务器
- **端口开放**: 80 端口用于证书验证，443 端口用于 HTTPS 服务

## 日志和监控

### 自动续期日志

- **位置**: `/var/log/ssl-renewal.log`
- **内容**: 续期执行时间、结果状态、错误信息
- **查看**: 在管理菜单中选择"查看续期日志"

### 实时状态监控

- 证书有效期检查
- 服务运行状态
- 端口占用情况
- DNS 解析状态

## 故障排除

### 常见问题

1. **域名解析失败**

   ```bash
   # 检查DNS解析
   nslookup sidifensen.com
   nslookup image.sidifensen.com
   nslookup minio.sidifensen.com
   dig sidifensen.com

   # 检查服务器IP
   curl ifconfig.me
   ```

2. **端口被占用**

   ```bash
   # 检查端口占用
   netstat -tlnp | grep :80
   netstat -tlnp | grep :443

   # 停止占用服务
   sudo systemctl stop nginx
   docker stop container_name
   ```

3. **证书申请失败**

   - 确保域名解析正确
   - 检查防火墙设置
   - 验证 Let's Encrypt 服务状态
   - 检查申请频率限制

4. **权限问题**

   ```bash
   # 使用root权限运行
   sudo bash ssl.sh

   # 检查文件权限
   ls -la /etc/letsencrypt/
   ```

5. **混合内容问题**

   在 HTTPS 环境中，前端必须通过 HTTPS 访问后端 API：

   ```bash
   # 检查 Nginx 代理配置
   grep -A 10 "location /api/" nginx-ssl.conf

   # 检查前端配置
   grep "VITE_BACKEND_SERVER" ../sidifensen_user/vite.config.js
   ```

6. **MinIO HTTPS 访问问题**

   ```bash
   # 检查 MinIO 域名配置
   grep -A 5 "image.sidifensen.com" nginx-ssl.conf
   grep -A 5 "minio.sidifensen.com" nginx-ssl.conf

   # 验证证书包含所有域名
   openssl x509 -in /etc/letsencrypt/live/sidifensen.com/fullchain.pem -text -noout | grep DNS

   # 测试 MinIO 访问
   curl -I https://image.sidifensen.com
   curl -I https://minio.sidifensen.com
   ```

7. **证书域名不匹配问题**

   如果出现 `NET::ERR_CERT_COMMON_NAME_INVALID` 错误：

   ```bash
   # 检查证书是否包含所有域名
   openssl x509 -in /etc/letsencrypt/live/sidifensen.com/fullchain.pem -text -noout | grep -A 1 "Subject Alternative Name"

   # 重新申请包含所有域名的证书
   sudo ./ssl.sh
   # 选择 [1] 首次设置SSL证书
   # 选择 [a] 全部域名
   ```

### 调试模式

如果遇到问题，可以：

1. 选择"系统信息"查看环境状态
2. 查看续期日志了解详细错误
3. 使用"--skip-dns-check"跳过 DNS 检查
4. 手动执行 certbot 命令进行调试

## 更新日志

### v2.2.0 (当前版本)

- 🌐 新增 MinIO HTTPS 支持
- 📁 配置 `image.sidifensen.com` 作为文件存储 API 域名
- ⚙️ 配置 `minio.sidifensen.com` 作为管理控制台域名
- 🔒 统一证书管理，所有域名使用同一个 SSL 证书
- 📝 完善 MinIO 配置文档和故障排除指南
- 🔧 优化证书申请脚本，支持多域名单证书模式

### v2.1.0

- 🔧 整合为单一脚本 `ssl.sh`
- 🚀 新增 Docker 服务管理脚本 (`start.sh` / `start.bat`)
- 🌐 完善的 Nginx 反向代理配置
- 🔒 解决 HTTPS 混合内容问题
- 📝 更新文档和使用说明
- 🐛 修复路径解析和构建上下文问题

### v2.0.0 (交互式版本)

- ✨ 全新的交互式界面设计
- 🎯 用户友好的菜单系统
- 🔧 增强的错误处理和提示
- 📊 实时状态显示和进度反馈
- 🎨 彩色输出和视觉改进
- 🔍 智能检查和验证功能
- 📝 详细的帮助信息和故障排除

### 基于原版功能

- SSL 证书申请和续期
- 自动续期任务设置
- Docker 集成支持
- 多域名证书管理

## 许可证

本项目基于原有的 SSL 管理脚本开发，保持相同的开源许可证。

---

## 快速开始

### 1. DNS 配置

确保以下域名都解析到你的服务器 IP：

```
sidifensen.com          → 服务器IP
www.sidifensen.com      → 服务器IP
admin.sidifensen.com    → 服务器IP
image.sidifensen.com    → 服务器IP
minio.sidifensen.com    → 服务器IP
```

### 2. 申请 SSL 证书

```bash
cd script/ssl
sudo ./ssl.sh
# 选择 [1] 首次设置SSL证书
# 选择 [a] 全部域名
```

### 3. 启动服务

```bash
# 启动完整的 HTTPS 服务
sudo ./start.sh
# 选择 [1] 启动SSL生产环境
```

### 4. 访问测试

- 主站：https://sidifensen.com
- 管理后台：https://admin.sidifensen.com
- MinIO 文件：https://image.sidifensen.com
- MinIO 控制台：https://minio.sidifensen.com

---

**注意**: 使用前请确保已正确配置域名解析，并且服务器防火墙允许 80 和 443 端口访问。
