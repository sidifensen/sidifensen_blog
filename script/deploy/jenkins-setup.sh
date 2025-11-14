#!/bin/bash

# Sidifensen Blog Jenkins 环境初始化脚本
# 用于安装并启动 Jenkins Docker 容器

set -euo pipefail

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_title() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

command_exists() {
    command -v "$1" >/dev/null 2>&1
}

require_command() {
    local cmd=$1
    local install_hint=$2
    if ! command_exists "$cmd"; then
        print_error "未找到命令: $cmd"
        echo "请先安装：$install_hint"
        exit 1
    fi
}

require_root() {
    if [ "$(id -u)" -ne 0 ]; then
        print_error "请使用 root 用户或 sudo 执行此脚本"
        exit 1
    fi
}

# 默认配置，可通过环境变量覆盖
CONTAINER_NAME="${CONTAINER_NAME:-jenkins}"
JENKINS_HOME="${JENKINS_HOME:-/opt/jenkins_home}"
DEPLOY_PATH="${DEPLOY_PATH:-/opt/sidifensen_blog}"
HTTP_PORT="${HTTP_PORT:-8080}"
AGENT_PORT="${AGENT_PORT:-50000}"
JENKINS_IMAGE="${JENKINS_IMAGE:-jenkins/jenkins:lts}"

print_title "准备安装 Jenkins"

require_root
require_command docker "参考官方文档安装 Docker: https://docs.docker.com/engine/install/"

if ! command_exists docker-compose; then
    print_warn "未检测到 docker-compose，可选安装以便后续管理 (https://docs.docker.com/compose/install/)"
fi

print_info "使用配置:"
echo "  容器名称: $CONTAINER_NAME"
echo "  Jenkins 数据目录: $JENKINS_HOME"
echo "  部署目录: $DEPLOY_PATH"
echo "  访问端口: $HTTP_PORT"
echo "  Agent 端口: $AGENT_PORT"
echo "  Docker 镜像: $JENKINS_IMAGE"

# 准备 Jenkins 数据目录
print_title "初始化 Jenkins 数据目录"
mkdir -p "$JENKINS_HOME"
chown -R 1000:1000 "$JENKINS_HOME"
print_info "目录已准备: $JENKINS_HOME (owner: 1000:1000)"

# 准备部署目录（如果不存在则创建）
print_title "检查部署目录"
if [ ! -d "$DEPLOY_PATH" ]; then
    print_warn "部署目录不存在，正在创建: $DEPLOY_PATH"
    mkdir -p "$DEPLOY_PATH"
    chown -R 1000:1000 "$DEPLOY_PATH"
    print_info "部署目录已创建: $DEPLOY_PATH (owner: 1000:1000)"
else
    print_info "部署目录已存在: $DEPLOY_PATH"
    # 确保 Jenkins 用户（UID 1000）有权限访问
    chown -R 1000:1000 "$DEPLOY_PATH" 2>/dev/null || {
        print_warn "无法更改部署目录所有者，请确保 Jenkins 用户（UID 1000）有权限访问该目录"
    }
fi

# 如有旧容器先停止
print_title "清理旧 Jenkins 容器"
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}\$"; then
    print_warn "检测到同名容器，正在停止并移除..."
    docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
    docker rm "$CONTAINER_NAME" >/dev/null 2>&1 || true
    print_info "旧容器已移除"
else
    print_info "未检测到旧容器"
fi

# 拉取镜像
print_title "拉取 Jenkins 镜像"
docker pull "$JENKINS_IMAGE"

# 启动 Jenkins 容器
print_title "启动 Jenkins 容器"
docker run -d \
    --name "$CONTAINER_NAME" \
    -p "${HTTP_PORT}:8080" \
    -p "${AGENT_PORT}:50000" \
    -e TZ=Asia/Shanghai \
    -v "$JENKINS_HOME:/var/jenkins_home" \
    -v "$DEPLOY_PATH:$DEPLOY_PATH" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v "$(command -v docker):/usr/bin/docker" \
    -v /etc/localtime:/etc/localtime:ro \
    -v /etc/timezone:/etc/timezone:ro \
    --restart unless-stopped \
    "$JENKINS_IMAGE"

print_info "Jenkins 容器已启动"

# 安装 Node.js 依赖库
print_title "安装 Node.js 依赖库"
print_info "检查并安装 libatomic.so.1（Node.js 运行所需）..."
sleep 2  # 等待容器完全启动

if docker exec -u root "$CONTAINER_NAME" sh -c "command -v apt-get >/dev/null 2>&1"; then
    # Debian/Ubuntu 系统
    print_info "检测到 Debian/Ubuntu 系统，安装 libatomic1..."
    docker exec -u root "$CONTAINER_NAME" sh -c "apt-get update -qq && apt-get install -y libatomic1" || {
        print_warn "无法自动安装 libatomic1，请手动执行:"
        echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
        echo "  apt-get update && apt-get install -y libatomic1"
    }
elif docker exec -u root "$CONTAINER_NAME" sh -c "command -v yum >/dev/null 2>&1"; then
    # CentOS/RHEL 系统
    print_info "检测到 CentOS/RHEL 系统，安装 libatomic..."
    docker exec -u root "$CONTAINER_NAME" sh -c "yum install -y libatomic" || {
        print_warn "无法自动安装 libatomic，请手动执行:"
        echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
        echo "  yum install -y libatomic"
    }
