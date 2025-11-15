# 自动化部署指南

本目录包含 Jenkins 自动化部署和 Gitea 私有仓库相关的脚本、配置和文档。

## 📁 文件说明

### Jenkins 部署相关

| 文件名              | 用途                       | 执行位置                           |
| ------------------- | -------------------------- | ---------------------------------- |
| `Jenkinsfile`       | Jenkins Pipeline 配置      | Jenkins 自动执行（项目根目录）     |
| `jenkins-setup.sh`  | Jenkins 安装和配置脚本     | 服务器上执行（可选，用于快速配置） |
| `jenkins-deploy.sh` | 服务器端部署脚本（已弃用） | 已集成到 Jenkinsfile，不再单独使用 |

### Gitea 私有仓库相关

| 文件名                     | 用途                     | 执行位置            |
| -------------------------- | ------------------------ | ------------------- |
| `docker-compose-gitea.yml` | Gitea Docker 配置        | Docker Compose 使用 |
| `gitea-setup.sh`           | Gitea 初始化脚本         | 本地或服务器执行    |
| `create-gitea-user.sql`    | Gitea MySQL 用户创建脚本 | MySQL 执行（可选）  |
| `env.example`              | 环境变量配置示例         | 配置文件模板        |

### 文档相关

| 文件名               | 用途                       |
| -------------------- | -------------------------- |
| `Jenkins部署指南.md` | Jenkins 详细配置和部署指南 |
| `Gitea配置指南.md`   | Gitea 详细配置和维护指南   |

---

## 🚀 快速开始

### 前置条件

- ✅ 服务器已安装 Docker 和 Docker Compose
- ✅ 服务器已配置 SSH 访问
- ✅ 服务器有足够资源（建议 2GB+ 内存）

---

## 📋 完整部署流程

### 步骤 1: 部署 Gitea 私有仓库

#### 1.1 配置环境变量

```bash
cd script/deploy
cp env.example .env
# 编辑 .env 文件，修改服务器 IP
# GITEA_DOMAIN=your-server-ip
# GITEA_PORT=3000
```

#### 1.2 启动 Gitea

```bash
docker-compose -f docker-compose-gitea.yml --env-file .env up -d
```

#### 1.3 初始化 Gitea

1. 访问 `http://your-server-ip:3000`
2. 点击 **"立即安装"**
3. 数据库类型选择：`SQLite3`
4. 配置管理员账户
5. 完成安装

#### 1.4 创建私有仓库

1. 登录 Gitea
2. 点击 **"+"** → **"新建仓库"**
3. 仓库名称：`sidifensen_blog`
4. 选择 **"私有"**
5. 创建仓库

#### 1.5 配置本地推送

```bash
# 添加 Gitea 远程仓库
git remote add gitea http://your-server-ip:3000/username/sidifensen_blog.git

# 推送代码
git push gitea main
```

---

### 步骤 2: 安装 Jenkins

#### 2.1 Docker 安装（推荐）

**方式一：使用脚本快速安装（推荐）**

```bash
cd script/deploy
chmod +x jenkins-setup.sh
sudo ./jenkins-setup.sh
```

**方式二：手动安装**

```bash
# 创建 Jenkins 数据目录和部署目录
sudo mkdir -p /opt/jenkins_home
sudo mkdir -p /opt/sidifensen_blog
sudo chown -R 1000:1000 /opt/jenkins_home

# 运行 Jenkins（需要挂载部署目录）
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v /opt/jenkins_home:/var/jenkins_home \
  -v /opt/sidifensen_blog:/opt/sidifensen_blog \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

**重要提示**：

- Jenkins 容器需要挂载部署目录 `/opt/sidifensen_blog`，以便 Pipeline 可以直接部署
- Jenkins 容器需要访问 Docker，因此需要挂载 `/var/run/docker.sock`

#### 2.2 初始化 Jenkins

1. 访问 `http://your-server:8080`
2. 获取初始密码：
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
3. 安装推荐插件
4. 创建管理员账户

