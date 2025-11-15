# Jenkins 自动化部署详细指南

本文档详细介绍 Jenkins 的安装、配置和故障排查。

## 📋 目录

- [Jenkins 安装](#jenkins-安装)
- [Jenkins 配置](#jenkins-配置)
- [项目配置](#项目配置)
- [Pipeline 说明](#pipeline-说明)
- [故障排查](#故障排查)

---

## 🚀 Jenkins 安装

本项目使用自动化脚本 `jenkins-setup.sh` 来安装和配置 Jenkins，脚本会自动处理所有必要的配置和依赖安装。

### 前置要求

- **Docker**：确保已安装 Docker（脚本会自动检查）
- **root 权限**：需要使用 root 用户或 sudo 执行脚本

### 使用脚本安装

1. **进入部署脚本目录**：

   ```bash
   cd script/deploy
   ```

2. **执行安装脚本**：

   ```bash
   sudo ./jenkins-setup.sh
   ```

   脚本会自动完成以下操作：

   - ✅ 创建并配置 Jenkins 数据目录（`/opt/jenkins_home`）
   - ✅ 创建并配置部署目录（`/opt/sidifensen_blog`）
   - ✅ 拉取 Jenkins LTS 镜像
   - ✅ 启动 Jenkins Docker 容器（包含所有必要的挂载和配置）
   - ✅ 自动安装 Node.js 依赖库（`libatomic.so.1`）
   - ✅ 自动检查并安装 Docker Compose（如需要）
   - ✅ 配置时区为 `Asia/Shanghai`

### 自定义配置（可选）

脚本支持通过环境变量自定义配置：

```bash
# 自定义配置示例
export CONTAINER_NAME=my-jenkins
export JENKINS_HOME=/custom/jenkins_home
export DEPLOY_PATH=/custom/deploy_path
export HTTP_PORT=9090
export AGENT_PORT=50001
export JENKINS_IMAGE=jenkins/jenkins:lts

sudo ./jenkins-setup.sh
```

**默认配置**：

- 容器名称：`jenkins`
- Jenkins 数据目录：`/opt/jenkins_home`
- 部署目录：`/opt/sidifensen_blog`
- HTTP 端口：`8080`
- Agent 端口：`50000`
- Docker 镜像：`jenkins/jenkins:lts`

### 初始化 Jenkins

脚本执行完成后，按以下步骤初始化 Jenkins：

1. **等待容器启动**（首次启动可能需要 1-2 分钟）

2. **访问 Jenkins**：

   在浏览器中访问 `http://<服务器IP>:8080`

3. **获取初始管理员密码**：

   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

4. **完成初始化**：
   - 输入初始管理员密码
   - 安装推荐插件（或选择自定义插件）
   - 创建管理员账户

### 脚本自动处理的内容

`jenkins-setup.sh` 脚本已经自动处理了以下内容，**无需手动操作**：

- ✅ **Docker 容器配置**：自动配置所有必要的挂载和权限
- ✅ **时区设置**：自动设置为 `Asia/Shanghai`
- ✅ **部署目录挂载**：自动挂载部署目录到容器
- ✅ **Docker 访问权限**：自动配置容器内 Docker 访问权限
- ✅ **Node.js 依赖**：自动检测并安装 `libatomic.so.1`（支持 Debian/Ubuntu/CentOS/Alpine）
- ✅ **Docker Compose**：自动检测并尝试安装 Docker Compose（如需要）
- ✅ **国内镜像源**：自动配置阿里云镜像源以加速下载（Debian/Ubuntu 系统）

> 💡 **提示**：如果脚本执行过程中遇到网络问题导致依赖安装失败，脚本会给出详细的手动安装指导，你可以稍后手动完成安装。

---

## ⚙️ Jenkins 配置

### 1. 安装必要插件

进入 `Manage Jenkins` → `Plugins` → `Available plugins`，安装：

- **SSH Pipeline Steps** - SSH 连接插件
- **Pipeline** - Pipeline 支持
- **Git** - Git 支持
- **Gitea** - Gitea 集成（如果使用 Gitea）
- **NodeJS** - Node.js 工具自动安装支持
- **Eclipse Temurin Installer** - 提供 OpenJDK/Temurin 自动安装
- **Docker Pipeline** - Docker 支持（可选）

### 2. 配置工具

进入 `Manage Jenkins` → `System Configuration` → `Tools`（即 `Global Tool Configuration`）：

#### JDK 配置

- **Name**: `JDK-21`
- **方式一（推荐）**：选择 `Install automatically`，版本选择 `21`（需先安装 `Eclipse Temurin Installer` 插件，才能看到 adoptium.net 的版本列表）
- **方式二（自动安装失败时）**：手动下载 JDK21 并配置
  1. 在宿主机上下载 JDK21（如 OpenJDK 或 Temurin）链接: https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz 解压到 `/opt/jenkins_home/tools/hudson.model.JDK/JDK-21`
  2. 在 Jenkins 配置中，**JAVA_HOME** 填写：`/var/jenkins_home/tools/hudson.model.JDK/JDK-21`
  3. 确保 JDK 目录结构正确（应包含 `bin/java` 等文件）

#### Maven 配置

- **Name**: `Maven-3`
- **MAVEN_HOME**: `/usr/share/maven`（根据实际路径调整）
- 或选择 `Install automatically`，版本选择 `3.9.x`

#### Node.js 配置

- **Name**: `NodeJS-20`
- **安装路径**: 选择 `Install automatically`（需先安装 `NodeJS` 插件，才能启用自动安装）
- **Installer**: 选择 `Install from nodejs.org`
- **Version**: 选择最新的 `NodeJS 20.x`（LTS 版本即可，如 `NodeJS 20.15.0`）
- **Force 32bit architecture**: 保持未勾选（默认 64 位）
- **Global npm packages to install**: 留空（如需指定全局包，可在此填写 `包名@版本`）
- **Global npm packages refresh hours**: 保持默认 `72`

#### Git 配置

- **是否需要新增条目**：一般不需要。若服务器已安装 `git` 且在 Shell 中可直接执行，保留默认的 `Name=Default`、`Path to Git executable=git` 即可。
- **何时需要配置**：只有在服务器未预装 `git`，或希望 Jenkins 自动下载 git 二进制时，才需要在此新增安装项（勾选 `Install automatically` 或填写自定义路径）。
- **检查方式**：在 Jenkins 所在主机执行 `git --version`，确认命令可用即可。

### 3. 配置 SSH 凭据

进入 `Manage Jenkins` → `Credentials` → `System` → `Global credentials`：

#### 添加 SSH 私钥

1. 点击 `Add Credentials`
2. 类型选择 `SSH Username with private key`
3. 配置：
   - **ID**: `jenkins-ssh-key`
   - **Description**: `Jenkins SSH Key for Deployment`
   - **Username**: `root`（或你的服务器用户名）
   - **Private Key**: 选择 `Enter directly`，粘贴 SSH 私钥内容（如暂无密钥，请按下方步骤生成）

> 🔑 **如何生成部署专用 SSH 密钥**
>
> - **步骤 1：生成密钥**  
>   在 Jenkins 服务器上执行：
>
>   ```bash
>   ssh-keygen -t ed25519 -C "jenkins@sidifensen" -f ~/.ssh/jenkins_deploy
>   ```
>
>   若系统不支持 ed25519，可将 `-t ed25519` 替换为 `-t rsa`。当提示输入 passphrase 时，可直接回车留空。
>
> - **步骤 2：确认权限**  
>   确保 SSH 目录和密钥文件权限正确：
>
>   ```bash
>   chmod 700 ~/.ssh
>   chmod 600 ~/.ssh/jenkins_deploy
>   chmod 644 ~/.ssh/jenkins_deploy.pub
>   ```
>
> - **步骤 3：部署公钥**  
>   将 `~/.ssh/jenkins_deploy.pub` 内容追加到目标部署服务器用户的 `~/.ssh/authorized_keys` 中：
>
>   ```bash
>   cat ~/.ssh/jenkins_deploy.pub >> ~/.ssh/authorized_keys
>   chmod 600 ~/.ssh/authorized_keys
>   ```
>
>   如果 Jenkins 服务器和部署目标不是同一台，需要把公钥复制到目标服务器再执行上述命令。
>
> - **步骤 4：配置 Jenkins 凭据**  
>   使用 `cat ~/.ssh/jenkins_deploy` 查看私钥内容，复制粘贴到 Jenkins 凭据中 `Private Key` 的输入框。

#### 添加服务器配置（必须）

1. 点击 `Add Credentials`
2. 类型选择 `Secret text`
3. 配置：

   - **ID**: `jenkins-server-host`
   - **Secret**: 服务器 IP 地址
   - **Description**: `Deployment Server Host`

4. 再次添加：
   - **ID**: `jenkins-server-user`
   - **Secret**: 服务器用户名（如 `root`）
   - **Description**: `Deployment Server User`

> ✅ **图形界面详细操作示例**
>
> 1. 从 Jenkins 首页点击左上角 `Jenkins` → 左侧菜单选择 `Credentials`（或依次进入 `Manage Jenkins` → `Manage Credentials`）。
> 2. 在 `Stores scoped to Jenkins` 中点击 `System` → 进入 `Global credentials (unrestricted)`。
> 3. 右侧点击 `Add Credentials` 按钮。
> 4. 在弹出的表单中：
>    - `Kind` 选择 `Secret text`
>    - 勾选 `Specify` 以启用自定义 `ID`
>    - `Secret` 输入目标值：服务器 IP 或用户名
>    - `ID` 分别填写 `jenkins-server-host`、`jenkins-server-user`
>    - `Description` 填写用途说明，便于后续维护
> 5. 点击 `Create` 保存，两个条目分别创建完成后即可在 Jenkinsfile 中通过 `credentials('凭据ID')` 进行引用。

### 4. 配置系统环境变量

进入 `Manage Jenkins` → `System Configuration` → `System`，在页面中勾选 `Global properties` 的 `Environment variables`，添加：

- `DEPLOY_PATH`: `/opt/sidifensen_blog`
- `SERVER_HOST`: `your-server-ip`（如果未使用凭据）
- `SERVER_USER`: `root`（如果未使用凭据）

### 5. 配置 Gitea 服务器（如果使用 Gitea）

1. 进入 `Manage Jenkins` → `System Configuration` → `System`，
2. 找到 **Gitea Servers** 部分
3. 点击 **"Add Gitea Server"**
4. 配置：
   - **Display Name**: `Gitea Server`
   - **Server URL**: `http://your-server-ip:3000`
   - **Manage hooks**: 视需求勾选；如勾选，可在右侧 `Add` 按钮中直接新增凭据

**准备 Gitea 凭据**：

1. 登录 Gitea → **设置** → **应用** → **生成新令牌**
2. 令牌名称：`Jenkins`
3. 权限：勾选 `read:repository`, `read:user`, `write:repository`, `admin:repo_hook`
4. 在 `Gitea Servers` 的 `Credentials` 下拉右侧点击 `Add` → `Jenkins` → **`Username with password`**，填写：
   - **Username**（必须填写具有 webhook 管理权限的 Gitea 账号，通常是管理员账号）
   - **Password**（直接粘贴刚生成的 Gitea Token，或该账号密码）
   - **ID**: `gitea-hook-credential`
5. 在 Jenkins Pipeline 或 Freestyle Job 的 Git 配置中，通过 `credentialsId` 引用该凭据（如 `gitea-hook-credential`）

---

## 📦 项目配置

### 1. 创建 Jenkins 任务

1. 点击 `New Item`
2. 输入任务名称：`sidifensen-blog-deploy`
3. 选择 `Pipeline`
4. 点击 `OK`

### 2. 配置 Pipeline

在任务配置页面：

#### Pipeline 设置

- **Definition**: `Pipeline script from SCM`
- **SCM**: `Git`
- **Repository URL**: 你的 Git 仓库地址（Gitea 或 GitHub）
- **Credentials**: 选择 Git 凭据（如果需要）
- **Branches to build**: `*/main`（或你的主分支）
- **Script Path**: `Jenkinsfile`

#### 构建触发器

- **Poll SCM**: `H/5 * * * *`（每 5 分钟检查一次）
- 或配置 **Gitea webhook trigger**（如果使用 Gitea）
- 或配置 **GitHub hook trigger**（如果使用 GitHub）

### 3. 配置构建参数（可选）

在 `This project is parameterized` 中添加参数（示例）：

- **Name**：`DEPLOY_ENV`
- **Choices**（每行一个选项）：
  ```
  production
  staging
  ```
  若暂时只有正式环境，也可以只保留 `production`
- **Description**：`选择部署环境`

---

### 4. 配置 Gitea Webhook 触发 Jenkins（使用 Gitea 时必须执行）

1. 登录 Gitea 仓库 → `设置` → `Web 钩子`，点击右上角 `添加 Web 钩子`。
2. 选择 `Gitea` 类型（推荐），在 **Payload URL** 中填写 Jenkins 的 webhook 地址，通常为 `http(s)://<jenkins域名或IP>/gitea-webhook/post`。
3. 如启用了 Jenkins Gitea 插件，请在 Jenkins 任务的 “构建触发器” 中勾选 `Build when a change is pushed to Gitea`，并记下页面提示的 webhook URL；若需要 Secret，请保持与 Jenkins 端一致。
4. 在 Gitea 中至少勾选 `推送` 事件（如需 PR 触发可额外勾选 `Pull Request`），保存后点击 `测试传送`，确认返回状态码为 `200`。
5. 若测试失败，请检查 Jenkins 是否对外可访问、防火墙/反向代理是否放行，以及 Jenkins 中的构建触发器和凭据配置是否正确。

---

## 📊 Pipeline 阶段说明

Jenkinsfile 包含以下阶段：

1. **Checkout** - 检出代码
2. **Build Backend** - 构建 Spring Boot 后端
3. **Build Frontend Admin** - 构建管理端前端
4. **Build Frontend User** - 构建用户端前端
5. **Package Artifacts** - 打包部署文件
6. **Deploy to Server** - 部署到服务器

每个阶段都有成功/失败的日志输出，便于排查问题。

---

## 🔧 故障排查

### 问题 1: SSH 连接失败

**错误信息**:

```
Permission denied (publickey)
```

**解决方案**:

1. 检查 SSH 私钥是否正确配置
2. 确保服务器上已添加对应的公钥：
   ```bash
   # 在服务器上执行
   cat ~/.ssh/authorized_keys
   ```
3. 测试 SSH 连接：
   ```bash
   ssh -i /path/to/private/key user@server
   ```

### 问题 2: 构建工具未找到

**错误信息**:

```
mvn: command not found
npm: command not found
```

**解决方案**:

1. 检查工具配置路径是否正确
2. 在 Jenkins 系统配置中安装自动工具
3. 确保 Jenkins 用户有权限访问工具

### 问题 3: Docker 命令执行失败

**错误信息**:

```
docker: command not found
permission denied
```

**解决方案**:

> ✅ **注意**：如果使用 `jenkins-setup.sh` 脚本安装，脚本已经自动配置了 Docker 访问权限，通常不会出现此问题。

如果仍然遇到问题：

1. 检查容器是否正常挂载了 Docker socket：

   ```bash
   docker exec jenkins ls -la /var/run/docker.sock
   ```

2. 检查容器内的 docker 命令：

   ```bash
   docker exec jenkins docker --version
   ```

3. 如果问题仍然存在，可以重新运行安装脚本：
   ```bash
   sudo ./jenkins-setup.sh
   ```

### 问题 4: 文件上传失败

**错误信息**:

```
scp: Connection refused
```

**解决方案**:

1. 检查服务器 SSH 服务是否运行
2. 检查防火墙设置
3. 验证服务器地址和端口

### 问题 5: 部署脚本执行失败

**错误信息**:

```
docker-compose: command not found
```

**解决方案**:

> ✅ **注意**：`jenkins-setup.sh` 脚本已经自动检测并尝试安装 Docker Compose，通常不会出现此问题。

如果仍然遇到问题：

1. **检查 Docker Compose 是否已安装**：

   ```bash
   # 检查 Docker Compose V2（推荐）
   docker exec jenkins docker compose version

   # 检查 Docker Compose V1
   docker exec jenkins docker-compose version
   ```

2. **如果未安装，手动安装**：

   ```bash
   # 进入容器
   docker exec -it -u root jenkins bash

   # 方法1: 使用包管理器安装（推荐）
   apt-get update
   apt-get install -y docker-compose-plugin

   # 方法2: 使用国内镜像下载安装
   ARCH=$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')
   OS=$(uname -s | tr '[:upper:]' '[:lower:]')
   # 使用 gitee 镜像
   curl -L "https://gitee.com/mirrors/docker-compose/releases/download/v2.24.5/docker-compose-${OS}-${ARCH}" -o /usr/local/bin/docker-compose
   chmod +x /usr/local/bin/docker-compose

   # 验证
   docker-compose version
   exit
   ```

3. **如果使用 Docker Compose V2**（Docker >= 20.10），Jenkinsfile 会自动使用 `docker compose` 命令（注意是空格）

4. 检查部署路径是否正确

5. 查看服务器上的部署日志

### 问题 6: Node.js 无法运行

**错误信息**:

```
node: error while loading shared libraries: libatomic.so.1: cannot open shared object file
```

**解决方案**:

> ✅ **注意**：`jenkins-setup.sh` 脚本已经自动检测并安装 `libatomic.so.1`，通常不会出现此问题。

如果仍然遇到问题（可能是脚本安装失败或网络问题）：

1. **验证 libatomic.so.1 是否已安装**：

   ```bash
   docker exec jenkins sh -c "ldconfig -p 2>/dev/null | grep libatomic.so.1 || find /usr/lib* /lib* -name 'libatomic.so.1' 2>/dev/null | head -1"
   ```

2. **如果未安装，手动安装**：

   ```bash
   # Debian/Ubuntu 系统
   docker exec -u root jenkins apt-get update && docker exec -u root jenkins apt-get install -y libatomic1

   # CentOS/RHEL 系统
   docker exec -u root jenkins yum install -y libatomic

   # Alpine 系统
   docker exec -u root jenkins apk add --no-cache libatomic
   ```

3. **验证 Node.js**：
   ```bash
   docker exec jenkins node -v
   ```

### 问题 7: Webhook 未触发

**检查**:

1. Gitea/GitHub Webhook 配置是否正确
2. Jenkins 插件是否安装
3. Jenkins 任务是否配置了触发器
4. 查看 Webhook 日志

---

## 🔒 安全建议

1. **使用凭据管理**：敏感信息使用 Jenkins 凭据管理，不要硬编码
2. **限制访问权限**：配置 Jenkins 用户权限，只允许必要的人员访问
3. **定期更新**：保持 Jenkins 和插件为最新版本
4. **备份配置**：定期备份 Jenkins 配置和数据
5. **日志管理**：配置日志轮转，避免日志文件过大

---

## 📝 最佳实践

1. **使用 Pipeline**：使用 Jenkinsfile 管理构建流程，版本控制
2. **环境分离**：为不同环境（开发、测试、生产）创建不同的 Pipeline
3. **回滚机制**：保留历史构建产物，支持快速回滚
4. **通知机制**：配置构建成功/失败通知（邮件、钉钉、企业微信等）
5. **监控告警**：配置构建监控和告警

---

## 🎯 下一步

1. **执行安装脚本**：运行 `sudo ./jenkins-setup.sh` 完成 Jenkins 安装
2. **初始化 Jenkins**：访问 Jenkins Web 界面，完成初始配置
3. **配置 Jenkins**：按照本文档的"Jenkins 配置"部分安装插件和配置工具
4. **创建 Jenkins 任务**：按照"项目配置"部分创建 Pipeline 任务
5. **测试部署**：手动触发一次构建，验证部署流程
6. **配置自动触发**：配置 Webhook 或定时触发机制
7. **设置通知和监控**：配置构建成功/失败通知

如有问题，请查看 Jenkins 日志或联系项目维护者。

**查看 Jenkins 日志**：

```bash
docker logs -f jenkins
```
