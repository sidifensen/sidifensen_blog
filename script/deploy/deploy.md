# 🚀 Sidifensen Blog 自动化部署脚本

本目录包含用于服务器部署的脚本文件。

## 📁 文件说明

### 🔧 部署脚本

| 文件                   | 用途               | 使用场景             |
| ---------------------- | ------------------ | -------------------- |
| `deploy.sh`            | 服务器自动部署脚本 | 一键部署到生产服务器 |
| `post-receive-hook.sh` | Git Hook 自动部署  | 代码推送自动触发部署 |

## 🛠️ 使用方法

### 1. GitHub Actions 自动部署（推荐）

**功能特点：**

- ✅ 代码推送自动触发部署
- ✅ 可视化部署日志和状态
- ✅ 安全的密钥管理
- ✅ 部署成功/失败通知
- ✅ 支持团队协作
- ✅ 零停机时间部署

**配置步骤：**

#### 步骤 1：生成 SSH 密钥对

在本地计算机上运行：

```bash
# 生成 SSH 密钥对
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"

# 查看公钥内容（复制此内容到服务器）
cat ~/.ssh/id_rsa.pub

# 查看私钥内容（复制此内容到 GitHub Secrets）
cat ~/.ssh/id_rsa
```

#### 步骤 2：在服务器上配置 SSH 公钥

```bash
# 登录服务器，添加公钥到 authorized_keys
mkdir -p ~/.ssh
echo "你的公钥内容" >> ~/.ssh/authorized_keys

# 设置正确的权限
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh

# 测试 SSH 连接
ssh -i ~/.ssh/id_rsa root@your-server-ip
```

#### 步骤 3：在服务器上初始部署

首次使用需要在服务器上运行一次完整部署：

```bash
# 下载并运行部署脚本
wget https://raw.githubusercontent.com/sidifensen/sidifensen_blog/main/script/deploy/deploy.sh
chmod +x deploy.sh
sudo ./deploy.sh
```

#### 步骤 4：配置 GitHub Secrets

1. 进入你的 GitHub 仓库
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret** 添加以下密钥：

| 密钥名称   | 值             | 说明                    |
| ---------- | -------------- | ----------------------- |
| `HOST`     | `你的服务器IP` | 服务器公网 IP 地址      |
| `USERNAME` | `root`         | 服务器登录用户名        |
| `SSH_KEY`  | `私钥内容`     | 完整的 SSH 私钥内容     |
| `PORT`     | `22`           | SSH 连接端口（默认 22） |

**SSH_KEY 格式示例：**

```
-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAA...
（完整的私钥内容）
-----END OPENSSH PRIVATE KEY-----
```

#### 步骤 5：验证自动部署

1. **提交代码触发部署**：

   ```bash
   # 修改任意文件
   echo "# 测试自动部署" >> README.md

   # 提交并推送
   git add .
   git commit -m "测试GitHub Actions自动部署"
   git push origin main
   ```

2. **查看部署状态**：

   - 进入 GitHub 仓库
   - 点击 **Actions** 标签页
   - 查看 "Deploy to Server" 工作流执行情况

3. **检查部署结果**：

   ```bash
   # 在服务器上检查服务状态
   cd /opt/sidifensen_blog/script
   docker-compose ps

   # 查看服务日志
   docker-compose logs -f
   ```

#### 🚀 完整的自动部署流程

**阶段 1：准备阶段（一次性配置）**

```mermaid
graph TD
    A["开发者本地电脑"] --> B["生成SSH密钥对<br/>ssh-keygen -t rsa -b 4096"]
    B --> C["私钥: ~/.ssh/id_rsa<br/>公钥: ~/.ssh/id_rsa.pub"]

    C --> D["复制公钥到服务器<br/>~/.ssh/authorized_keys"]
    C --> E["复制私钥到GitHub Secrets<br/>SSH_KEY"]

    F["服务器"] --> G["初始部署<br/>运行 deploy.sh"]
    G --> H["安装Docker环境<br/>克隆代码<br/>启动服务"]

    I["GitHub仓库"] --> J["配置Secrets<br/>HOST, USERNAME, SSH_KEY, PORT"]
    J --> K["创建工作流文件<br/>.github/workflows/deploy.yml"]

    style A fill:#e1f5fe
    style F fill:#fff3e0
    style I fill:#f3e5f5
```

**阶段 2：自动部署流程（每次推送代码）**

