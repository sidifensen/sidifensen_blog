#!/bin/bash

# Sidifensen Blog Docker 启动脚本

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

# 检查 Docker 和 Docker Compose
check_requirements() {
    print_title "检查环境要求"
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    docker_version=$(docker --version)
    compose_version=$(docker-compose --version)
    
    print_message "Docker 版本: ${docker_version}"
    print_message "Docker Compose 版本: ${compose_version}"
}

# 检查环境变量文件
check_env_file() {
    print_title "检查环境配置"
    
    if [ ! -f "../.env" ]; then
        print_warning "未找到 .env 文件，正在创建..."
        cp env.example ../.env
        print_message "已创建 .env 文件，请根据需要修改配置"
        print_warning "建议修改 .env 文件中的敏感信息（如密码、密钥等）"
    else
        print_message "找到 .env 文件"
    fi
}

# 启动生产环境
start_production() {
    print_title "启动生产环境"
    
    print_message "正在构建并启动服务..."
    docker-compose -f docker-compose.yml up -d --build
    
    print_message "等待服务启动..."
    sleep 10
    
    print_message "检查服务状态..."
    docker-compose -f docker-compose.yml ps
    
    show_access_info
}

# 启动开发环境
start_development() {
    print_title "启动开发环境"
    
    print_message "正在构建并启动服务..."
    docker-compose -f docker-compose-service.yml up -d --build
    
    print_message "等待服务启动..."
    sleep 10
    
    print_message "检查服务状态..."
    docker-compose -f docker-compose-service.yml ps
    
    print_message "开发环境基础服务已启动"
    print_message "四个基础服务：MySQL、Redis、MinIO、RabbitMQ"
    print_message "现在可以在 IDE 中启动后端应用，在终端中启动前端应用进行开发"
}

# 启动应用服务
start_apps_only() {
    print_title "启动应用服务 (后端+前端)"
    
    print_message "正在启动后端和前端服务..."
    docker-compose -f docker-compose.apps.yml up -d --build
    
    print_message "等待服务启动..."
    sleep 10
    
    print_message "检查服务状态..."
    docker-compose -f docker-compose.apps.yml ps
    
    show_access_info
}

# 停止服务
stop_services() {
    print_title "停止服务"
    
    if [ -f "docker-compose.yml" ]; then
        print_message "停止生产环境服务..."
        docker-compose down
    fi
    
    if [ -f "docker-compose-service.yml" ]; then
        print_message "停止开发环境服务..."
        docker-compose -f docker-compose-service.yml down
    fi
    
    print_message "所有服务已停止"
}

# 查看服务状态
show_status() {
    print_title "服务状态"
    print_message "正在执行 docker-compose ps..."
    docker-compose ps
    print_message "docker-compose ps 执行完成"
}

# 查看日志
show_logs() {
    print_title "服务日志"
    echo "选择要查看的服务日志:"
    echo "1) 所有服务"
    echo "2) 后端服务"
    echo "3) 管理端前端"
    echo "4) 用户端前端"
    echo "5) MySQL"
    echo "6) Redis"
    echo "7) MinIO"
    echo "8) RabbitMQ"
    
    read -p "请选择 (1-8): " log_choice
    
    case $log_choice in
        1) 
            print_message "正在查看所有服务日志..."
            docker-compose logs --tail=50
            print_message "日志查看完成"
            ;;
        2) docker-compose logs --tail=50 backend ;;
        3) docker-compose logs --tail=50 frontend-admin ;;
        4) docker-compose logs --tail=50 frontend-user ;;
        5) docker-compose logs --tail=50 mysql ;;
        6) docker-compose logs --tail=50 redis ;;
        7) docker-compose logs --tail=50 minio ;;
        8) docker-compose logs --tail=50 rabbitmq ;;
        *) print_error "无效选择" ;;
    esac
}

# 重启服务
restart_services() {
    print_title "重启服务"
    docker-compose restart
    print_message "服务重启完成"
}

