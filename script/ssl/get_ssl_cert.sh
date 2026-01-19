#!/bin/bash

# Sidifensen Blog SSL 证书获取脚本
# 基于 Let's Encrypt 和 Certbot
# 支持多域名证书获取和自动续约

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

# 检查必要的依赖
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

# 检查当前目录
check_current_directory() {
    if [ ! -f "docker-compose-ssl.yml" ]; then
        print_error "请在 script/ssl 目录下运行此脚本"
        exit 1
    fi
}

# 获取用户输入的域名和邮箱
get_user_input() {
    print_title "证书配置"
    
    # 使用默认邮箱
    EMAIL="1848221808@qq.com"
    print_message "使用默认邮箱: $EMAIL"
    
    # 使用默认主域名
    MAIN_DOMAIN="sidifensen.com"
    print_message "使用默认主域名: $MAIN_DOMAIN"
    
    # 默认子域名列表
    DEFAULT_SUBDOMAINS=("www" "admin" "api" "image")
    
    # 自动使用默认子域名列表
    SUBDOMAINS=("${DEFAULT_SUBDOMAINS[@]}")
    print_message "使用默认子域名列表: ${DEFAULT_SUBDOMAINS[*]}"
    
    # 构建完整的域名列表
    DOMAINS=("$MAIN_DOMAIN")
    for subdomain in "${SUBDOMAINS[@]}"; do
        if [ -n "$subdomain" ]; then
            DOMAINS+=("$subdomain.$MAIN_DOMAIN")
        fi
    done
    
    print_message "完整域名列表: ${DOMAINS[*]}"
    
    # 选择证书类型
    print_message "请选择 SSL 证书类型:"
    print_message "1. Let's Encrypt (免费，自动续约)"
    print_message "2. 自定义证书 (使用自己的SSL证书)"
    
    # 获取用户选择
    read -p "请选择 (1-2): " CERT_TYPE_CHOICE
    
    # 根据用户选择设置证书类型
    case "$CERT_TYPE_CHOICE" in
        1)
            CERT_TYPE="letsencrypt"
            print_message "选择: Let's Encrypt 证书"
            ;;
        2)
            CERT_TYPE="custom"
            print_message "选择: 自定义证书"
            ;;
        *)
            # 默认选择 Let's Encrypt
            CERT_TYPE="letsencrypt"
            print_message "未选择，使用默认: Let's Encrypt 证书"
            ;;
    esac
    
    # 如果选择自定义证书，获取证书文件路径
    if [ "$CERT_TYPE" = "custom" ]; then
        read -p "请输入 SSL 证书文件路径 (fullchain.pem): " CUSTOM_CERT_FILE
        if [ ! -f "$CUSTOM_CERT_FILE" ]; then
            print_error "证书文件不存在: $CUSTOM_CERT_FILE"
            exit 1
        fi
        
        read -p "请输入 SSL 私钥文件路径 (privkey.pem): " CUSTOM_KEY_FILE
        if [ ! -f "$CUSTOM_KEY_FILE" ]; then
            print_error "私钥文件不存在: $CUSTOM_KEY_FILE"
            exit 1
        fi
    else
        # 选择验证方式
        print_message "请选择 SSL 证书验证方式:"
        print_message "1. HTTP-01 (需要 80 端口，简单易用)"
        print_message "2. DNS-01 (不需要开放端口，适合防火墙环境)"
        
        # 获取用户选择
        read -p "请选择 (1-2): " VALIDATION_CHOICE
        
        # 根据用户选择设置验证方式
        case "$VALIDATION_CHOICE" in
            1)
                VALIDATION_METHOD="http-01"
                print_message "选择: HTTP-01 验证"
                ;;
            2)
                VALIDATION_METHOD="dns-01"
                print_message "选择: DNS-01 验证"
                ;;
            *)
                # 默认选择 HTTP-01 验证
                VALIDATION_METHOD="http-01"
                print_message "未选择，使用默认: HTTP-01 验证"
                ;;
        esac
    fi
    
    # 自动确认配置
    print_message "配置已自动确认"
}

# 创建必要的目录
create_directories() {
    print_title "创建目录结构"
    mkdir -p ./certbot-webroot
    mkdir -p ./nginx-conf
    mkdir -p /etc/nginx/ssl
    print_message "目录创建完成"
}