elif docker exec -u root "$CONTAINER_NAME" sh -c "command -v apk >/dev/null 2>&1"; then
    # Alpine 系统
    print_info "检测到 Alpine 系统，安装 libatomic..."
    docker exec -u root "$CONTAINER_NAME" sh -c "apk add --no-cache libatomic" || {
        print_warn "无法自动安装 libatomic，请手动执行:"
        echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
        echo "  apk add --no-cache libatomic"
    }
else
    print_warn "无法识别系统包管理器，请手动安装 libatomic.so.1"
    echo "  进入容器: docker exec -it -u root ${CONTAINER_NAME} bash"
    echo "  然后根据系统类型安装相应的 libatomic 包"
fi

# 检查 Docker Compose
print_title "检查 Docker Compose"
print_info "检查 Docker Compose 是否可用..."
if docker exec "$CONTAINER_NAME" docker compose version >/dev/null 2>&1; then
    print_info "✅ Docker Compose V2 已可用 (docker compose)"
elif docker exec "$CONTAINER_NAME" docker-compose version >/dev/null 2>&1; then
    print_info "✅ Docker Compose V1 已可用 (docker-compose)"
else
    print_warn "未检测到 Docker Compose，尝试安装..."
    if docker exec -u root "$CONTAINER_NAME" sh -c "command -v apt-get >/dev/null 2>&1"; then
        # 尝试安装 docker-compose-plugin（Docker Compose V2）
        print_info "尝试安装 Docker Compose V2 (docker-compose-plugin)..."
        docker exec -u root "$CONTAINER_NAME" sh -c "apt-get update -qq && apt-get install -y docker-compose-plugin" || {
            print_warn "无法自动安装 docker-compose-plugin，尝试使用国内镜像安装 docker-compose V1..."
            # 使用国内镜像安装 docker-compose V1
            # 使用 gitee 镜像或直接下载（如果 gitee 不可用，使用 daocloud 镜像）
            docker exec -u root "$CONTAINER_NAME" sh -c "
                ARCH=\$(uname -m)
                OS=\$(uname -s | tr '[:upper:]' '[:lower:]')
                [ \"\$ARCH\" = \"x86_64\" ] && ARCH=\"amd64\" || true
                [ \"\$ARCH\" = \"aarch64\" ] && ARCH=\"arm64\" || true
                
                # 尝试使用 gitee 镜像
                curl -L \"https://gitee.com/mirrors/docker-compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose 2>/dev/null || \
                # 如果 gitee 失败，尝试使用 daocloud 镜像
                curl -L \"https://get.daocloud.io/docker/compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose 2>/dev/null || \
                # 如果都失败，使用 GitHub（可能需要代理）
                curl -L \"https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose && \
                chmod +x /usr/local/bin/docker-compose
            " || {
                print_warn "无法自动安装 Docker Compose，请手动安装:"
                echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
                echo "  # 方法1: 使用 gitee 镜像"
                echo "  curl -L \"https://gitee.com/mirrors/docker-compose/releases/download/v2.24.5/docker-compose-\$(uname -s | tr '[:upper:]' '[:lower:]')-\$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')\" -o /usr/local/bin/docker-compose"
                echo "  chmod +x /usr/local/bin/docker-compose"
                echo ""
                echo "  # 方法2: 使用 daocloud 镜像"
                echo "  curl -L \"https://get.daocloud.io/docker/compose/releases/download/v2.24.5/docker-compose-\$(uname -s | tr '[:upper:]' '[:lower:]')-\$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')\" -o /usr/local/bin/docker-compose"
                echo "  chmod +x /usr/local/bin/docker-compose"
            }
        }
    else
        print_warn "无法自动安装 Docker Compose，请手动安装:"
        echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
        echo "  # 使用 gitee 镜像（推荐）"
        echo "  ARCH=\$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')"
        echo "  OS=\$(uname -s | tr '[:upper:]' '[:lower:]')"
        echo "  curl -L \"https://gitee.com/mirrors/docker-compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose"
        echo "  chmod +x /usr/local/bin/docker-compose"
        echo ""
        echo "  # 或使用 daocloud 镜像"
        echo "  curl -L \"https://get.daocloud.io/docker/compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose"
        echo "  chmod +x /usr/local/bin/docker-compose"
    fi
    
    # 再次检查
    if docker exec "$CONTAINER_NAME" docker compose version >/dev/null 2>&1 || docker exec "$CONTAINER_NAME" docker-compose version >/dev/null 2>&1; then
        print_info "✅ Docker Compose 安装成功"
    else
        print_warn "⚠️  Docker Compose 可能未正确安装，请手动验证"
    fi
fi

# 初始化提示
print_title "后续步骤"
print_info "1. 等待 Jenkins 完成初始化，首次启动可能需要 1-2 分钟"
print_info "2. 在浏览器访问: http://<服务器IP>:${HTTP_PORT}"
print_info "3. 获取初始管理员密码:"
echo "   docker exec ${CONTAINER_NAME} cat /var/jenkins_home/secrets/initialAdminPassword"
print_info "4. 根据提示完成插件安装和管理员账户创建"

print_title "安装完成"
print_info "Jenkins 已成功启动并以 Docker 方式运行"
print_info "使用 'docker logs -f ${CONTAINER_NAME}' 查看初始化日志"

