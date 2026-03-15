# Gitea 私有仓库配置详细指南

本文档详细介绍 Gitea 私有 Git 仓库的部署、配置和与 Jenkins 的集成。

## 📋 目录

- [方案概述](#方案概述)
- [Gitea 安装](#gitea-安装)
- [Gitea 初始化](#gitea-初始化)
- [创建私有仓库](#创建私有仓库)
- [Jenkins 集成](#jenkins-集成)
- [使用流程](#使用流程)
- [监控和维护](#监控和维护)
- [故障排查](#故障排查)

---

## 🎯 方案概述

### 为什么选择 Gitea？

- ✅ **轻量级**：资源占用小，适合小型服务器
- ✅ **功能完整**：支持 Webhook、PR、Issue 等
- ✅ **易于部署**：Docker 一键部署
- ✅ **界面友好**：类似 GitHub 的界面
- ✅ **完全私有**：数据完全掌控

### 架构流程

```
本地开发 → 推送代码 → Gitea 仓库 → Webhook 触发 → Jenkins → 自动部署 → 服务器
```

---

## 🚀 Gitea 安装

### Docker Compose 安装（推荐）

#### 1. 配置环境变量

```bash
cd script/prod/jenkins
cp .env.example .env
# 编辑 .env 文件，修改服务器 IP 等配置
```

#### 2. 启动 Gitea

```bash
docker-compose -f docker-compose-gitea.yml --env-file .env up -d
```

#### 3. 验证安装

访问 `http://your-server-ip:3000`，应该能看到 Gitea 安装页面。

---

## ⚙️ Gitea 初始化

### 1. 首次访问配置

1. 打开浏览器访问：`http://your-server-ip:3000`
2. 点击 **"立即安装"**
3. 配置以下信息：

   **数据库设置**：
   - 数据库类型：`SQLite3`（简单，适合小型项目）
   - 或选择 `MySQL`（如果已有 MySQL 服务）

   **常规设置**：
   - 站点标题：`Sidifensen Blog`
   - 仓库根目录：`/data/git/repositories`
   - Git LFS 根目录：`/data/git/lfs`
   - 运行用户名：`git`
   - SSH 服务域名：`your-server-ip`
   - SSH 端口：`2222`
   - HTTP 端口：`3000`
   - 应用 URL：`http://your-server-ip:3000/`

   **可选设置**：
   - 管理员账户设置（建议设置）
   - 邮件服务配置（可选）

4. 点击 **"立即安装"**

### 2. 创建管理员账户

如果未在安装时创建，可以：
1. 注册第一个账户
2. 该账户自动成为管理员

---

## 📦 创建私有仓库

### 1. 创建新仓库

1. 登录 Gitea
2. 点击右上角 **"+"** → **"新建仓库"**
3. 填写仓库信息：
   - **仓库名称**：`sidifensen_blog`
   - **仓库描述**：`Sidifensen Blog Project`
   - **可见性**：选择 **"私有"** ⭐
   - **初始化仓库**：可以勾选添加 README
4. 点击 **"创建仓库"**

### 2. 配置仓库访问

#### 方式一：HTTPS（推荐）

```bash
# 在本地项目目录执行
git remote add gitea http://your-server-ip:3000/username/sidifensen_blog.git

# 推送代码
git push gitea main
```

#### 方式二：SSH

```bash
# 配置 SSH（端口 2222）
git remote add gitea ssh://git@your-server-ip:2222/username/sidifensen_blog.git

# 推送代码
git push gitea main
```

**注意**：如果使用 SSH，需要在 Gitea 中添加 SSH 公钥：
1. 进入 **设置** → **SSH / GPG 密钥**
2. 点击 **"添加密钥"**
3. 粘贴你的 SSH 公钥内容

---

## 🔗 Jenkins 集成

### 1. 安装 Gitea 插件

1. 进入 Jenkins：`Manage Jenkins` → `Plugins`
2. 搜索并安装：**Gitea Plugin**
3. 重启 Jenkins（如果需要）

### 2. 配置 Gitea 服务器

1. 进入 `Manage Jenkins` → `Configure System`
2. 找到 **Gitea Servers** 部分
3. 点击 **"Add Gitea Server"**
4. 配置：
   - **Server URL**: `http://your-server-ip:3000`
   - **Display Name**: `Gitea Server`
   - **Credentials**: 点击 **"Add"** 创建凭据
     - **Kind**: `Gitea API Token`
     - **Token**: 在 Gitea 中生成 API Token（见下方）
     - **Description**: `Gitea API Token`

#### 生成 Gitea API Token

1. 登录 Gitea
2. 进入 **设置** → **应用** → **生成新令牌**
3. 令牌名称：`Jenkins`
4. 权限：勾选 `read:repository`, `read:user`
5. 点击 **"生成令牌"**
6. **复制令牌**（只显示一次）

### 3. 配置 Webhook

#### 在 Gitea 中配置

1. 进入仓库：`sidifensen_blog`
2. 点击 **设置** → **Webhooks**
3. 点击 **"添加 Webhook"** → **"Gitea"**
4. 配置 Webhook：
   - **目标 URL**: `http://jenkins-server-ip:8080/gitea-webhook/post`
   - **HTTP 方法**: `POST`
   - **POST Content Type**: `application/json`
   - **触发事件**：
     - ✅ **推送事件**（必须）
     - ✅ **创建事件**（可选）
     - ✅ **删除事件**（可选）
   - **激活**: ✅ 勾选
5. 点击 **"添加 Webhook"**

#### 在 Jenkins 中配置任务

1. 进入 Jenkins 任务：`sidifensen-blog-deploy`
2. 点击 **配置**
3. 在 **构建触发器** 部分：
   - ✅ 勾选 **"Gitea webhook trigger for GITScm polling"**
   - 或选择 **"Build when a change is pushed to Gitea"**
4. 保存配置

---

## 🔄 使用流程

### 日常开发流程

1. **本地开发**
   ```bash
   # 在本地进行开发
   git add .
   git commit -m "feat: 新功能"
   ```

2. **推送到 Gitea**
   ```bash
   # 推送到 Gitea 私有仓库
   git push gitea main
   ```

3. **自动触发部署**
   - Gitea 收到推送
   - Webhook 触发 Jenkins
   - Jenkins 自动执行 Pipeline
   - 自动部署到服务器

4. **查看部署状态**
   - 在 Jenkins 中查看构建日志
   - 检查部署是否成功

---

## 📊 监控和维护

### 查看 Gitea 日志

```bash
# 查看容器日志
docker logs -f sidifensen-gitea

# 查看 Gitea 应用日志
docker exec sidifensen-gitea tail -f /data/gitea/log/gitea.log
```

### 备份和恢复

#### 备份

```bash
# 备份数据卷
docker run --rm \
  -v sidifensen-gitea-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/gitea-backup-$(date +%Y%m%d).tar.gz /data
```

#### 恢复

```bash
# 恢复数据卷
docker run --rm \
  -v sidifensen-gitea-data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/gitea-backup-YYYYMMDD.tar.gz -C /
```

---

## 🔧 故障排查

### 问题 1: 无法访问 Gitea

**检查**：
1. 容器是否运行：`docker ps | grep gitea`
2. 端口是否开放：`netstat -tlnp | grep 3000`
3. 防火墙设置

### 问题 2: Webhook 未触发

**检查**：
1. Gitea Webhook 配置是否正确
2. Jenkins Gitea 插件是否安装
3. Jenkins 任务是否配置了触发器
4. 查看 Gitea Webhook 日志（在 Webhook 设置页面）

### 问题 3: 推送代码失败

**检查**：
1. 仓库 URL 是否正确
2. 认证信息是否正确
3. 网络连接是否正常

---

## 🔒 安全配置

### 1. 修改默认端口（重要）

**风险**：默认端口 `3000` 和 `2222` 容易被扫描和攻击。

**建议**：修改为非标准端口

在 `.env` 中修改：
```bash
# 修改为非标准端口，降低被扫描风险
GITEA_PORT=30001        # 原默认 3000
GITEA_SSH_PORT=22222    # 原默认 2222
```

**注意**：修改端口后，需要更新：
- Jenkins 中的 Gitea 服务器 URL
- Webhook 配置中的 URL
- 本地 Git 远程仓库地址

### 2. 禁止公开注册

在 `.env` 中设置：
```bash
GITEA_DISABLE_REGISTRATION=true
```

### 3. 配置防火墙（强烈推荐）

```bash
# 方式一：使用 iptables 限制访问
# 只允许特定 IP 访问（推荐）
sudo iptables -A INPUT -p tcp --dport 30001 -s 允许的IP -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 30001 -j DROP

# 方式二：使用云服务商安全组
# 在云控制台配置安全组规则，只允许特定 IP 访问

# 方式三：仅内网访问（最安全）
# 如果 Gitea 只在内网使用，不要暴露到公网
# 使用 VPN 或跳板机访问
```

### 4. 使用 HTTPS（生产环境必须）

配置 Nginx 反向代理，使用 HTTPS 访问 Gitea。

**为什么重要**：
- HTTP 明文传输，密码和代码可能被窃取
- HTTPS 加密传输，保护数据安全

### 5. 强密码和 SSH 密钥

- 使用复杂密码（至少 12 位，包含大小写字母、数字、特殊字符）
- 优先使用 SSH 密钥认证，禁用密码登录（如果可能）
- 定期更换密码和密钥

### 6. 定期备份

```bash
# 备份 Gitea 数据
docker exec sidifensen-gitea tar -czf /tmp/gitea-backup.tar.gz /data
```

---

## 📝 最佳实践

1. **使用私有仓库**：保护代码安全
2. **配置 Webhook**：实现自动触发
3. **定期备份**：防止数据丢失
4. **使用 HTTPS**：加密传输
5. **限制访问**：只允许授权用户

---

## 🎉 完成！

现在你已经配置好了 Gitea 私有仓库和 Jenkins 集成，可以：

1. ✅ 推送代码到私有仓库
2. ✅ 自动触发 Jenkins 部署
3. ✅ 完全掌控代码仓库

开始使用吧！🚀