# 生成临时 Nginx 配置用于证书验证
generate_temp_nginx_config() {
    print_title "生成临时 Nginx 配置"
    
    cat > ./nginx-conf/temp-ssl.conf <<EOF
server {
    listen 80;
    server_name ${DOMAINS[*]};
    
    location ^~ /.well-known/acme-challenge/ {
        allow all;
        root /var/www/certbot;
        default_type "text/plain";
        try_files \$uri =404;
    }
    
    location / {
        return 301 https://\$host\$request_uri;
    }
}
EOF
    
    print_message "临时 Nginx 配置生成完成"
    cat ./nginx-conf/temp-ssl.conf
}

# 检查端口是否被占用
check_port() {
    local port=$1
    # 检查端口是否被占用
    if docker ps --filter publish=$port -q | grep -q .; then
        print_error "端口 $port 已被其他 Docker 容器占用"
        print_error "占用该端口的容器: $(docker ps --filter publish=$port --format "{{.Names}}")"
        return 1
    fi
    
    # 在 Linux/macOS 上检查系统端口
    if command -v lsof >/dev/null 2>&1; then
        if lsof -i :$port | grep -q LISTEN; then
            print_error "端口 $port 已被系统服务占用"
            print_error "占用该端口的进程: $(lsof -i :$port | grep LISTEN)"
            return 1
        fi
    # 在 Windows 上检查系统端口
    elif command -v netstat >/dev/null 2>&1; then
        if netstat -an | findstr :$port | findstr LISTENING; then
            print_error "端口 $port 已被系统服务占用"
            return 1
        fi
    fi
    
    return 0
}

# 启动临时 Nginx 容器用于证书验证
start_temp_nginx() {
    print_title "启动临时 Nginx 容器"
    
    # 检查并清理已存在的临时容器
    CONTAINER_NAME="sidifensen-nginx-temp"
    
    # 使用更精确的方式检查容器是否存在
    while docker ps -a --filter name="^/$CONTAINER_NAME$" -q | grep -q .; do
        print_message "清理已存在的临时 Nginx 容器..."
        docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
        docker rm "$CONTAINER_NAME" >/dev/null 2>&1 || true
        # 短暂等待确保容器被完全删除
        sleep 1
    done
    
    # 检查 80 端口是否被占用
    if ! check_port 80; then
        print_error "无法启动临时 Nginx 容器，端口 80 已被占用"
        print_error "请先停止占用 80 端口的服务或容器，然后重新运行脚本"
        exit 1
    fi
    
    docker run -d \
        --name "$CONTAINER_NAME" \
        --network sidifensen-blog-network \
        -p 80:80 \
        -v "$(pwd)/certbot-webroot:/var/www/certbot" \
        -v "$(pwd)/nginx-conf/temp-ssl.conf:/etc/nginx/conf.d/default.conf" \
        nginx:alpine
    
    print_message "等待临时 Nginx 启动..."
    sleep 5
    
    if docker ps --filter name="^/$CONTAINER_NAME$" -q | grep -q .; then
        print_message "临时 Nginx 容器启动成功"
    else
        print_error "临时 Nginx 容器启动失败"
        exit 1
    fi
}