```mermaid
graph TD
    A["开发者修改代码"] --> B["git push origin main"]
    B --> C["GitHub检测到推送"]
    C --> D["触发GitHub Actions"]

    D --> E["GitHub Actions Runner<br/>（虚拟机）"]
    E --> F["读取GitHub Secrets<br/>获取SSH连接信息"]

    F --> G["使用SSH私钥连接服务器<br/>ssh -i SSH_KEY user@HOST"]
    G --> H["服务器验证公钥<br/>检查authorized_keys"]
    H --> I["✅ SSH连接建立"]

    I --> J["在服务器执行部署命令"]
    J --> K["git pull origin main<br/>拉取最新代码"]
    K --> L["docker-compose down<br/>停止旧服务"]
    L --> M["docker-compose up -d --build<br/>构建并启动新服务"]
    M --> N["健康检查<br/>curl http://localhost:5000/health"]
    N --> O["✅ 部署完成"]

    style A fill:#e8f5e8
    style O fill:#c8e6c9
    style G fill:#fff3e0
    style I fill:#e1f5fe
```

**SSH 密钥认证原理**

```mermaid
graph TB
    A["1. 生成密钥对<br/>ssh-keygen"] --> B["私钥 (id_rsa)<br/>🔑 保密，用于身份证明"]
    A --> C["公钥 (id_rsa.pub)<br/>🔓 公开，用于验证身份"]

    B --> D["存储在客户端<br/>~/.ssh/id_rsa"]
    C --> E["存储在服务器<br/>~/.ssh/authorized_keys"]

    F["SSH 连接请求"] --> G["服务器用公钥验证"]
    G --> H["客户端用私钥证明身份"]
    H --> I["✅ 连接成功"]

    style B fill:#ffcdd2
    style C fill:#c8e6c9
    style I fill:#4caf50
```

#### 部署流程步骤说明

每次推送代码到 `main` 分支时，GitHub Actions 会自动：

1. 🔍 **检测代码推送** - GitHub 监听 main 分支变化
2. 🔐 **使用 SSH 连接服务器** - 通过密钥认证安全连接
3. 📥 **拉取最新代码** - git pull 获取最新版本
4. 🛑 **停止旧服务** - docker-compose down 优雅停止
5. 🔨 **重新构建镜像** - --build 参数重新构建
6. 🚀 **启动新服务** - docker-compose up -d 后台启动
7. ✅ **执行健康检查** - 验证服务是否正常运行
8. 📊 **显示部署结果** - 输出部署状态和访问信息

### 2. 服务器一键部署

```bash
# 在服务器上运行
sudo ./deploy.sh
```

**功能特点：**

- ✅ 自动安装 Docker 环境
- ✅ 代码拉取和更新
- ✅ 自动备份旧版本
- ✅ 环境配置检查
- ✅ 服务健康检查
- ✅ 部署信息展示

**部署流程：**

1. 检查 root 权限
2. 安装系统依赖 (Docker, Docker Compose)
3. 备份现有版本
4. 拉取最新代码
5. 配置环境变量
6. 构建并启动服务
7. 健康检查
8. 显示访问信息

### 3. Git Hook 自动部署

```bash
# 在服务器的 Git 仓库中设置
cp post-receive-hook.sh /path/to/repo.git/hooks/post-receive
chmod +x /path/to/repo.git/hooks/post-receive
```

**功能特点：**

- ✅ 代码推送自动触发
- ✅ 分支过滤 (仅 main 分支)
- ✅ 自动重新构建
- ✅ 零停机时间部署

## ⚙️ 配置说明

### deploy.sh 配置项

```bash
PROJECT_DIR="/opt/sidifensen_blog"    # 项目部署目录
GIT_REPO="https://github.com/your-username/sidifensen_blog.git"  # Git 仓库地址
BRANCH="main"                         # 部署分支
BACKUP_DIR="/opt/backups"            # 备份目录
```

### post-receive-hook.sh 配置项

```bash
PROJECT_DIR="/opt/sidifensen_blog"    # 项目部署目录
DEPLOY_BRANCH="main"                  # 部署分支
```

## 🔒 安全注意事项

1. **权限要求**: `deploy.sh` 需要 root 权限运行
2. **备份策略**: 自动保留最近 5 个版本的备份
3. **环境变量**: 生产环境必须修改 `.env` 中的敏感信息
4. **网络安全**: 确保服务器防火墙配置正确

## 🚨 故障排除

### GitHub Actions 部署问题

