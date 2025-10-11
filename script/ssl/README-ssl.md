# SSL 证书管理系统（交互式版本）

## 概述

这是一个基于 `ssl.sh` 脚本开发的交互式 SSL 证书管理系统，为 Sidifensen Blog 项目提供完整的 HTTPS 支持。该系统集成了 Let's Encrypt 免费 SSL 证书申请、自动续期、Docker 服务管理等功能，通过友好的菜单界面让 SSL 证书管理变得简单直观。

## 🆕 最新更新 (v2.4.0)

### 新增功能

- **🔧 增强的 Docker 服务管理**: 新增 `start.sh` 脚本，支持分模块启动服务
- **📦 模块化部署**: 支持基础服务、前后端服务、完整环境的独立启动
- **🌐 新增 API 专用域名**: `api.sidifensen.com` 提供独立的 API 访问入口
- **🔒 增强的安全配置**: 优化 SSL 配置，支持 TLS 1.3 和更强的加密套件
- **📊 实时服务监控**: 新增服务状态检查、日志查看、健康检查功能
- **🛠️ 开发友好**: 支持开发环境快速启动和调试

### 改进内容

- **优化 Nginx 配置**: 改进反向代理配置，支持 WebSocket 和大文件上传
- **增强 MinIO 支持**: 完善对象存储的 HTTPS 访问配置
- **改进错误处理**: 更详细的错误信息和故障排除指南
- **性能优化**: 优化容器启动顺序和依赖关系

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

# 或者使用Windows批处理脚本启动服务
start.bat
```

### 3. Docker 服务管理

#### 3.1 SSL 证书管理

```bash
# Linux/macOS 运行SSL管理脚本
sudo bash ssl.sh

# Windows 环境
bash ssl.sh
```

#### 3.2 服务启动管理

```bash
# Linux/macOS 启动服务
sudo bash start.sh

# Windows 环境
start.bat
```

#### 3.3 模块化服务启动

```bash
# 启动完整SSL生产环境
sudo bash start.sh  # 选择 1

# 启动基础服务 (MySQL, Redis, MinIO, RabbitMQ)
sudo bash start.sh  # 选择 2

# 启动前后端服务
sudo bash start.sh  # 选择 3

# 启动后端服务
sudo bash start.sh  # 选择 4

# 启动前端服务
sudo bash start.sh  # 选择 5
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
DOMAINS=("sidifensen.com" "www.sidifensen.com" "admin.sidifensen.com" "image.sidifensen.com" "minio.sidifensen.com" "api.sidifensen.com")
EMAIL="1848221808@qq.com"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-ssl.yml"
```

### 域名配置说明

系统支持以下域名的 SSL 证书管理：

- `sidifensen.com` - 主站域名（用户端前端）
- `www.sidifensen.com` - WWW 主站（用户端前端）
- `admin.sidifensen.com` - 管理后台（管理端前端）
- `api.sidifensen.com` - API 接口服务（独立 API 访问入口）
- `image.sidifensen.com` - MinIO 文件存储 API（图片、文件访问）
- `minio.sidifensen.com` - MinIO 管理控制台

**重要**: 所有域名使用同一个 SSL 证书，存储在 `/etc/letsencrypt/live/sidifensen.com/` 目录下。

### 新增 API 专用域名

`api.sidifensen.com` 提供独立的 API 访问入口，支持：

- 直接 API 调用，无需通过前端代理
- 独立的 CORS 配置
- 支持大文件上传（最大 10MB）
- 完整的 WebSocket 支持

### Docker Compose 配置

SSL 环境使用 `docker-compose-ssl.yml` 文件，包含以下服务：

#### 核心服务

- **nginx-gateway**: Nginx 反向代理，支持 SSL 终止和域名路由
- **backend**: Spring Boot 后端服务
- **frontend-admin**: 管理端前端
- **frontend-user**: 用户端前端

#### 基础服务

- **mysql**: MySQL 8.0 数据库服务
- **redis**: Redis 7 缓存服务
- **minio**: MinIO 对象存储服务（支持 HTTPS 访问）
- **rabbitmq**: RabbitMQ 消息队列服务

#### 新增模块化配置

系统现在支持分模块启动，包含以下配置文件：

- `docker-compose-ssl.yml` - 完整 SSL 生产环境
- `docker-compose-services.yml` - 基础服务（MySQL, Redis, MinIO, RabbitMQ）
- `docker-compose-apps.yml` - 前后端应用服务
- `docker-compose-backend.yml` - 后端服务
- `docker-compose-frontend.yml` - 前端服务

### Nginx 域名路由配置

系统通过 Nginx 反向代理实现多域名路由：

```nginx
# 主站和用户端
sidifensen.com, www.sidifensen.com → sidifensen-user:80

