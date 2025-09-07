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
    
    print_message "Docker 版本: $(docker --version)"
    print_message "Docker Compose 版本: $(docker-compose --version)"
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

# 启动服务
start_services() {
    local compose_file=$1
    local service_name=$2
    
    print_title "启动 $service_name"
    
    print_message "正在构建并启动服务..."
    docker-compose -f $compose_file up -d --build
    
    print_message "等待服务启动..."
    sleep 10
    
    print_message "检查服务状态..."
    docker-compose -f $compose_file ps
}

# 显示访问信息
show_access_info() {
    print_title "服务访问信息"
    
    # 从环境变量文件读取端口配置
    local backend_port=$(grep "^BACKEND_PORT=" ../.env 2>/dev/null | cut -d'=' -f2 || echo "5000")
    local admin_port=$(grep "^ADMIN_PORT=" ../.env 2>/dev/null | cut -d'=' -f2 || echo "8000")
    local user_port=$(grep "^USER_PORT=" ../.env 2>/dev/null | cut -d'=' -f2 || echo "7000")
    local minio_console_port=$(grep "^MINIO_CONSOLE_PORT=" ../.env 2>/dev/null | cut -d'=' -f2 || echo "9001")
    local rabbitmq_management_port=$(grep "^RABBITMQ_MANAGEMENT_PORT=" ../.env 2>/dev/null | cut -d'=' -f2 || echo "15672")
    
    echo -e "${GREEN}✅ 后端 API:${NC}        http://localhost:${backend_port}"
    echo -e "${GREEN}✅ 管理端前端:${NC}      http://localhost:${admin_port}"
    echo -e "${GREEN}✅ 用户端前端:${NC}      http://localhost:${user_port}"
    echo -e "${GREEN}✅ MinIO 控制台:${NC}    http://localhost:${minio_console_port} (minioadmin/minioadmin123)"
    echo -e "${GREEN}✅ RabbitMQ 管理:${NC}   http://localhost:${rabbitmq_management_port} (admin/admin123)"
    
    echo ""
    print_message "常用命令:"
    echo "  查看日志: docker-compose logs -f"
    echo "  停止服务: docker-compose down"
    echo "  重启服务: docker-compose restart"
}

# 主菜单
show_menu() {
    echo ""
    print_title "Sidifensen Blog Docker 管理脚本"
    echo "1. 启动生产环境 (完整服务)"
    echo "2. 启动开发环境 (仅基础服务)"
    echo "3. 停止所有服务"
    echo "4. 查看服务状态"
    echo "5. 查看服务日志"
    echo "6. 重启服务"
    echo "7. 清理数据 (危险操作)"
    echo "0. 退出"
    echo ""
}

# 停止服务
stop_services() {
    print_title "停止服务"
    
    if [ -f "docker-compose.yml" ]; then
        print_message "停止生产环境服务..."
        docker-compose down
    fi
    
    if [ -f "docker-compose.dev.yml" ]; then
        print_message "停止开发环境服务..."
        docker-compose -f docker-compose.dev.yml down
    fi
    
    print_message "所有服务已停止"
}

# 查看服务状态
show_status() {
    print_title "服务状态"
    docker-compose ps
}

# 查看日志
show_logs() {
    print_title "服务日志"
    echo "选择要查看的服务日志:"
    echo "1. 所有服务"
    echo "2. 后端服务"
    echo "3. 管理端前端"
    echo "4. 用户端前端"
    echo "5. MySQL"
    echo "6. Redis"
    echo "7. MinIO"
    echo "8. RabbitMQ"
    
    read -p "请选择 (1-8): " log_choice
    
    case $log_choice in
        1) docker-compose logs -f ;;
        2) docker-compose logs -f backend ;;
        3) docker-compose logs -f frontend-admin ;;
        4) docker-compose logs -f frontend-user ;;
        5) docker-compose logs -f mysql ;;
        6) docker-compose logs -f redis ;;
        7) docker-compose logs -f minio ;;
        8) docker-compose logs -f rabbitmq ;;
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
        docker-compose -f docker-compose.dev.yml down -v 2>/dev/null || true
        print_message "数据清理完成"
    else
        print_message "操作已取消"
    fi
}

# 主程序
main() {
    # 检查是否在 script 目录中
    if [ ! -f "docker-compose.yml" ]; then
        print_error "请在 script 目录中运行此脚本"
        exit 1
    fi
    
    check_requirements
    
    while true; do
        show_menu
        read -p "请选择操作 (0-7): " choice
        
        case $choice in
            1)
                check_env_file
                start_services "docker-compose.yml" "生产环境"
                show_access_info
                ;;
            2)
                check_env_file
                start_services "docker-compose.dev.yml" "开发环境"
                print_message "开发环境基础服务已启动，可以手动启动后端和前端服务进行开发"
                ;;
            3)
                stop_services
                ;;
            4)
                show_status
                ;;
            5)
                show_logs
                ;;
            6)
                restart_services
                ;;
            7)
                clean_data
                ;;
            0)
                print_message "再见！"
                exit 0
                ;;
            *)
                print_error "无效选择，请重新输入"
                ;;
        esac
        
        echo ""
        read -p "按 Enter 键继续..."
    done
}

# 运行主程序
main "$@"
