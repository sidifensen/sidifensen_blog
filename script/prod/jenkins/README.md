# Jenkins 自动化部署指南

本目录包含 Jenkins + GitHub CI/CD 自动化部署相关的脚本、配置和文档。

## 📁 文件说明

| 文件名             | 用途                   | 执行位置                       |
| ------------------ | ---------------------- | ------------------------------ |
| `Jenkinsfile`      | Jenkins Pipeline 配置   | Jenkins 自动执行（项目根目录） |
| `jenkins-setup.sh` | Jenkins 安装和配置脚本  | 服务器上执行（可选，用于快速配置） |
| `env.example`      | 环境变量配置示例        | 配置文件模板                   |

---

## 🚀 GitHub + Jenkins CI/CD 架构

```
┌─────────────────────────────────────────────────────────────────┐
│                          GitHub 仓库                            │
│                   (https://github.com/sidifensen/sidifensen_blog) │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ Push 代码
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      GitHub Webhook                              │
│            (自动通知 Jenkins: /github-webhook/)                  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Jenkins CI/CD                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐ │
│  │  检出代码 │→│ 构建后端  │→│ 构建前端  │→│  部署到服务器     │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        生产服务器                                │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────────┐ │
│  │ Backend │  │ Admin   │  │  User   │  │  Nginx Gateway     │ │
│  │ :5000   │  │ :8000   │  │ :7000   │  │  :443 (SSL)       │ │
│  └─────────┘  └─────────┘  └─────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## ⚙️ GitHub Webhook 配置步骤

### 1. Jenkins 安装 GitHub 插件

1. 进入 `Manage Jenkins` → `Plugins`
2. 在 `Available plugins` 中搜索并安装 **GitHub** 插件
3. 重启 Jenkins（如果需要）

### 2. Jenkins 配置构建触发器

1. 进入 Jenkins 任务 `sidifensen-blog-deploy` → `Configure`
2. 在 `Build Triggers` 部分：
   - ✅ 勾选 **"GitHub hook trigger for GITScm polling"**
3. 保存配置

### 3. GitHub 端配置 Webhook

1. 进入 GitHub 仓库 `https://github.com/sidifensen/sidifensen_blog`
2. 点击 `Settings` → `Webhooks` → `Add webhook`
3. 配置：
   - **Payload URL**: `http://<your-jenkins-ip>:8080/github-webhook/`
   - **Content type**: `application/json`
   - **Events**: 选择 `Just the push events`
4. 点击 `Add webhook`

### 4. 测试 Webhook

1. 在 GitHub Webhooks 页面，找到刚创建的 webhook
2. 点击 `Test` → `Push` 发送测试事件
3. 检查 Jenkins 是否触发构建

---

## 🔧 常用命令

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
```

### 触发构建

```bash
# 方式一：推送代码自动触发（推荐）
git push origin main

# 方式二：手动触发
# 访问 Jenkins Web 界面，点击 "Build Now"
```

---

## 📝 详细文档

如需更详细的配置说明和故障排查，请查看：

- **[Jenkins 部署详细指南](./Jenkins部署指南.md)** - Jenkins 完整配置、Pipeline 说明和故障排查