# 管理后台
admin.sidifensen.com → sidifensen-admin:80

# API 专用域名
api.sidifensen.com → sidifensen-backend:5000

# MinIO 文件存储 API
image.sidifensen.com → minio:9000

# MinIO 管理控制台
minio.sidifensen.com → minio:9001

# API 接口代理（在所有域名下）
/api/ → backend:5000
```

### 新增路由特性

- **独立 API 域名**: `api.sidifensen.com` 提供直接的 API 访问
- **增强的 CORS 支持**: 所有 API 端点都支持跨域请求
- **WebSocket 支持**: 完整的 WebSocket 代理配置
- **大文件上传**: 支持最大 10MB 的文件上传
- **静态文件缓存**: 优化静态资源加载性能

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
```

#### 5. 请求流程

- **开发环境**: 前端 → `http://localhost:5000` (直接访问后端)
- **生产环境**: 前端 → `https://domain.com/api` → Nginx → `http://backend:5000` (通过代理)
- **API 专用域名**: 直接 → `https://api.sidifensen.com` → Nginx → `http://backend:5000`
- **文件访问**: 用户 → `https://image.sidifensen.com/file.jpg` → Nginx → `http://minio:9000`
- **MinIO 管理**: 管理员 → `https://minio.sidifensen.com` → Nginx → `http://minio:9001`

这样确保所有请求都通过 HTTPS，避免混合内容警告。

#### 6. 新增服务管理功能

**start.sh 脚本功能**:

- 模块化服务启动（基础服务、前后端、完整环境）
- 服务状态监控和健康检查
- 实时日志查看
- 服务重启和停止
- 数据清理功能（危险操作）

**开发环境支持**:

- 支持单独启动后端或前端服务
- 快速调试和开发
- 环境变量自动配置

## 新增服务管理功能

### start.sh 脚本功能详解

`start.sh` 脚本提供了完整的 Docker 服务管理功能，支持以下操作：

#### 1. 服务启动选项

- **完整环境**: 启动所有服务（SSL 生产环境）
- **基础服务**: 仅启动 MySQL, Redis, MinIO, RabbitMQ
- **前后端服务**: 启动后端和前端应用
- **后端服务**: 仅启动 Spring Boot 后端
- **前端服务**: 仅启动前端应用

#### 2. 服务管理功能

- **服务状态查看**: 实时检查所有服务运行状态
- **日志查看**: 支持查看单个服务或所有服务的日志
- **服务重启**: 支持重启单个服务或所有服务
- **服务停止**: 支持停止服务并清理资源
- **数据清理**: 危险操作，清理所有数据卷

#### 3. 开发环境优化

- **快速启动**: 支持仅启动需要的服务
- **独立调试**: 可以单独启动后端或前端进行调试
- **环境隔离**: 不同服务使用独立的网络和数据卷

### 使用示例

```bash
# 进入SSL脚本目录
cd script/ssl

# 运行服务管理脚本
sudo ./start.sh

# 选择操作：
# 1. 启动生产环境 (SSL 全量服务)
# 2. 启动基础服务 (MySQL, Redis, MinIO, RabbitMQ)
# 3. 启动前后端服务
# 4. 启动后端服务
# 5. 启动前端服务
# 6. 停止服务
# 7. 查看服务状态
# 8. 查看服务日志
# 9. 重启服务
# 10. 清理数据 (危险操作)
```

### 服务监控

系统提供实时监控功能：

- **健康检查**: 自动检查服务健康状态
- **端口监控**: 检查服务端口占用情况
- **日志监控**: 实时查看服务日志
- **资源监控**: 监控容器资源使用情况

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

### 支持的操作系统

- **Linux**: Ubuntu 18.04+, CentOS 7+, Debian 9+
- **Windows**: Windows 10/11 (需要 WSL 2 或 Git Bash)
- **macOS**: macOS 10.14+ (需要安装 Docker Desktop)