# 使用 Certbot 获取 SSL 证书
get_ssl_certificates() {
    print_title "获取 SSL 证书"
    
    # 构建域名参数
    DOMAIN_ARGS=""
    for domain in "${DOMAINS[@]}"; do
        DOMAIN_ARGS+=" -d $domain"
    done
    
    print_message "正在请求 Let's Encrypt 证书..."
    print_message "域名: ${DOMAINS[*]}"
    print_message "邮箱: $EMAIL"
    print_message "验证方式: $VALIDATION_METHOD"
    
    if [ "$VALIDATION_METHOD" = "http-01" ]; then
        # 使用 HTTP-01 验证（需要 80 端口）
        docker run --rm -it \
            -v "$(pwd)/certbot-webroot:/var/www/certbot" \
            -v "/etc/letsencrypt:/etc/letsencrypt" \
            -v "/var/lib/letsencrypt:/var/lib/letsencrypt" \
            certbot/certbot \
            certonly \
            --webroot \
            --webroot-path=/var/www/certbot \
            --email "$EMAIL" \
            --agree-tos \
            --non-interactive \
            --rsa-key-size 2048 \
            --force-renewal \
            $DOMAIN_ARGS
    else
        # 添加 DNS 记录检查功能
        check_dns_records() {
            local domain="$1"
            local attempts=0
            local max_attempts=3
            local record_exists=false
            
            print_message "正在检查 DNS 记录 _acme-challenge.$domain..."
            
            while [ $attempts -lt $max_attempts ]; do
                # 使用 dig 命令检查 TXT 记录
                local txt_record
                txt_record=$(docker run --rm tutum/dnsutils dig -t TXT _acme-challenge.$domain +short 2>/dev/null)
                
                if [ -n "$txt_record" ]; then
                    print_message "✓ DNS 记录已生效: _acme-challenge.$domain"
                    print_message "   记录值: $txt_record"
                    return 0
                else
                    attempts=$((attempts + 1))
                    if [ $attempts -eq $max_attempts ]; then
                        print_warning "⚠ DNS 记录可能尚未生效: _acme-challenge.$domain"
                        print_warning "   请确保已正确添加 TXT 记录，并等待 DNS 传播完成"
                        return 1
                    fi
                    print_message "⏳ 等待 5 秒后重试..."
                    sleep 5
                fi
            done
            
            return 1
        }
        
        # 使用 DNS-01 验证（不需要开放端口）
        print_message "DNS-01 验证需要您在域名的 DNS 记录中添加 TXT 记录"
        print_message ""
        print_message "📋 您需要为以下所有域名添加 TXT 记录:"
        print_message "   - _acme-challenge.${MAIN_DOMAIN}"
        for subdomain in "${SUBDOMAINS[@]}"; do
            if [ -n "$subdomain" ]; then
                print_message "   - _acme-challenge.${subdomain}.${MAIN_DOMAIN}"
            fi
        done
        print_message ""
        print_message "请按照以下步骤操作:"
        print_message "1. 登录您的域名注册商管理界面（如阿里云、腾讯云等）"
        print_message "2. 找到域名的 DNS 管理页面"
        print_message "3. 运行 Certbot 命令后，会显示每个域名对应的 TXT 记录值"
        print_message "4. 为每个域名的 _acme-challenge 子域名添加对应的 TXT 记录和值"
        print_message "   - 记录名称格式：_acme-challenge.${MAIN_DOMAIN}" 
        print_message "   - 记录类型：TXT"
        print_message "   - 记录值：(Certbot 会显示随机字符串)"
        print_message "5. 等待 DNS 记录生效（通常需要 5-15 分钟，有时更长）"
        print_message "6. 可以使用以下命令检查记录是否生效："
        for domain in "${DOMAINS[@]}"; do
            print_message "   nslookup -type=TXT _acme-challenge.${domain}"
        done
        print_message "   或使用在线工具：https://mxtoolbox.com/TXTLookup.aspx"
        print_message ""
        print_message "💡 提示：Certbot 会在显示记录后暂停，等待您添加完记录后按 Enter 继续"
        print_message ""
        
        # 提示用户添加TXT记录
        print_message "🔔 Certbot 将显示需要添加的 TXT 记录信息"
        print_message "   请仔细记录每个域名的 _acme-challenge 子域名和对应的记录值"
        print_message ""
        
        # 使用 DNS-01 验证
        docker run --rm -it \
            -v "/etc/letsencrypt:/etc/letsencrypt" \
            -v "/var/lib/letsencrypt:/var/lib/letsencrypt" \
            certbot/certbot \
            certonly \
            --manual \
            --preferred-challenges dns \
            --email "$EMAIL" \
            --agree-tos \
            --rsa-key-size 2048 \
            --force-renewal \
            --manual-public-ip-logging-ok \
            $DOMAIN_ARGS
        
        # 检查Certbot执行结果
        local certbot_result=$?
        
        # 自动等待DNS记录生效（如果验证失败）
        if [ $certbot_result -ne 0 ]; then
            print_message ""
            print_message "⏳ 检测到验证失败，正在为您自动等待 DNS 记录生效..."
            print_message "   将在 60 秒后再次尝试检查所有 DNS 记录"
            sleep 60
            
            print_message ""
            print_message "🔍 再次检查所有 DNS 记录的状态："
            for domain in "${DOMAINS[@]}"; do
                check_dns_records "$domain"
            done
            
            print_message ""
            print_message "💡 提示：如果记录已经正确添加但仍验证失败，建议："
            print_message "   - 等待更长时间（至少 15 分钟）让 DNS 完全传播"
            print_message "   - 确保使用正确的 DNS 服务器（尝试使用 8.8.8.8）"
            print_message "   - 检查域名的 DNS 配置是否有问题"
        fi
    fi
    
    # 检查 Certbot 执行结果
    local final_result=$?
    if [ "$VALIDATION_METHOD" = "dns-01" ]; then
        final_result=$certbot_result
    fi
    
    if [ $final_result -ne 0 ]; then
        print_error "SSL 证书获取失败，请检查以下几点："
        
        if [ "$VALIDATION_METHOD" = "dns-01" ]; then
            print_error "1. DNS 验证失败，可能是以下原因："
            print_error "   - 是否为所有域名添加了正确的 TXT 记录？"
            for domain in "${DOMAINS[@]}"; do
                print_error "     ✗ _acme-challenge.${domain}"
            done
            print_error "   - 记录值是否完全匹配 Certbot 显示的随机字符串？"
            print_error "   - DNS 记录是否已经完全传播（可能需要等待更长时间）？"
            print_error ""
            print_error "2. 正在为您检查所有 DNS 记录的状态："
            for domain in "${DOMAINS[@]}"; do
                check_dns_records "$domain"
            done
            print_error ""
        else
            print_error "1. HTTP 验证失败，可能是 80 端口被占用或配置错误"
        fi
        
        print_error "3. 解决方案建议："
        print_error "   - 仔细核对每个域名的 TXT 记录名称和值是否完全正确"
        print_error "   - 等待至少 15 分钟让 DNS 记录完全传播"
        print_error "   - 使用多个 DNS 服务器检查记录是否生效（如 8.8.8.8、114.114.114.114）"
        print_error "   - 尝试清理之前的 Let's Encrypt 缓存：rm -rf /etc/letsencrypt/*"
        print_error "   - 考虑切换到 HTTP-01 验证方式（需要开放 80 端口）"
        print_error ""
        print_error "如果问题仍然存在，请尝试再次运行脚本。"
        exit 1
    fi
    
    print_message "SSL 证书获取成功"
}
# 生成正式的 Nginx SSL 配置
generate_nginx_ssl_config() {
    print_title "生成正式 Nginx SSL 配置"
    
    # 根据证书类型设置不同的证书路径
    if [ "$CERT_TYPE" = "letsencrypt" ]; then
        # Let's Encrypt 证书路径
        CERT_PATH="/etc/letsencrypt/live/$MAIN_DOMAIN/fullchain.pem"
        KEY_PATH="/etc/letsencrypt/live/$MAIN_DOMAIN/privkey.pem"
    else
        # 自定义证书路径
        CERT_PATH="/etc/nginx/ssl/fullchain.pem"
        KEY_PATH="/etc/nginx/ssl/privkey.pem"
    fi
    
    # 确保在脚本所在目录生成配置文件
    # 检查并删除可能存在的同名目录或文件
    if [ -e "$SCRIPT_DIR/nginx-ssl.conf" ]; then
        if [ -d "$SCRIPT_DIR/nginx-ssl.conf" ]; then
            rm -rf "$SCRIPT_DIR/nginx-ssl.conf"
        else
            rm -f "$SCRIPT_DIR/nginx-ssl.conf"
        fi
    fi
    
    # 生成完整的 Nginx 配置文件
    cat > "$SCRIPT_DIR/nginx-ssl.conf" <<EOF
# Sidifensen Blog Nginx SSL 配置

# HTTP 到 HTTPS 重定向
server {
    listen 80;
    server_name ${DOMAINS[*]};
EOF
    
    # 根据证书类型添加不同的配置
    if [ "$CERT_TYPE" = "letsencrypt" ]; then
        # 添加 Let's Encrypt 验证目录配置
        cat >> "$SCRIPT_DIR/nginx-ssl.conf" <<EOF
    
    location ^~ /.well-known/acme-challenge/ {
        allow all;
        root /var/www/certbot;
        default_type "text/plain";
        try_files \$uri =404;
    }
EOF
    fi
    
    # 继续添加共用的重定向配置
    cat >> "$SCRIPT_DIR/nginx-ssl.conf" <<EOF
    
    location / {
        return 301 https://\$host\$request_uri;
    }
}

# 主域名 HTTPS 配置 (用户端)
server {
    listen 443 ssl;
    http2 on;
    server_name $MAIN_DOMAIN www.$MAIN_DOMAIN;
    
    # SSL 配置
    ssl_certificate $CERT_PATH;
    ssl_certificate_key $KEY_PATH;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_session_tickets off;
    
    # TLS 版本配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;
    
    # HSTS 配置
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 代理到用户端前端
        location / {
            proxy_pass http://frontend-user:80;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Port $server_port;
        }
}

# 管理端域名 HTTPS 配置
server {
    listen 443 ssl;
    http2 on;
    server_name admin.$MAIN_DOMAIN;
    
    # SSL 配置
    ssl_certificate $CERT_PATH;
    ssl_certificate_key $KEY_PATH;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_session_tickets off;
    
    # TLS 版本配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;
    
    # HSTS 配置
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 代理到管理端前端
        location / {
            proxy_pass http://frontend-admin:80;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Port $server_port;
        }
}

# API 域名 HTTPS 配置
server {
    listen 443 ssl;
    http2 on;
    server_name api.$MAIN_DOMAIN;
    
    # SSL 配置
    ssl_certificate $CERT_PATH;
    ssl_certificate_key $KEY_PATH;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_session_tickets off;
    
    # TLS 版本配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;
    
    # HSTS 配置
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 代理到后端服务
        location / {
            proxy_pass http://backend:5000;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Port $server_port;
            
            # 增加超时配置
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }
}

# 图片域名 HTTPS 配置
server {
    listen 443 ssl;
    http2 on;
    server_name image.$MAIN_DOMAIN;
    
    # SSL 配置
    ssl_certificate $CERT_PATH;
    ssl_certificate_key $KEY_PATH;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_session_tickets off;
    
    # TLS 版本配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384';
    ssl_prefer_server_ciphers off;
    
    # HSTS 配置
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    
    # 代理到 MinIO 服务
        location / {
            proxy_pass http://minio:9000;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Port $server_port;
            
            # MinIO 特殊配置
            proxy_buffering off;
            proxy_request_buffering off;
            
            # 增加超时配置
            proxy_connect_timeout 300s;
            proxy_send_timeout 300s;
            proxy_read_timeout 300s;
            send_timeout 300s;
        }
}
EOF
    
    print_message "正式 Nginx SSL 配置生成完成"
}

