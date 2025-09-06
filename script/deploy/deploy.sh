#!/bin/bash

# Sidifensen Blog 服务器部署脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
PROJECT_DIR="/opt/sidifensen_blog"  # 项目部署目录
GIT_REPO="https://github.com/sidifensen/sidifensen_blog.git"  # Git 仓库地址
BRANCH="main"  # 部署分支
BACKUP_DIR="/opt/backups"  # 备份目录

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

# 检查是否为 root 用户
check_root() {
    if [ "$EUID" -ne 0 ]; then
        print_error "请使用 root 权限运行此脚本"
        exit 1
    fi
}

# 检测操作系统
detect_os() {
    if [ -f /etc/redhat-release ]; then
        OS="centos"
        print_message "检测到 CentOS/RHEL 系统"
    elif [ -f /etc/debian_version ]; then
        OS="ubuntu"
        print_message "检测到 Ubuntu/Debian 系统"
    else
        print_error "不支持的操作系统"
        exit 1
    fi
}

# 安装依赖
install_dependencies() {
    print_title "安装系统依赖"
    
    # 检测操作系统
    detect_os
    
    if [ "$OS" = "centos" ]; then
        # CentOS/RHEL 系统
        print_message "更新系统包..."
        yum update -y
        
        # 安装必要软件
        print_message "安装 Git, Docker, Docker Compose..."
        yum install -y git curl wget yum-utils device-mapper-persistent-data lvm2
    else
        # Ubuntu/Debian 系统
        print_message "更新系统包..."
        apt update -y
        
        # 安装必要软件
        print_message "安装 Git, Docker, Docker Compose..."
        apt install -y git curl wget
    fi
    
    # 安装 Docker
    if ! command -v docker &> /dev/null; then
        print_message "安装 Docker..."
        
        if [ "$OS" = "centos" ]; then
            # CentOS Docker 安装
            yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
            yum install -y docker-ce docker-ce-cli containerd.io
        else
            # Ubuntu/Debian Docker 安装
            curl -fsSL https://get.docker.com -o get-docker.sh
            sh get-docker.sh
            rm get-docker.sh
        fi
        
        # 启动 Docker 服务
        systemctl enable docker
        systemctl start docker
        
        # 将当前用户添加到 docker 组（如果不是 root）
        if [ "$USER" != "root" ] && [ -n "$USER" ]; then
            usermod -aG docker $USER
            print_warning "已将用户 $USER 添加到 docker 组，请重新登录或使用 newgrp docker"
        fi
    fi
    
    # 安装 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_message "安装 Docker Compose..."
        
        # 获取最新版本号
        COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d\" -f4)
        if [ -z "$COMPOSE_VERSION" ]; then
            COMPOSE_VERSION="v2.21.0"  # 备用版本
            print_warning "无法获取最新版本，使用备用版本 $COMPOSE_VERSION"
        fi
        
        curl -L "https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        chmod +x /usr/local/bin/docker-compose
        
        # 验证安装
        if docker-compose --version > /dev/null 2>&1; then
            print_message "Docker Compose 安装成功: $(docker-compose --version)"
        else
            print_error "Docker Compose 安装失败"
            exit 1
        fi
    fi
    
    # CentOS 特殊配置
    if [ "$OS" = "centos" ]; then
        print_message "配置 CentOS 特殊设置..."
        
        # 配置防火墙
        if systemctl is-active --quiet firewalld; then
            print_message "配置防火墙端口..."
            firewall-cmd --permanent --add-port=5000/tcp    # 后端 API
            firewall-cmd --permanent --add-port=8000/tcp    # 管理端前端
            firewall-cmd --permanent --add-port=7000/tcp    # 用户端前端
            firewall-cmd --permanent --add-port=3306/tcp    # MySQL
            firewall-cmd --permanent --add-port=6379/tcp    # Redis
            firewall-cmd --permanent --add-port=9000/tcp    # MinIO API
            firewall-cmd --permanent --add-port=9001/tcp    # MinIO Console
            firewall-cmd --permanent --add-port=5672/tcp    # RabbitMQ
            firewall-cmd --permanent --add-port=15672/tcp   # RabbitMQ Management
            firewall-cmd --reload
            print_message "防火墙配置完成"
        fi
        
        # 配置 SELinux (如果启用)
        if command -v getenforce &> /dev/null && [ "$(getenforce)" != "Disabled" ]; then
            print_warning "检测到 SELinux 已启用，建议设置为 Permissive 模式以避免 Docker 权限问题"
            print_warning "可以运行: setenforce 0 && sed -i 's/SELINUX=enforcing/SELINUX=permissive/' /etc/selinux/config"
        fi
    fi
}

