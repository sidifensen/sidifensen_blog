#!/bin/bash

# Gitea 初始化脚本
# 用于配置 Gitea 和创建初始仓库

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_title() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="$(cd "$SCRIPT_DIR" && pwd)"

print_title "Gitea 初始化脚本"

# 检查 .env 文件
ENV_FILE="$DEPLOY_DIR/.env"
if [ ! -f "$ENV_FILE" ]; then
    print_warning "未找到 .env 文件，正在创建..."
    if [ -f "$DEPLOY_DIR/env.example" ]; then
        cp "$DEPLOY_DIR/env.example" "$ENV_FILE"
        print_message "已从 env.example 创建 .env 文件"
    else
        print_error "未找到 env.example 文件，请先创建环境变量配置文件"
        exit 1
    fi
fi

# 修复 Windows 行尾符问题（CRLF -> LF）
if command -v dos2unix >/dev/null 2>&1; then
    dos2unix "$ENV_FILE" >/dev/null 2>&1
elif command -v sed >/dev/null 2>&1; then
    sed -i 's/\r$//' "$ENV_FILE"
else
    # 使用 tr 命令移除 \r 字符
    tr -d '\r' < "$ENV_FILE" > "$ENV_FILE.tmp" && mv "$ENV_FILE.tmp" "$ENV_FILE"
fi

# 加载环境变量
source "$ENV_FILE"

# 检查 Docker 是否安装
if ! command -v docker >/dev/null 2>&1; then
    print_error "Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 docker-compose 是否可用
DOCKER_COMPOSE_CMD="docker-compose"
if ! command -v docker-compose >/dev/null 2>&1; then
    # 尝试使用 docker compose (Docker Compose V2)
    if docker compose version >/dev/null 2>&1; then
        DOCKER_COMPOSE_CMD="docker compose"
    else
        print_error "未找到 docker-compose 或 docker compose 命令"
        exit 1
    fi
fi

# 检查 docker-compose 文件是否存在
COMPOSE_FILE="$DEPLOY_DIR/docker-compose-gitea.yml"
if [ ! -f "$COMPOSE_FILE" ]; then
    print_error "未找到 docker-compose-gitea.yml 文件"
    exit 1
fi

# 检查 MySQL 连接（如果配置了 MySQL）
DB_TYPE="${GITEA_DB_TYPE:-sqlite3}"
if [ "$DB_TYPE" = "mysql" ]; then
    if [ -z "$GITEA_DB_HOST" ] || [ -z "$GITEA_DB_USER" ] || [ -z "$GITEA_DB_PASSWORD" ]; then
        print_error "MySQL 配置不完整，请检查 .env 文件"
        print_error "需要配置: GITEA_DB_HOST, GITEA_DB_USER, GITEA_DB_PASSWORD"
        exit 1
    fi
    
    print_message "检查 MySQL 数据库连接..."
    
    # 如果 MySQL 在 Docker 网络中（使用容器名），检查容器是否运行
    if [ "$GITEA_DB_HOST" = "sidifensen-mysql" ] || docker ps | grep -q "$GITEA_DB_HOST"; then
        if ! docker ps | grep -q "$GITEA_DB_HOST"; then
            print_warning "MySQL 容器 ($GITEA_DB_HOST) 未运行"
            print_warning "请确保 MySQL 服务已启动，Gitea 需要连接到 MySQL 数据库"
            echo ""
            # 只在交互式终端中询问
            if [ -t 0 ]; then
                read -p "是否继续启动 Gitea？（y/n）" -n 1 -r
                echo ""
                if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                    print_error "已取消启动"
                    exit 1
                fi
            else
                print_warning "非交互式模式：将继续启动 Gitea，请确保 MySQL 服务可用"
            fi
        else
            print_message "MySQL 容器 ($GITEA_DB_HOST) 正在运行"
        fi
    else
        print_warning "MySQL 主机: $GITEA_DB_HOST"
        print_warning "请确保 MySQL 服务可访问，Gitea 需要连接到 MySQL 数据库"
        echo ""
    fi
    
    # 检查数据库和用户（如果 MySQL 在 Docker 中）
    if [ "$GITEA_DB_HOST" = "sidifensen-mysql" ] && docker ps | grep -q "$GITEA_DB_HOST"; then
        DB_NAME="${GITEA_DB_NAME:-gitea}"
        DB_USER="${GITEA_DB_USER:-gitea}"
        
        print_message "检查数据库 '$DB_NAME' 和用户 '$DB_USER'..."
        
        # 检查是否使用 root 用户（安全警告）
        if [ "$DB_USER" = "root" ]; then
            print_warning "⚠️  安全警告：检测到使用 root 用户连接数据库！"
            print_warning "   建议创建专门的 gitea 用户以提高安全性"
            print_warning "   创建用户脚本：create-gitea-user.sql"
            echo ""
        fi
        
        # 尝试连接并检查数据库
        if docker exec "$GITEA_DB_HOST" mysql -u"$DB_USER" -p"$GITEA_DB_PASSWORD" -e "USE $DB_NAME;" >/dev/null 2>&1; then
            print_message "数据库 '$DB_NAME' 和用户 '$DB_USER' 配置正确"
        else
            print_warning "数据库 '$DB_NAME' 不存在或用户 '$DB_USER' 无法连接"
            if [ "$DB_USER" != "root" ]; then
                print_warning "请确保："
                echo "  1. 用户 '$DB_USER' 已创建"
                echo "  2. 用户 '$DB_USER' 对数据库 '$DB_NAME' 有权限"
                echo "  3. 可以使用脚本创建：docker exec -i $GITEA_DB_HOST mysql -uroot -p < create-gitea-user.sql"
            else
                print_message "Gitea 首次启动时会自动创建数据库（如果用户有权限）"
            fi
            echo ""
        fi
    fi
    echo ""