#### 2.3 安装 Node.js 依赖库（重要）

Jenkins 容器中需要安装 `libatomic1` 库才能运行 Node.js：

```bash
docker exec -u root jenkins apt-get update
docker exec -u root jenkins apt-get install -y libatomic1
```

**注意**：只需执行一次，后续构建会自动检测该库是否存在。

---

### 步骤 3: 配置 Jenkins

#### 3.1 安装必要插件

进入 `Manage Jenkins` → `Plugins` → `Available plugins`，安装：

- **SSH Pipeline Steps**
- **Pipeline**
- **Git**
- **Gitea Plugin**（用于 Gitea 集成）

#### 3.2 配置工具

进入 `Manage Jenkins` → `Global Tool Configuration`：

- **JDK**: `JDK-21`，选择 `Install automatically`，版本 `21`
- **Maven**: `Maven-3`，选择 `Install automatically`，版本 `3.9.x`
- **Node.js**: `NodeJS-20`，选择 `Install automatically`，版本 `20.x.x`

#### 3.3 配置 SSH 凭据

进入 `Manage Jenkins` → `Credentials` → `System` → `Global credentials`：

1. 点击 `Add Credentials`
2. 类型：`SSH Username with private key`
3. 配置：
   - **ID**: `jenkins-ssh-key`
   - **Username**: `root`
   - **Private Key**: 粘贴 SSH 私钥
   - **Description**: `Jenkins SSH Key`

#### 3.4 配置环境变量

进入 `Manage Jenkins` → `Configure System`：

在 `Global properties` → `Environment variables` 添加：

- `DEPLOY_PATH`: `/opt/sidifensen_blog`（必须与 Jenkins 容器挂载的目录一致）

**重要提示**：

- `DEPLOY_PATH` 必须与启动 Jenkins 容器时挂载的目录路径完全一致
- 如果使用默认路径 `/opt/sidifensen_blog`，确保容器启动时已挂载该目录

#### 3.5 配置 Gitea 服务器

1. 进入 `Manage Jenkins` → `Configure System`
2. 找到 **Gitea Servers** 部分
3. 点击 **"Add Gitea Server"**
4. 配置：
   - **Server URL**: `http://your-server-ip:3000`
   - **Display Name**: `Gitea Server`
   - **Credentials**: 创建 Gitea API Token 凭据

**生成 Gitea API Token**：

1. 登录 Gitea → **设置** → **应用** → **生成新令牌**
2. 令牌名称：`Jenkins`
3. 权限：勾选 `read:repository`, `read:user`
4. 复制令牌并添加到 Jenkins 凭据

---

### 步骤 4: 创建 Jenkins 任务

#### 4.1 创建 Pipeline 任务

1. 点击 `New Item`
2. 任务名称：`sidifensen-blog-deploy`
3. 选择 `Pipeline`
4. 点击 `OK`

#### 4.2 配置 Pipeline

- **Definition**: `Pipeline script from SCM`
- **SCM**: `Git`
- **Repository URL**: `http://your-server-ip:3000/username/sidifensen_blog.git`
- **Credentials**: 添加 Gitea 凭据（如果需要）
- **Branches to build**: `*/main`
- **Script Path**: `Jenkinsfile`（项目根目录下的 Jenkinsfile）

**Pipeline 说明**：

- Pipeline 会自动构建后端（Maven）和前端（Node.js）
- 构建完成后会自动部署到服务器并重启 Docker 容器
- 无需手动执行 `jenkins-deploy.sh`，所有部署逻辑已集成到 Pipeline 中

#### 4.3 配置构建触发器

在 **构建触发器** 部分：

- ✅ 勾选 **"Gitea webhook trigger for GITScm polling"**
- 或选择 **"Build when a change is pushed to Gitea"**

---

### 步骤 5: 配置 Gitea Webhook

