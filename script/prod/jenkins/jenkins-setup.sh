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

# 带超时的命令执行（如果 timeout 命令可用）
run_with_timeout() {
    local timeout_seconds=$1
    shift
    if command_exists timeout; then
        timeout "$timeout_seconds" "$@"
    else
        print_warn "timeout 命令不可用，直接执行命令（无超时保护）"
        "$@"
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

# 获取 docker 组的 GID，用于容器内权限配置
DOCKER_GID=$(getent group docker | cut -d: -f3)
if [ -z "$DOCKER_GID" ]; then
    print_warn "未找到 docker 组，Jenkins 容器可能无法访问 Docker"
    DOCKER_GROUP_ADD=""
else
    print_info "检测到 docker 组 GID: $DOCKER_GID"
    DOCKER_GROUP_ADD="--group-add $DOCKER_GID"
fi

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
    $DOCKER_GROUP_ADD \
    --restart unless-stopped \
    "$JENKINS_IMAGE"

print_info "Jenkins 容器已启动"

# 安装 Node.js 依赖库
print_title "安装 Node.js 依赖库"
print_info "检查并安装 libatomic.so.1（Node.js 运行所需）..."
print_info "等待容器完全启动..."
sleep 5  # 等待容器完全启动

# 检查容器是否运行
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}\$"; then
    print_error "Jenkins 容器未运行，无法安装依赖库"
    exit 1
fi

# 检查 libatomic.so.1 是否已存在
if docker exec "$CONTAINER_NAME" sh -c "ldconfig -p 2>/dev/null | grep -q libatomic.so.1 || find /usr/lib* /lib* -name 'libatomic.so.1' 2>/dev/null | head -1 | grep -q ."; then
    print_info "✅ libatomic.so.1 已存在，跳过安装"