fi

# 检查是否需要重启容器以应用新配置
NEED_RESTART=false
if docker ps | grep -q sidifensen-gitea; then
    print_warning "Gitea 容器正在运行"
    print_warning "如果这是首次配置或修改了 .env 文件，建议重启容器以应用新配置"
    echo ""
    if [ -t 0 ]; then
        read -p "是否重启容器以应用新配置？（y/n，默认 n）" -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            NEED_RESTART=true
        fi
    fi
fi

# 检查 Gitea 容器是否运行
if ! docker ps | grep -q sidifensen-gitea; then
    print_warning "Gitea 容器未运行，正在启动..."
    
    # 检查容器是否存在但已停止
    if docker ps -a | grep -q sidifensen-gitea; then
        print_message "发现已停止的容器，正在启动..."
        cd "$DEPLOY_DIR"
        $DOCKER_COMPOSE_CMD -f docker-compose-gitea.yml --env-file .env up -d
    else
        print_message "正在创建并启动 Gitea 容器..."
        cd "$DEPLOY_DIR"
        $DOCKER_COMPOSE_CMD -f docker-compose-gitea.yml --env-file .env up -d
    fi
    
    if [ $? -ne 0 ]; then
        print_error "启动 Gitea 容器失败"
        exit 1
    fi
    
    print_message "Gitea 容器启动成功，等待服务就绪..."
    
    # 等待容器启动
    max_attempts=30
    attempt=0
    while [ $attempt -lt $max_attempts ]; do
        if docker ps | grep -q sidifensen-gitea; then
            print_message "容器已启动，等待服务就绪..."
            sleep 5
            break
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    
    if [ $attempt -eq $max_attempts ]; then
        print_error "等待容器启动超时"
        exit 1
    fi
else
    if [ "$NEED_RESTART" = true ]; then
        print_message "正在重启 Gitea 容器以应用新配置..."
        cd "$DEPLOY_DIR"
        $DOCKER_COMPOSE_CMD -f docker-compose-gitea.yml --env-file .env restart
        print_message "等待容器重启完成..."
        sleep 10
    else
        print_message "Gitea 容器已在运行"
    fi
fi

# 等待 Gitea 服务完全启动
print_message "等待 Gitea 服务启动完成..."
sleep 10

# 检查服务健康状态
print_message "检查 Gitea 服务健康状态..."
health_check_attempts=12
health_check_attempt=0
GITEA_URL="http://localhost:${GITEA_PORT:-3000}"

# 优先使用 curl，其次使用 wget，最后使用 docker exec
HEALTH_CHECK_CMD=""
if command -v curl >/dev/null 2>&1; then
    HEALTH_CHECK_CMD="curl -sf ${GITEA_URL} >/dev/null 2>&1"
elif command -v wget >/dev/null 2>&1; then
    HEALTH_CHECK_CMD="wget --quiet --tries=1 --spider ${GITEA_URL} >/dev/null 2>&1"
else
    # 使用 docker exec 检查容器内服务
    HEALTH_CHECK_CMD="docker exec sidifensen-gitea wget --quiet --tries=1 --spider http://localhost:3000 >/dev/null 2>&1"
fi