1. 进入 Gitea 仓库 → **设置** → **Webhooks**
2. 点击 **"添加 Webhook"** → **"Gitea"**
3. 配置：
   - **目标 URL**: `http://jenkins-server-ip:8080/gitea-webhook/post`
   - **HTTP 方法**: `POST`
   - **触发事件**: ✅ **推送事件**
   - **激活**: ✅ 勾选
4. 点击 **"添加 Webhook"**

---

### 步骤 6: 测试部署

#### 手动触发

1. 进入 Jenkins 任务页面
2. 点击 `Build Now`
3. 查看构建日志

#### 自动触发

推送代码到 `main` 分支：

```bash
git push gitea main
```

Jenkins 会自动检测并执行部署。

---

## ✅ 验证部署

部署成功后，访问：

- 后端 API: `http://your-server:5000`
- 管理端前端: `http://your-server:8000`
- 用户端前端: `http://your-server:7000`

---

## ⚙️ 工作流程

```
本地开发
  ↓
git push gitea main
  ↓
Gitea 接收推送
  ↓
Webhook 触发 Jenkins
  ↓
Jenkins 自动构建和部署
  ↓
服务器更新完成
```

每次推送代码到主分支，Jenkins 会自动：

1. **准备工具链**：检查并配置 JDK、Maven、Node.js
2. **检出代码**：从 Gitea 仓库拉取最新代码
3. **构建后端**：使用 Maven 构建 Spring Boot 应用
4. **构建管理端前端**：使用 Node.js 构建管理端前端
5. **构建用户端前端**：使用 Node.js 构建用户端前端
6. **部署到服务器**：
   - 复制后端 jar 包到部署目录
   - 复制前端 dist 目录到部署目录
   - 停止旧容器
   - 重新构建并启动新容器
7. **健康检查**：验证服务是否正常启动

---

## 🔧 常用命令

### Gitea 管理

```bash
cd script/deploy

# 启动
docker-compose -f docker-compose-gitea.yml --env-file .env up -d

# 停止
docker-compose -f docker-compose-gitea.yml --env-file .env down

# 查看日志
docker logs -f sidifensen-gitea

# 重启
docker-compose -f docker-compose-gitea.yml --env-file .env restart

# 查看状态
docker-compose -f docker-compose-gitea.yml --env-file .env ps
```

### Jenkins 管理

```bash
# 查看日志
docker logs -f jenkins

# 重启
docker restart jenkins

# 查看容器状态
docker ps | grep jenkins

# 进入 Jenkins 容器
docker exec -it jenkins bash

# 安装 Node.js 依赖库（首次需要）
docker exec -u root jenkins apt-get update
docker exec -u root jenkins apt-get install -y libatomic1

# 检查部署目录挂载
docker inspect jenkins | grep -A 10 Mounts
```

---

## 🔧 常见问题

### SSH 连接失败

```bash
# 确保服务器上已添加公钥
cat ~/.ssh/authorized_keys

# 测试 SSH 连接
ssh -i /path/to/private/key user@server
```

### Docker 权限问题