# 清理数据
clean_data() {
    print_title "清理数据 (危险操作)"
    print_warning "这将删除所有数据卷中的数据，包括数据库数据！"
    read -p "确定要继续吗？(yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        print_message "停止并删除所有容器和数据卷..."
        docker-compose down -v
        
        print_message "删除命名数据卷..."
        docker volume rm sidifensen-mysql-data 2>/dev/null || true
        docker volume rm sidifensen-redis-data 2>/dev/null || true
        docker volume rm sidifensen-minio-data 2>/dev/null || true
        docker volume rm sidifensen-rabbitmq-data 2>/dev/null || true
        
        print_message "数据清理完成"
    else
        print_message "操作已取消"
    fi
}

# 显示访问信息
show_access_info() {
    print_title "服务访问信息"
    
    # 从环境变量文件读取端口配置
    backend_port=$(grep "^BACKEND_PORT=" .env 2>/dev/null | cut -d'=' -f2 || echo "5000")
    admin_port=$(grep "^ADMIN_PORT=" .env 2>/dev/null | cut -d'=' -f2 || echo "8000")
    user_port=$(grep "^USER_PORT=" .env 2>/dev/null | cut -d'=' -f2 || echo "7000")
    minio_console_port=$(grep "^MINIO_CONSOLE_PORT=" .env 2>/dev/null | cut -d'=' -f2 || echo "9001")
    rabbitmq_management_port=$(grep "^RABBITMQ_MANAGEMENT_PORT=" .env 2>/dev/null | cut -d'=' -f2 || echo "15672")
    
    echo -e "${GREEN}✅ 后端 API:${NC}        http://localhost:${backend_port}"
    echo -e "${GREEN}✅ 管理端前端:${NC}      http://localhost:${admin_port}"
    echo -e "${GREEN}✅ 用户端前端:${NC}      http://localhost:${user_port}"
    echo -e "${GREEN}✅ MinIO 控制台:${NC}    http://localhost:${minio_console_port} "
    echo -e "${GREEN}✅ RabbitMQ 管理:${NC}   http://localhost:${rabbitmq_management_port} "
    
    echo ""
    print_message "常用命令:"
    echo "  查看日志: docker-compose logs -f"
    echo "  停止服务: docker-compose down"
    echo "  重启服务: docker-compose restart"
}

# 无效选择
invalid_choice() {
    print_error "无效选择，请重新输入"
}

# 菜单继续
menu_continue() {
    echo ""
    read -p "按 Enter 键继续..."
}

# 主菜单
show_menu() {
    echo ""
    print_title "Sidifensen Blog Docker 管理脚本"
    echo "1. 启动生产环境 (完整服务)"
    echo "2. 启动开发环境 (仅基础服务)"
    echo "3. 启动应用服务 (后端+前端)"
    echo "4. 停止所有服务"
    echo "5. 查看服务状态"
    echo "6. 查看服务日志"
    echo "7. 重启服务"
    echo "8. 清理数据 (危险操作)"
    echo "0. 退出"
    echo ""
}

# 退出脚本
exit_script() {
    print_message "再见！"
    exit 0
}

# 主程序
main() {
    # 检查是否在 script 目录中
    if [ ! -f "docker-compose.yml" ]; then
        print_error "请在 script 目录中运行此脚本"
        exit 1
    fi
    
    print_message "脚本开始运行..."
    print_message "当前目录: $(pwd)"
    
    check_requirements
    check_env_file
    
    while true; do
        show_menu
        read -p "请选择操作 (0-8): " choice
        
        case $choice in
            1) start_production ;;
            2) start_development ;;
            3) start_apps_only ;;
            4) stop_services ;;
            5) show_status ;;
            6) show_logs ;;
            7) restart_services ;;
            8) clean_data ;;
            0) exit_script ;;
            *) invalid_choice ;;
        esac
        
        menu_continue
    done
}

# 运行主程序
main "$@"
