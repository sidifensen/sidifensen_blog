#!/bin/bash

# Sidifensen Blog SSL Docker 启动脚本（针对 docker-compose-ssl.yml）

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

check_requirements() {
    print_title "检查环境要求"
    if ! command -v docker &>/dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    if ! command -v docker-compose &>/dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    docker_version=$(docker --version)
    compose_version=$(docker-compose --version)
    print_message "Docker 版本: ${docker_version}"
    print_message "Docker Compose 版本: ${compose_version}"
}

check_compose_file() {
    if [ ! -f "docker-compose-ssl.yml" ]; then
        print_error "未找到 docker-compose-ssl.yml，请在 script/ssl 目录下运行"
        exit 1
    fi
}

check_env_file() {
    print_title "检查环境配置"
    if [ ! -f ".env" ]; then
        if [ -f "env.example" ]; then
            print_warning "未找到 .env 文件，正在创建..."
            cp env.example .env
            print_message "已创建 .env 文件，请根据需要修改配置"
        else
            print_warning "未找到 .env 和 env.example，跳过环境文件创建"
        fi
    else
        print_message "找到 .env 文件"
    fi
}

start_stack() {
    print_title "启动 SSL 生产环境"
    print_message "正在构建并启动服务..."
    print_message "使用项目路径: ${PROJECT_ROOT}"
    PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml up -d --build
    print_message "等待服务启动..."
    sleep 8
    print_message "检查服务状态..."
    docker-compose -f docker-compose-ssl.yml ps
    show_access_info
}

stop_stack() {
    print_title "停止服务"
    PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml down
    print_message "所有服务已停止"
}

show_status() {
    print_title "服务状态"
    PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml ps
}

show_logs() {
    print_title "服务日志"
    echo "选择要查看的服务日志:"
    echo "1) 所有服务"
    echo "2) Nginx 网关"
    echo "3) 后端服务"
    echo "4) 管理端前端"
    echo "5) 用户端前端"
    echo "6) MySQL"
    echo "7) Redis"
    echo "8) MinIO"
    echo "9) RabbitMQ"
    read -p "请选择 (1-9): " log_choice
    case $log_choice in
        1) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 ;;
        2) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 nginx-gateway ;;
        3) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 backend ;;
        4) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 frontend-admin ;;
        5) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 frontend-user ;;
        6) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 mysql ;;
        7) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 redis ;;
        8) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 minio ;;
        9) PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml logs --tail=50 rabbitmq ;;
        *) print_error "无效选择" ;;
    esac
}

restart_stack() {
    print_title "重启服务"
    PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml restart
    print_message "服务重启完成"
}

clean_data() {
    print_title "清理数据 (危险操作)"
    print_warning "这将删除所有数据卷中的数据，包括数据库等！"
    read -p "确定要继续吗？(yes/no): " confirm
    if [ "$confirm" = "yes" ]; then
        print_message "停止并删除所有容器和数据卷..."
        PROJECT_ROOT="${PROJECT_ROOT}" docker-compose -f docker-compose-ssl.yml down -v
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

show_access_info() {
    print_title "服务访问信息"
    echo -e "${GREEN}✅ 用户站点(HTTPS):${NC}    https://sidifensen.com/"
    echo -e "${GREEN}✅ 管理后台(HTTPS):${NC}   https://admin.sidifensen.com/"
}

menu() {
    echo ""
    print_title "Sidifensen Blog SSL Docker 管理脚本"
    echo "1. 启动生产环境 (SSL 全量服务)"
    echo "2. 停止所有服务"
    echo "3. 查看服务状态"
    echo "4. 查看服务日志"
    echo "5. 重启服务"
    echo "6. 清理数据 (危险操作)"
    echo "0. 退出"
    echo ""
}

main() {
    print_message "脚本开始运行..."
    print_message "当前目录: $(pwd)"
    # 计算并导出项目根目录，供 docker-compose-ssl.yml 中的 ${PROJECT_ROOT} 使用
    export PROJECT_ROOT="$(cd "$(dirname "$0")"/../.. && pwd)"
    print_message "PROJECT_ROOT: ${PROJECT_ROOT}"
    check_requirements
    check_compose_file
    check_env_file
    while true; do
        menu
        read -p "请选择操作 (0-6): " choice
        case $choice in
            1) start_stack ;;
            2) stop_stack ;;
            3) show_status ;;
            4) show_logs ;;
            5) restart_stack ;;
            6) clean_data ;;
            0) print_message "再见！"; exit 0 ;;
            *) print_error "无效选择，请重新输入" ;;
        esac
        echo ""
        read -p "按 Enter 键继续..." _
    done
}

main "$@"