### 网络要求

- **公网 IP**: 服务器需要有公网 IP 地址
- **域名解析**: 所有域名必须正确解析到服务器 IP
- **防火墙**: 开放 80 (HTTP) 和 443 (HTTPS) 端口
- **带宽**: 建议至少 10Mbps 上下行带宽

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

### 日志查看命令

```bash
# 查看续期日志
tail -f /var/log/ssl-renewal.log

# 查看 Docker 服务日志
docker-compose -f docker-compose-ssl.yml logs nginx-gateway
docker-compose -f docker-compose-ssl.yml logs backend

# 查看系统日志中的 SSL 相关信息
journalctl -u docker -f | grep ssl
```

### 监控脚本

系统提供了内置的监控功能，可以通过以下方式查看：

```bash
# 运行SSL管理脚本
sudo ./ssl.sh

# 选择 [2] SSL证书管理
# 选择 [1] 检查证书状态
```

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

## 最佳实践

### 🔒 安全建议

1. **定期检查证书状态**

   ```bash
   # 每月检查一次证书有效期
   sudo ./ssl.sh
   # 选择 [2] SSL证书管理 → [1] 检查证书状态
   ```

2. **备份证书文件**

   ```bash
   # 备份 Let's Encrypt 证书目录
   sudo tar -czf ssl-backup-$(date +%Y%m%d).tar.gz /etc/letsencrypt/
   ```

3. **监控证书过期时间**

   - 设置证书过期提醒（30 天、7 天、1 天）
   - 使用自动续期功能，推荐每周检查一次