# 停止临时 Nginx 容器
stop_temp_nginx() {
    print_title "停止临时 Nginx 容器"
    CONTAINER_NAME="sidifensen-nginx-temp"
    
    # 使用精确匹配方式停止和删除容器
    if docker ps -a --filter name="^/$CONTAINER_NAME$" -q | grep -q .; then
        docker stop "$CONTAINER_NAME"
        docker rm "$CONTAINER_NAME"
        print_message "临时 Nginx 容器已停止并删除"
    else
        print_message "临时 Nginx 容器不存在，无需清理"
    fi
}

# 设置证书自动续约
setup_auto_renew() {
    print_title "设置证书自动续约"
    
    # 创建续约脚本
    cat > ./renew_cert.sh <<EOF
#!/bin/bash

# SSL 证书自动续约脚本

print_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: $1"
}

print_error() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1"
}

print_message "开始续约 SSL 证书..."

# 获取当前脚本所在目录的绝对路径
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# 运行 Certbot 续约命令（使用 HTTP-01 验证，支持自动续约）    
docker run --rm -it \
    -v "/etc/letsencrypt:/etc/letsencrypt" \
    -v "/var/lib/letsencrypt:/var/lib/letsencrypt" \
    -v "$SCRIPT_DIR/certbot-webroot:/var/www/certbot" \
    certbot/certbot \
    renew \
    --webroot \
    --webroot-path=/var/www/certbot \
    --quiet