while [ $health_check_attempt -lt $health_check_attempts ]; do
    if eval "$HEALTH_CHECK_CMD"; then
        print_message "Gitea 服务已就绪"
        break
    fi
    sleep 5
    health_check_attempt=$((health_check_attempt + 1))
    if [ $health_check_attempt -lt $health_check_attempts ]; then
        echo -n "."
    fi
done

if [ $health_check_attempt -eq $health_check_attempts ]; then
    print_warning "Gitea 服务健康检查超时，但容器已启动，请手动检查服务状态"
    print_warning "访问地址: ${GITEA_URL}"
else
    echo ""
fi

# 获取 Gitea 配置
GITEA_PORT="${GITEA_PORT:-3000}"
GITEA_DOMAIN="${GITEA_DOMAIN:-localhost}"

# 检查数据库配置
DB_TYPE="${GITEA_DB_TYPE:-sqlite3}"
if [ "$DB_TYPE" = "mysql" ]; then
    print_message "检测到 MySQL 数据库配置"
    echo -e "  数据库主机: ${GITEA_DB_HOST:-未设置}"
    echo -e "  数据库名称: ${GITEA_DB_NAME:-gitea}"
    echo -e "  数据库用户: ${GITEA_DB_USER:-未设置}"
    echo ""
    if [ -z "$GITEA_DB_HOST" ] || [ -z "$GITEA_DB_USER" ] || [ -z "$GITEA_DB_PASSWORD" ]; then
        print_warning "MySQL 配置不完整，请检查 .env 文件中的以下配置："
        echo "  - GITEA_DB_HOST"
        echo "  - GITEA_DB_USER"
        echo "  - GITEA_DB_PASSWORD"
        echo ""
        print_warning "如果 MySQL 在 Docker 网络中，GITEA_DB_HOST 应设置为容器名（如：sidifensen-mysql）"
        echo ""
    fi
else
    print_message "使用 SQLite3 数据库（默认）"
    echo ""
fi

print_title "Gitea 访问信息"
echo -e "${GREEN}Gitea 地址:${NC} http://${GITEA_DOMAIN}:${GITEA_PORT}"
echo ""
print_message "请按照以下步骤完成初始化："
echo ""
echo "1. 打开浏览器访问: http://${GITEA_DOMAIN}:${GITEA_PORT}"
echo "2. 点击 '立即安装' 进行初始配置"
echo ""
echo -e "${GREEN}✅ 所有配置已通过环境变量自动设置！${NC}"
echo ""
echo "配置详情："
echo "  📊 数据库类型: MySQL（已强制使用，不使用 SQLite）"
echo "  📊 数据库主机: ${GITEA_DB_HOST:-sidifensen-mysql}"
echo "  📊 数据库名称: ${GITEA_DB_NAME:-gitea}"
echo "  📊 数据库用户: ${GITEA_DB_USER:-sidifensen}"
echo "  📊 数据库端口: ${GITEA_DB_PORT:-3306}"
echo "  🌐 站点名称: ${GITEA_APP_NAME:-斯蒂芬森}"
echo "  📁 仓库路径: ${GITEA_REPOSITORY_ROOT:-/data/git/repositories}"
echo "  🔐 SSH 端口: ${GITEA_SSH_PORT:-2222}"
echo "  🌐 HTTP 端口: ${GITEA_PORT:-3000}"
echo "  🔗 基础 URL: http://${GITEA_DOMAIN:-localhost}:${GITEA_PORT:-3000}/"
echo ""
echo "3. 在初始化页面上，所有配置应该已经自动填充"
echo "   如果已自动填充，直接确认即可，无需修改"
echo "4. 填写管理员账户信息（用户名、密码、邮箱等）"
echo "5. 点击 '立即安装' 完成安装"
echo "6. 完成安装后，创建新仓库"
echo ""
print_warning "注意：如果 Gitea 运行在服务器上，请将 ${GITEA_DOMAIN} 替换为服务器 IP 地址"
echo ""

print_title "创建仓库步骤"
echo "1. 登录 Gitea"
echo "2. 点击右上角 '+' → '新建仓库'"
echo "3. 仓库名称: sidifensen_blog"
echo "4. 选择 '私有'"
echo "5. 点击 '创建仓库'"
echo ""

print_title "配置 Webhook 步骤"
echo "1. 进入仓库设置 → Webhooks"
echo "2. 点击 '添加 Webhook' → 'Gitea'"
echo "3. 目标 URL: http://jenkins-server:8080/gitea-webhook/post"
echo "4. HTTP 方法: POST"
echo "5. 触发事件: 选择 '推送事件'"
echo "6. 点击 '添加 Webhook'"
echo ""

print_message "初始化脚本完成！"