1. **SSH 连接失败**

   ```bash
   # 检查 SSH 密钥格式是否正确
   ssh -i ~/.ssh/id_rsa -T root@your-server-ip

   # 确保服务器允许 SSH 密钥登录
   sudo vim /etc/ssh/sshd_config
   # 确保以下配置启用：
   # PubkeyAuthentication yes
   # AuthorizedKeysFile .ssh/authorized_keys

   # 重启 SSH 服务
   sudo systemctl restart sshd
   ```

2. **GitHub Secrets 配置错误**

   - 检查 `HOST` 是否为服务器公网 IP
   - 确保 `SSH_KEY` 包含完整的私钥内容（包括头尾标识）
   - 验证 `USERNAME` 和 `PORT` 设置正确

3. **部署权限问题**

   ```bash
   # 确保部署用户有 Docker 权限
   sudo usermod -aG docker $USER

   # 重新登录或重启服务器使权限生效
   sudo reboot
   ```

4. **服务启动失败**

   ```bash
   # 检查服务器资源
   free -h  # 内存使用情况
   df -h    # 磁盘使用情况

   # 查看具体错误日志
   cd /opt/sidifensen_blog/script
   docker-compose logs --tail=50
   ```

5. **GitHub Actions 执行失败**

   - 进入 GitHub 仓库 → Actions 标签页
   - 点击失败的工作流查看详细日志
   - 检查是否为网络超时或服务器资源不足

### 传统部署问题

1. **Docker 安装失败**

   ```bash
   # 手动安装 Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   ```

2. **权限不足**

   ```bash
   # 确保使用 root 权限
   sudo ./deploy.sh
   ```

3. **端口冲突**

   ```bash
   # 检查端口占用
   sudo netstat -tulpn | grep :5000
   ```

4. **服务启动失败**
   ```bash
   # 查看详细日志
   cd /opt/sidifensen_blog/script
   docker-compose logs -f
   ```

## 📊 部署监控

### 服务访问地址

部署完成后可通过以下地址访问服务：

| 服务              | 地址                        | 说明                 |
| ----------------- | --------------------------- | -------------------- |
| **后端 API**      | http://your-server-ip:5000  | Spring Boot 后端服务 |
| **管理端前端**    | http://your-server-ip:8000  | 管理员界面           |
| **用户端前端**    | http://your-server-ip:7000  | 用户访问界面         |
| **MinIO 控制台**  | http://your-server-ip:9001  | 文件存储管理         |
| **RabbitMQ 管理** | http://your-server-ip:15672 | 消息队列管理         |

### GitHub Actions 部署状态监控

1. **实时查看部署状态**：

   - 进入 GitHub 仓库 → **Actions** 标签页
   - 查看最新的 "Deploy to Server" 工作流

2. **部署历史记录**：

   - 可查看所有历史部署记录
   - 每次部署的详细日志
   - 部署成功/失败统计

3. **部署通知设置**：
   ```bash
   # GitHub 会自动发送邮件通知：
   # ✅ 部署成功通知
   # ❌ 部署失败通知
   # 📊 部署状态变化通知
   ```

### 服务健康检查

```bash
# 检查所有服务状态
cd /opt/sidifensen_blog/script
docker-compose ps

# 检查后端服务健康状态
curl http://localhost:5000/actuator/health

# 检查前端服务
curl -I http://localhost:8000
curl -I http://localhost:7000

# 查看服务资源使用情况
docker stats
```

## 🔄 回滚操作

### GitHub Actions 部署回滚

如果最新部署出现问题，有以下回滚选项：

#### 方法 1：回滚到上一个工作版本（推荐）

```bash
# 1. 在本地回滚到上一个提交
git log --oneline -10  # 查看最近10次提交
git reset --hard <上一个正常版本的commit-hash>

# 2. 强制推送触发重新部署
git push origin main --force
```

#### 方法 2：使用 GitHub 界面回滚

1. 进入 GitHub 仓库
2. 找到最后一个正常工作的提交
3. 点击 **Revert** 按钮
4. 创建新的 revert 提交
5. 推送后自动触发部署

#### 方法 3：手动服务器回滚

如果 GitHub Actions 无法使用：

```bash
# 查看备份列表
ls -la /opt/backups/

# 停止当前服务
cd /opt/sidifensen_blog/script
docker-compose down

# 恢复备份版本
cd /opt
rm -rf sidifensen_blog
cp -r /opt/backups/sidifensen_blog_YYYYMMDD_HHMMSS sidifensen_blog

# 重新启动服务
cd /opt/sidifensen_blog/script
docker-compose up -d
```

