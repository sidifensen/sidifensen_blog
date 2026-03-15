#!/bin/bash

# Sidifensen Blog Jenkins 部署脚本
# 此脚本在服务器上执行，用于部署新版本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
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
PROJECT_ROOT="$(cd "$DEPLOY_DIR/../.." && pwd)"
DOCKER_COMPOSE_DIR="$(cd "$DEPLOY_DIR/.." && pwd)"

# 部署包文件名（从环境变量获取，Jenkins 会传递）
DEPLOY_PACKAGE="${1:-deploy.tar.gz}"

print_title "开始部署 Sidifensen Blog"
print_message "部署包: $DEPLOY_PACKAGE"

# 1. 检查部署包是否存在
if [ ! -f "$PROJECT_ROOT/$DEPLOY_PACKAGE" ]; then
    print_error "部署包不存在: $PROJECT_ROOT/$DEPLOY_PACKAGE"
    exit 1
fi

# 2. 解压部署包
print_message "解压部署包..."
cd "$PROJECT_ROOT"
tar -xzf "$DEPLOY_PACKAGE"

# 3. 移动文件到正确位置
print_message "整理部署文件..."

# 移动后端 jar 包
if [ -f "$PROJECT_ROOT/sidifensen_blog_backend-1.0-SNAPSHOT.jar" ]; then
    mkdir -p "$PROJECT_ROOT/sidifensen_blog_backend/target"
    mv "$PROJECT_ROOT/sidifensen_blog_backend-1.0-SNAPSHOT.jar" "$PROJECT_ROOT/sidifensen_blog_backend/target/"
    print_message "后端 jar 包已移动到正确位置"
fi

# 处理前端 dist 目录
if [ -d "$PROJECT_ROOT/dist" ]; then
    # 判断是哪个前端项目（根据文件特征）
    if [ -f "$PROJECT_ROOT/dist/index.html" ]; then
        # 检查是否有管理端特征文件
        if grep -q "admin" "$PROJECT_ROOT/dist/index.html" 2>/dev/null; then
            mkdir -p "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_admin"
            if [ -d "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_admin/dist" ]; then
                rm -rf "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_admin/dist"
            fi
            mv "$PROJECT_ROOT/dist" "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_admin/"
            print_message "管理端前端已移动到正确位置"
        else
            mkdir -p "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_user"
            if [ -d "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_user/dist" ]; then
                rm -rf "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_user/dist"
            fi
            mv "$PROJECT_ROOT/dist" "$PROJECT_ROOT/sidifensen_blog_frontend/sidifensen_user/"
            print_message "用户端前端已移动到正确位置"
        fi
    fi
fi

# 4. 停止旧容器
print_message "停止旧容器..."
cd "$DOCKER_COMPOSE_DIR"
docker-compose -f docker-compose.yml down || print_warning "停止容器时出现错误（可能容器未运行）"

# 5. 重新构建并启动
print_message "构建并启动新容器..."
docker-compose -f docker-compose.yml up -d --build

# 6. 等待服务启动
print_message "等待服务启动..."
sleep 15

# 7. 检查服务状态
print_message "检查服务状态..."
docker-compose -f docker-compose.yml ps

# 8. 健康检查
print_message "执行健康检查..."
sleep 5
if docker-compose -f docker-compose.yml ps | grep -q "Up"; then
    print_message "✅ 服务运行正常"
else
    print_warning "⚠️ 部分服务可能未正常启动，请检查日志"
fi

# 9. 清理部署包
print_message "清理部署包..."
rm -f "$PROJECT_ROOT/$DEPLOY_PACKAGE"

# 10. 显示访问信息
print_title "部署完成"
print_message "服务已成功部署并启动"
print_message "使用 'docker-compose logs -f' 查看日志"
print_message "使用 'docker-compose ps' 查看服务状态"