# 创建备份
create_backup() {
    print_title "创建备份"
    
    if [ -d "$PROJECT_DIR" ]; then
        BACKUP_NAME="sidifensen_blog_$(date +%Y%m%d_%H%M%S)"
        mkdir -p "$BACKUP_DIR"
        
        print_message "备份当前版本到 $BACKUP_DIR/$BACKUP_NAME"
        cp -r "$PROJECT_DIR" "$BACKUP_DIR/$BACKUP_NAME"
        
        # 只保留最近5个备份
        cd "$BACKUP_DIR"
        ls -t | tail -n +6 | xargs -d '\n' rm -rf --
    fi
}

# 部署代码
deploy_code() {
    print_title "部署代码"
    
    if [ -d "$PROJECT_DIR" ]; then
        print_message "更新现有项目..."
        cd "$PROJECT_DIR"
        git fetch origin
        git reset --hard origin/$BRANCH
        git pull origin $BRANCH
    else
        print_message "克隆新项目..."
        git clone -b $BRANCH $GIT_REPO $PROJECT_DIR
        cd "$PROJECT_DIR"
    fi
    
    print_message "当前版本: $(git rev-parse --short HEAD)"
}

# 配置环境
setup_environment() {
    print_title "配置环境"
    
    cd "$PROJECT_DIR"
    
    # 复制环境变量文件
    if [ ! -f ".env" ]; then
        print_message "创建环境变量文件..."
        cp script/env.example .env
        print_warning "请编辑 .env 文件设置正确的配置"
    fi
    
    # 设置文件权限
    chmod +x script/start.sh
    chmod 600 .env
}

# 构建和启动服务
start_services() {
    print_title "构建和启动服务"
    
    cd "$PROJECT_DIR/script"
    
    # 停止旧服务
    print_message "停止旧服务..."
    docker-compose down || true
    
    # 清理旧镜像
    print_message "清理旧镜像..."
    docker system prune -f
    
    # 构建并启动新服务
    print_message "构建并启动服务..."
    docker-compose up -d --build
    
    # 等待服务启动
    print_message "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    print_message "检查服务状态..."
    docker-compose ps
}

# 健康检查
health_check() {
    print_title "健康检查"
    
    # 检查后端服务
    if curl -f http://localhost:5000/actuator/health > /dev/null 2>&1; then
        print_message "后端服务正常"
    else
        print_warning "后端服务可能未就绪"
    fi
    
    # 检查前端服务
    if curl -f http://localhost:8000 > /dev/null 2>&1; then
        print_message "管理端前端正常"
    else
        print_warning "管理端前端可能未就绪"
    fi
    
    if curl -f http://localhost:7000 > /dev/null 2>&1; then
        print_message "用户端前端正常"
    else
        print_warning "用户端前端可能未就绪"
    fi
}

# 显示部署信息
show_deploy_info() {
    print_title "部署完成"
    
    echo -e "${GREEN}✅ 服务访问地址:${NC}"
    echo "  后端 API:     http://your-server-ip:5000"
    echo "  管理端前端:   http://your-server-ip:8000"
    echo "  用户端前端:   http://your-server-ip:7000"
    echo "  MinIO 控制台: http://your-server-ip:9001"
    echo "  RabbitMQ 管理: http://your-server-ip:15672"
    
    echo ""
    echo -e "${GREEN}✅ 常用命令:${NC}"
    echo "  查看服务状态: cd $PROJECT_DIR/script && docker-compose ps"
    echo "  查看日志:     cd $PROJECT_DIR/script && docker-compose logs -f"
    echo "  重启服务:     cd $PROJECT_DIR/script && docker-compose restart"
    echo "  停止服务:     cd $PROJECT_DIR/script && docker-compose down"
}

# 主函数
main() {
    print_title "Sidifensen Blog 自动部署脚本"
    
    # 检查权限
    check_root
    
    # 安装依赖
    install_dependencies
    
    # 创建备份
    create_backup
    
    # 部署代码
    deploy_code
    
    # 配置环境
    setup_environment
    
    # 启动服务
    start_services
    
    # 健康检查
    health_check
    
    # 显示部署信息
    show_deploy_info
    
    print_message "部署完成！"
}

# 运行主函数
main "$@"