```bash
# 将 Jenkins 用户添加到 docker 组
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Webhook 未触发

1. 检查 Gitea Webhook 配置是否正确
2. 检查 Jenkins Gitea 插件是否安装
3. 查看 Gitea Webhook 日志（在 Webhook 设置页面）

### 构建工具未找到

检查 `Global Tool Configuration` 中的工具路径是否正确，或使用自动安装选项。

### Node.js 运行失败

如果 Pipeline 中 Node.js 运行失败，提示缺少 `libatomic.so.1` 库：

```bash
# 在 Jenkins 容器中安装依赖库
docker exec -u root jenkins apt-get update
docker exec -u root jenkins apt-get install -y libatomic1
```

### 部署目录无法访问

如果 Pipeline 报错 "无法访问部署目录"：

1. 检查 Jenkins 容器是否挂载了部署目录：

   ```bash
   docker inspect jenkins | grep -A 10 Mounts
   ```

2. 确保挂载路径正确：
   ```bash
   # 重新启动 Jenkins 容器并挂载目录
   docker stop jenkins
   docker rm jenkins
   docker run -d \
     --name jenkins \
     -p 8080:8080 \
     -p 50000:50000 \
     -v /opt/jenkins_home:/var/jenkins_home \
     -v /opt/sidifensen_blog:/opt/sidifensen_blog \
     -v /var/run/docker.sock:/var/run/docker.sock \
     jenkins/jenkins:lts
   ```

### Docker Compose 未找到

如果 Pipeline 报错 "未找到 docker-compose 或 docker compose 命令"：

1. 检查 Docker 版本（Docker 20.10+ 已包含 Compose V2）：

   ```bash
   docker compose version
   ```

2. 如果未安装，在 Jenkins 容器中安装：
   ```bash
   docker exec -it -u root jenkins bash
   # 在容器内安装 Docker Compose（参考 Jenkinsfile 中的说明）
   ```

---

## 📖 详细文档

如需更详细的配置说明和故障排查，请查看：

- **[Jenkins 部署详细指南](./Jenkins部署指南.md)** - Jenkins 完整配置、Pipeline 说明和故障排查
- **[Gitea 配置详细指南](./Gitea配置指南.md)** - Gitea 完整配置、监控维护和故障排查

---

## 🔒 安全提示

### 通用安全建议

1. 确保 Jenkins SSH 密钥安全
2. 生产环境建议使用非 root 用户
3. 定期更新服务器和依赖
4. 配置防火墙规则，限制访问

### Gitea 安全加固

1. **修改默认端口**（重要）

   - 将 HTTP 端口从 `3000` 改为非标准端口（如 `30001`）
   - 将 SSH 端口从 `2222` 改为非标准端口（如 `22222`）
   - 降低被自动扫描和攻击的风险

2. **防火墙配置**（强烈推荐）

   ```bash
   # 只允许特定 IP 访问 Gitea（如果可能）
   # 或使用云服务商的安全组规则
   ```

3. **使用 HTTPS**（生产环境必须）

   - 配置 Nginx 反向代理，使用 HTTPS
   - 避免 HTTP 明文传输

4. **禁用公开注册**

   - 设置 `GITEA_DISABLE_REGISTRATION=true`

5. **仅内网访问**（最安全）

   - 如果 Gitea 只在内网使用，不要暴露到公网
   - 使用 VPN 或跳板机访问

6. **强密码策略**

   - 使用复杂密码
   - 定期更换密码

7. **SSH 密钥认证**
   - 优先使用 SSH 密钥，而非密码登录

---

## 🎯 下一步

1. 按照本文档完成配置
2. 推送代码测试自动部署
3. 查看详细文档解决遇到的问题

## 📝 重要提示

### Pipeline 部署说明

- **Jenkinsfile** 位于项目根目录，包含完整的构建和部署流程
- Pipeline 会自动处理所有部署步骤，无需手动执行 `jenkins-deploy.sh`
- 部署目录必须在 Jenkins 容器启动时挂载，路径为 `/opt/sidifensen_blog`
- Pipeline 使用 `docker-compose-ssl.yml` 进行部署，确保 `script/ssl/.env` 文件已正确配置

### 首次部署前检查清单

- [ ] Gitea 已部署并创建仓库
- [ ] Jenkins 已安装并配置工具（JDK、Maven、Node.js）
- [ ] Jenkins 容器已挂载部署目录 `/opt/sidifensen_blog`
- [ ] Jenkins 容器已安装 `libatomic1` 库
- [ ] Jenkins 已配置 Gitea 服务器和凭据
- [ ] Gitea Webhook 已配置
- [ ] 服务器上已配置 `script/ssl/.env` 文件
- [ ] 服务器上已准备好 `docker-compose-ssl.yml` 文件

如有问题，请查看详细文档或联系项目维护者。