4. **安全头配置**
   - 系统已配置 HSTS、X-Frame-Options 等安全头
   - 定期检查 SSL 配置安全性：[SSL Labs Test](https://www.ssllabs.com/ssltest/)

### ⚡ 性能优化

1. **证书缓存优化**

   ```nginx
   # nginx-ssl.conf 中已配置
   ssl_session_cache shared:SSL:10m;
   ssl_session_timeout 10m;
   ```

2. **HTTP/2 支持**

   - 系统默认启用 HTTP/2
   - 提升网站加载速度

3. **OCSP Stapling**
   ```nginx
   # 可在 nginx-ssl.conf 中添加
   ssl_stapling on;
   ssl_stapling_verify on;
   ```

### 🔧 维护建议

1. **定期更新系统**

   ```bash
   # 更新 Docker 镜像
   docker-compose -f docker-compose-ssl.yml pull
   docker-compose -f docker-compose-ssl.yml up -d
   ```

2. **日志清理**

   ```bash
   # 清理过期日志（保留最近30天）
   find /var/log -name "ssl-renewal.log*" -mtime +30 -delete
   ```

3. **容器健康检查**

   ```bash
   # 检查所有服务状态
   docker-compose -f docker-compose-ssl.yml ps

   # 检查服务健康状况
   docker-compose -f docker-compose-ssl.yml logs --tail=50
   ```

### 📋 运维清单

#### 每日检查

- [ ] 服务运行状态正常
- [ ] 网站 HTTPS 访问正常
- [ ] 错误日志无异常

#### 每周检查

- [ ] 证书有效期检查（剩余天数 > 30）
- [ ] 自动续期任务运行正常
- [ ] 备份证书文件

#### 每月检查

- [ ] 更新 Docker 镜像
- [ ] 检查系统安全更新
- [ ] 清理过期日志文件
- [ ] SSL 配置安全性测试

### 🚨 应急处理

1. **证书过期紧急处理**

   ```bash
   # 立即续期证书
   sudo ./ssl.sh
   # 选择 [2] SSL证书管理 → [3] 手动续期证书

   # 重启 Nginx 服务
   docker-compose -f docker-compose-ssl.yml restart nginx-gateway
   ```

2. **服务异常恢复**

   ```bash
   # 快速重启所有服务
   sudo ./start.sh
   # 选择 [5] 重启服务
   ```

3. **回滚操作**
   ```bash
   # 如果新证书有问题，可以回滚到备份
   sudo tar -xzf ssl-backup-YYYYMMDD.tar.gz -C /
   docker-compose -f docker-compose-ssl.yml restart nginx-gateway
   ```

## 更新日志

### v2.4.0 (当前版本)

- 🆕 **新增 start.sh 服务管理脚本**: 支持模块化服务启动和管理
- 🆕 **模块化部署支持**: 基础服务、前后端、完整环境独立启动
- 🆕 **API 专用域名**: `api.sidifensen.com` 提供独立 API 访问入口
- 🆕 **增强的服务监控**: 实时状态检查、日志查看、健康检查
- 🆕 **开发环境优化**: 支持单独启动后端或前端服务
- 🔧 **优化 Nginx 配置**: 改进反向代理，支持 WebSocket 和大文件上传
- 🔒 **增强安全配置**: 支持 TLS 1.3，优化加密套件
- 📊 **新增服务管理功能**: 服务重启、停止、数据清理
- 🐛 **修复容器依赖**: 优化启动顺序和健康检查
- ⚡ **性能优化**: 改进容器启动时间和资源使用

### v2.3.0

- 🌐 新增 `api.sidifensen.com` API 备用域名支持
- 🔧 优化交互式界面，增强用户体验
- 📝 完善文档说明，新增快速开始指南
- 🔒 加强证书安全配置，支持 TLS 1.3
- 🐛 修复容器名称映射问题
- ⚡ 优化脚本性能，减少执行时间
- 📋 更新环境变量配置说明

### v2.2.0

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

### 1. 前置准备

#### 环境要求

- **操作系统**: Linux (推荐 Ubuntu/CentOS) 或 Windows (WSL/Git Bash)
- **权限**: Root 权限（Linux）或管理员权限（Windows）
- **依赖软件**:
  - Docker 20.10+
  - Docker Compose 1.29+
  - OpenSSL
  - curl

#### 检查依赖

```bash
# 检查 Docker 版本
docker --version
docker-compose --version

# 检查网络工具
curl --version
openssl version
```

### 2. DNS 配置

确保以下域名都解析到你的服务器 IP：

```
sidifensen.com          → 服务器IP
www.sidifensen.com      → 服务器IP
admin.sidifensen.com    → 服务器IP
api.sidifensen.com      → 服务器IP
image.sidifensen.com    → 服务器IP
minio.sidifensen.com    → 服务器IP
```

**验证 DNS 解析**:

```bash
# 检查域名解析
nslookup sidifensen.com
dig sidifensen.com

# 检查服务器公网IP
curl ifconfig.me
```

### 3. 环境配置

```bash
# 进入SSL脚本目录
cd script/ssl

# 复制环境变量文件
cp env.example .env

# 根据实际情况编辑配置
vim .env  # 或使用其他编辑器
```

### 4. 申请 SSL 证书

```bash
# 运行SSL管理脚本
sudo ./ssl.sh

# 按照交互式菜单操作：
# 选择 [1] 首次设置SSL证书
# 输入或确认邮箱地址
# 选择 [a] 全部域名
# 等待证书申请完成
```

### 5. 启动服务

#### 5.1 完整环境启动

```bash
# 启动完整的 HTTPS 服务
sudo ./start.sh
# 选择 [1] 启动SSL生产环境

# Windows 用户可以使用
start.bat
```

#### 5.2 模块化启动（推荐开发环境）

```bash
# 启动基础服务
sudo ./start.sh
# 选择 [2] 启动基础服务

# 启动前后端服务
sudo ./start.sh
# 选择 [3] 启动前后端服务

# 单独启动后端（开发调试）
sudo ./start.sh
# 选择 [4] 启动后端服务

# 单独启动前端（开发调试）
sudo ./start.sh
# 选择 [5] 启动前端服务
```

### 6. 访问测试

- **主站**: https://sidifensen.com
- **管理后台**: https://admin.sidifensen.com
- **API 服务**: https://api.sidifensen.com
- **MinIO 文件存储**: https://image.sidifensen.com
- **MinIO 控制台**: https://minio.sidifensen.com

### 7. 设置自动续期

```bash
# 再次运行SSL管理脚本
sudo ./ssl.sh

# 选择 [3] 设置自动续期
# 推荐选择 [1] 每周日凌晨2点
```

---

**⚠️ 重要提示**:

- 使用前请确保已正确配置域名解析
- 服务器防火墙必须开放 80 和 443 端口
- 首次申请证书需要域名能正常访问到服务器
- 建议在非生产时间进行证书操作