else
    print_info "检测到缺少 libatomic.so.1，开始安装..."
    
    if docker exec -u root "$CONTAINER_NAME" sh -c "command -v apt-get >/dev/null 2>&1"; then
        # Debian/Ubuntu 系统
        print_info "检测到 Debian/Ubuntu 系统，安装 libatomic1..."
        
        # 尝试配置国内镜像源（可选，如果网络慢）
        print_info "尝试配置国内镜像源以加速下载..."
        docker exec -u root "$CONTAINER_NAME" sh -c "
            if [ ! -f /etc/apt/sources.list.backup ]; then
                cp /etc/apt/sources.list /etc/apt/sources.list.backup 2>/dev/null || true
                # 检测 Debian 版本并配置阿里云镜像源
                DEBIAN_VERSION=\$(cat /etc/debian_version 2>/dev/null | cut -d. -f1)
                if [ \"\$DEBIAN_VERSION\" = \"11\" ] || [ \"\$DEBIAN_VERSION\" = \"bullseye\" ]; then
                    cat > /etc/apt/sources.list << 'EOF'
deb http://mirrors.aliyun.com/debian/ bullseye main contrib non-free
deb http://mirrors.aliyun.com/debian/ bullseye-updates main contrib non-free
deb http://mirrors.aliyun.com/debian-security bullseye-security main contrib non-free
EOF
                elif [ \"\$DEBIAN_VERSION\" = \"12\" ] || [ \"\$DEBIAN_VERSION\" = \"bookworm\" ]; then
                    cat > /etc/apt/sources.list << 'EOF'
deb http://mirrors.aliyun.com/debian/ bookworm main contrib non-free
deb http://mirrors.aliyun.com/debian/ bookworm-updates main contrib non-free
deb http://mirrors.aliyun.com/debian-security bookworm-security main contrib non-free
EOF
                fi
            fi
        " || print_warn "镜像源配置失败，使用默认源"
        
        print_info "正在更新软件包列表（最多等待3分钟）..."
        # 使用超时机制，并设置非交互式环境变量
        if run_with_timeout 180 docker exec -u root -e DEBIAN_FRONTEND=noninteractive "$CONTAINER_NAME" sh -c "apt-get update -qq 2>&1" && \
           run_with_timeout 180 docker exec -u root -e DEBIAN_FRONTEND=noninteractive "$CONTAINER_NAME" sh -c "apt-get install -y --no-install-recommends libatomic1 2>&1"; then
            print_info "✅ libatomic1 安装成功"
        else
            print_error "❌ 无法自动安装 libatomic1（可能超时或网络问题）"
            print_warn "请手动执行以下命令（使用国内镜像源）:"
            echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
            echo "  cat > /etc/apt/sources.list << 'EOF'"
            echo "  deb http://mirrors.aliyun.com/debian/ bullseye main contrib non-free"
            echo "  deb http://mirrors.aliyun.com/debian/ bullseye-updates main contrib non-free"
            echo "  deb http://mirrors.aliyun.com/debian-security bullseye-security main contrib non-free"
            echo "  EOF"
            echo "  export DEBIAN_FRONTEND=noninteractive"
            echo "  apt-get update"
            echo "  apt-get install -y libatomic1"
            print_warn "或者跳过此步骤，稍后手动安装（不影响 Docker 权限测试）"
            # 不退出，允许继续执行
        fi
    elif docker exec -u root "$CONTAINER_NAME" sh -c "command -v yum >/dev/null 2>&1"; then
        # CentOS/RHEL 系统
        print_info "检测到 CentOS/RHEL 系统，安装 libatomic..."
        print_info "正在安装（可能需要一些时间，最多等待5分钟）..."
        if run_with_timeout 300 docker exec -u root "$CONTAINER_NAME" sh -c "yum install -y libatomic 2>&1"; then
            print_info "✅ libatomic 安装成功"
        else
            print_error "❌ 无法自动安装 libatomic（可能超时或网络问题）"
            print_warn "请手动执行以下命令:"
            echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
            echo "  yum install -y libatomic"
            exit 1
        fi
    elif docker exec -u root "$CONTAINER_NAME" sh -c "command -v apk >/dev/null 2>&1"; then
        # Alpine 系统
        print_info "检测到 Alpine 系统，安装 libatomic..."
        print_info "正在安装（可能需要一些时间，最多等待5分钟）..."
        if run_with_timeout 300 docker exec -u root "$CONTAINER_NAME" sh -c "apk add --no-cache libatomic 2>&1"; then
            print_info "✅ libatomic 安装成功"
        else
            print_error "❌ 无法自动安装 libatomic（可能超时或网络问题）"
            print_warn "请手动执行以下命令:"
            echo "  docker exec -it -u root ${CONTAINER_NAME} sh"
            echo "  apk add --no-cache libatomic"
            exit 1
        fi
    else
        print_error "❌ 无法识别系统包管理器"
        print_warn "请手动安装 libatomic.so.1:"
        echo "  docker exec -it -u root ${CONTAINER_NAME} bash"
        echo "  然后根据系统类型安装相应的 libatomic 包"
        exit 1
    fi
    
    # 验证安装
    print_info "验证 libatomic.so.1 安装..."
    if docker exec "$CONTAINER_NAME" sh -c "ldconfig -p 2>/dev/null | grep -q libatomic.so.1 || find /usr/lib* /lib* -name 'libatomic.so.1' 2>/dev/null | head -1 | grep -q ."; then
        print_info "✅ libatomic.so.1 安装验证成功"
    else
        print_warn "⚠️  安装完成但验证失败，请手动检查"
    fi
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
    INSTALLED=false
    
    # 方法1: 从宿主机复制（最简单，如果宿主机已安装）
    if command -v docker-compose >/dev/null 2>&1; then
        print_info "方法1: 从宿主机复制 docker-compose..."
        if docker cp "$(command -v docker-compose)" "${CONTAINER_NAME}:/usr/local/bin/docker-compose" 2>/dev/null && \
           docker exec -u root "$CONTAINER_NAME" chmod +x /usr/local/bin/docker-compose 2>/dev/null && \
           docker exec "$CONTAINER_NAME" docker-compose version >/dev/null 2>&1; then
            print_info "✅ 从宿主机复制成功"
            INSTALLED=true
        fi
    fi
    
    # 方法2: 使用包管理器安装（如果方法1失败）
    if [ "$INSTALLED" = false ] && docker exec -u root "$CONTAINER_NAME" sh -c "command -v apt-get >/dev/null 2>&1"; then
        print_info "方法2: 使用包管理器安装 docker-compose-plugin..."
        if docker exec -u root "$CONTAINER_NAME" sh -c "apt-get update -qq && apt-get install -y docker-compose-plugin 2>/dev/null" && \
           docker exec "$CONTAINER_NAME" docker compose version >/dev/null 2>&1; then
            print_info "✅ 包管理器安装成功"
            INSTALLED=true
        fi
    fi
    
    # 如果所有方法都失败，给出建议
    if [ "$INSTALLED" = false ]; then
        print_error "❌ 自动安装失败"
        echo ""
        print_warn "建议的解决方案:"
        echo ""
        echo "  1. 如果宿主机已安装 docker-compose，手动复制:"
        echo "     docker cp \$(which docker-compose) ${CONTAINER_NAME}:/usr/local/bin/docker-compose"
        echo "     docker exec -u root ${CONTAINER_NAME} chmod +x /usr/local/bin/docker-compose"
        echo ""
        echo "  2. 检查 Docker 版本是否支持 docker compose (V2):"
        echo "     docker exec ${CONTAINER_NAME} docker compose version"
        echo "     如果支持，Jenkinsfile 会自动使用 'docker compose' 命令"
        echo ""
        echo "  3. 或者手动在宿主机下载后复制到容器:"
        echo "     # 在宿主机上下载（如果可以访问网络）"
        echo "     ARCH=\$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')"
        echo "     OS=\$(uname -s | tr '[:upper:]' '[:lower:]')"
        echo "     curl -L \"https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /tmp/docker-compose"
        echo "     chmod +x /tmp/docker-compose"
        echo "     docker cp /tmp/docker-compose ${CONTAINER_NAME}:/usr/local/bin/docker-compose"
        echo ""
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