# 检查续约是否成功
if [ $? -eq 0 ]; then
    print_message "证书续约成功，重启 Nginx 服务..."
    docker restart sidifensen-nginx
    print_message "Nginx 服务已重启"
else
    print_error "证书续约失败，请检查以下几点："
    print_error "1. 80 端口是否可以正常访问？"
    print_error "2. /var/www/certbot 目录是否正确挂载？"
    print_error "3. 域名的 DNS 配置是否正确？"
    print_error ""
    print_error "请手动运行脚本并按照提示进行修复。"
    exit 1
fi
EOF
    
    chmod +x ./renew_cert.sh
    
    # 设置 crontab 定时任务
    CRON_JOB="0 2 * * * cd $(pwd) && bash ./renew_cert.sh >> ./cert_renew.log 2>&1"
    
    print_message "将为当前用户设置证书自动续约任务"
    print_message "计划任务: 每天凌晨 2 点执行证书续约"
    
    # 检查是否已存在相同的 cron 任务
    if crontab -l 2>/dev/null | grep -q "renew_cert.sh"; then
        print_warning "已存在证书续约的计划任务，将更新为新的配置"
        (crontab -l 2>/dev/null | grep -v "renew_cert.sh"; echo "$CRON_JOB") | crontab -
    else
        (crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -
    fi
    
    print_message "证书自动续约设置完成"
}