### 紧急回滚脚本

创建快速回滚脚本：

```bash
# 在服务器上创建回滚脚本
cat > /opt/quick-rollback.sh << 'EOF'
#!/bin/bash
echo "🔄 开始紧急回滚..."

# 停止服务
cd /opt/sidifensen_blog/script
docker-compose down

# 获取最新备份
LATEST_BACKUP=$(ls -t /opt/backups/ | head -n1)
echo "📦 使用备份: $LATEST_BACKUP"

# 恢复备份
cd /opt
rm -rf sidifensen_blog
cp -r /opt/backups/$LATEST_BACKUP sidifensen_blog

# 重启服务
cd /opt/sidifensen_blog/script
docker-compose up -d

echo "✅ 回滚完成！"
EOF

# 设置执行权限
chmod +x /opt/quick-rollback.sh

# 紧急情况下运行
sudo /opt/quick-rollback.sh
```

## 📞 技术支持

### 系统要求

| 项目         | 最低要求                  | 推荐配置                  |
| ------------ | ------------------------- | ------------------------- |
| **操作系统** | Ubuntu 18.04+ / CentOS 7+ | Ubuntu 20.04+ / CentOS 8+ |
| **内存**     | 2GB                       | 4GB+                      |
| **磁盘空间** | 10GB                      | 20GB+                     |
| **CPU**      | 1 核                      | 2 核+                     |
| **网络**     | 1Mbps                     | 10Mbps+                   |

### 部署前检查清单

**基础环境检查：**

- [ ] 服务器已安装 Docker 和 Docker Compose
- [ ] SSH 密钥已正确配置
- [ ] 防火墙已开放必要端口 (5000, 7000, 8000, 9001, 15672)
- [ ] 域名 DNS 已正确解析（如果使用域名）
- [ ] 服务器有足够的磁盘空间和内存

**GitHub Actions 配置检查：**

- [ ] GitHub Secrets 已正确配置（HOST, USERNAME, SSH_KEY, PORT）
- [ ] `.github/workflows/deploy.yml` 文件已创建
- [ ] SSH 公钥已添加到服务器 `~/.ssh/authorized_keys`
- [ ] SSH 私钥已完整复制到 GitHub Secrets
- [ ] 测试 SSH 连接成功：`ssh -i ~/.ssh/id_rsa root@your-server-ip`

**部署流程验证：**

```mermaid
graph LR
    A["✅ 检查清单"] --> B["🔑 SSH测试"]
    B --> C["🚀 首次部署"]
    C --> D["📝 推送测试"]
    D --> E["🎉 自动部署成功"]

    style A fill:#e8f5e8
    style E fill:#c8e6c9
```

### 常用调试命令

```bash
# 查看系统资源
free -h          # 内存使用情况
df -h            # 磁盘使用情况
top              # CPU 和内存实时监控
netstat -tulpn   # 端口占用情况

# 查看服务状态
systemctl status docker                    # Docker 服务状态
docker --version                          # Docker 版本
docker-compose --version                  # Docker Compose 版本

# 查看容器日志
docker logs <container-name>              # 查看特定容器日志
docker-compose logs -f --tail=100         # 查看最近100行日志

# 清理系统资源
docker system prune -f                    # 清理未使用的镜像和容器
docker volume prune -f                    # 清理未使用的数据卷
```

### 获取帮助

如有部署问题，请提供以下信息：

1. **系统信息**：

   ```bash
   # 收集系统信息
   echo "=== 系统信息 ===" > debug-info.txt
   uname -a >> debug-info.txt
   cat /etc/os-release >> debug-info.txt

   echo "=== Docker 信息 ===" >> debug-info.txt
   docker --version >> debug-info.txt
   docker-compose --version >> debug-info.txt

   echo "=== 资源使用情况 ===" >> debug-info.txt
   free -h >> debug-info.txt
   df -h >> debug-info.txt

   echo "=== 服务状态 ===" >> debug-info.txt
   docker-compose ps >> debug-info.txt

   cat debug-info.txt
   ```

2. **错误日志**：提供完整的错误信息和日志
3. **部署步骤**：说明执行了哪些操作
4. **GitHub Actions 日志**：如果使用自动部署，提供 Actions 执行日志

### 联系方式

- 📧 **邮箱支持**：通过项目 Issues 提交问题
- 📚 **文档参考**：查看项目 README 和相关文档
- 🐛 **问题反馈**：在 GitHub 仓库提交 Issue