# 更新环境变量配置
update_env_config() {
    print_title "更新环境变量配置"
    
    # 更新 .env 文件中的前端地址配置
    if [ -f ".env" ]; then
        # 更新用户端前端地址
        sed -i "s|^FRONTEND_USER_HOST=.*|FRONTEND_USER_HOST=https://$MAIN_DOMAIN/|" .env
        # 更新管理端前端地址
        sed -i "s|^FRONTEND_ADMIN_HOST=.*|FRONTEND_ADMIN_HOST=https://admin.$MAIN_DOMAIN/|" .env
        # 更新 MinIO 公网访问地址
        sed -i "s|^MINIO_PUBLIC_POINT=.*|MINIO_PUBLIC_POINT=https://image.$MAIN_DOMAIN|" .env
        print_message ".env 文件已更新"
    else
        print_warning ".env 文件不存在，将使用默认配置"
    fi
}

# 主函数
main() {
    print_title "Sidifensen Blog SSL 证书获取脚本"
    
    # 检查环境
    check_requirements
    check_current_directory
    
    # 获取用户配置
    get_user_input
    
    # 准备工作
    create_directories
    
    # 根据证书类型执行不同流程
    if [ "$CERT_TYPE" = "letsencrypt" ]; then
        # Let's Encrypt 证书流程
        if [ "$VALIDATION_METHOD" = "http-01" ]; then
            # 生成临时 Nginx 配置
            generate_temp_nginx_config
            
            # 启动临时 Nginx 容器
            start_temp_nginx
        fi
        
        # 获取 SSL 证书
        get_ssl_certificates
        
        if [ "$VALIDATION_METHOD" = "http-01" ]; then
            # 停止临时 Nginx 容器
            stop_temp_nginx
        fi
        
        # 设置自动续约
        setup_auto_renew
        
        # 完成提示
        print_title "SSL 证书获取完成"
        print_message "🎉 Let's Encrypt 证书获取成功！"
        print_message "📁 证书存储位置: /etc/letsencrypt/live/$MAIN_DOMAIN/"
        print_message "📄 Nginx 配置文件: $SCRIPT_DIR/nginx-ssl.conf"
        print_message "🔄 证书将自动续约（每天凌晨 2 点）"
    else
        # 自定义证书流程
        # 处理自定义 SSL 证书
        process_custom_certificates
        
        # 完成提示
        print_title "SSL 证书配置完成"
        print_message "🎉 自定义 SSL 证书配置成功！"
        print_message "📁 证书存储位置: /etc/nginx/ssl/"
        print_message "📄 Nginx 配置文件: $SCRIPT_DIR/nginx-ssl.conf"
        print_message "⚠️  注意：自定义证书不会自动续约，请定期手动更新"
    fi
    
    # 生成正式 Nginx 配置
    generate_nginx_ssl_config
    
    # 更新环境配置
    update_env_config
    
    print_message ""
    print_message "接下来请运行: ./start.sh 启动完整的 SSL 服务"
}

main "$@"
