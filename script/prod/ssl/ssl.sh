#!/bin/bash

# ========================================
# SSL证书管理脚本（交互式版本）
# 用于证书续期、检查和维护
# ========================================

# 配置变量
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-ssl.yml"
DOMAINS=("sidifensen.com" "www.sidifensen.com" "admin.sidifensen.com" "image.sidifensen.com" "minio.sidifensen.com" "api.sidifensen.com" )
EMAIL="1848221808@qq.com"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# 特殊字符
CHECKMARK="✓"
CROSS="✗"
ARROW="→"
STAR="★"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

# 显示标题
show_title() {
    clear
    echo -e "${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║                                                              ║${NC}"
    echo -e "${CYAN}║${WHITE}                  SSL证书管理系统 (交互式)                   ${CYAN}║${NC}"
    echo -e "${CYAN}║                                                              ║${NC}"
    echo -e "${CYAN}║${YELLOW}                    Sidifensen Blog                          ${CYAN}║${NC}"
    echo -e "${CYAN}║                                                              ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

# 显示分隔线
show_separator() {
    echo -e "${CYAN}────────────────────────────────────────────────────────────────${NC}"
}

# 暂停函数
pause() {
    echo ""
    echo -e "${YELLOW}按任意键继续...${NC}"
    read -n 1 -s
}

# 确认函数
confirm() {
    local message="$1"
    local default="${2:-n}"

    echo ""
    if [ "$default" = "y" ]; then
        printf "${YELLOW}$message [Y/n]: ${NC}"
    else
        printf "${YELLOW}$message [y/N]: ${NC}"
    fi

    read -n 1 response
    echo ""

    case "$response" in
        [Yy])
            return 0
            ;;
        [Nn])
            return 1
            ;;
        "")
            if [ "$default" = "y" ]; then
                return 0
            else
                return 1
            fi
            ;;
        *)
            echo -e "${RED}请输入 y 或 n${NC}"
            confirm "$message" "$default"
            ;;
    esac
}

# 输入邮箱地址
input_email() {
    echo ""
    echo -e "${CYAN}请输入用于SSL证书申请的邮箱地址：${NC}"
    echo -e "${YELLOW}当前邮箱: $EMAIL${NC}"
    echo -e "${WHITE}直接按回车使用当前邮箱，或输入新邮箱：${NC}"
    read -p "> " new_email

    if [ ! -z "$new_email" ]; then
        # 验证邮箱格式
        if [[ "$new_email" =~ ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$ ]]; then
            EMAIL="$new_email"
            log_success "邮箱已更新为: $EMAIL"
        else
            log_error "邮箱格式不正确，使用默认邮箱"
        fi
    fi
}

# 选择域名
select_domains() {
    echo ""
    echo -e "${CYAN}请选择要申请SSL证书的域名：${NC}"
    echo ""

    local selected_domains=()
    local i=1

    for domain in "${DOMAINS[@]}"; do
        echo -e "${WHITE}[$i]${NC} $domain"
        ((i++))
    done

    echo -e "${WHITE}[a]${NC} 全部域名"
    echo -e "${WHITE}[c]${NC} 自定义域名"
    echo ""

    read -p "请选择 [1-${#DOMAINS[@]}/a/c]: " choice

    case "$choice" in
        # 先匹配数字（支持多位数如 10、11 等）
        [0-9]*)
            if [[ "$choice" =~ ^[0-9]+$ ]] && [ "$choice" -ge 1 ] && [ "$choice" -le "${#DOMAINS[@]}" ]; then
                selected_domains=("${DOMAINS[$((choice-1))]}")
            else
                log_error "无效选择"
                select_domains
                return
            fi
            ;;
        "a"|"A")
            selected_domains=("${DOMAINS[@]}")
            ;;
        "c"|"C")
            echo ""
            echo -e "${CYAN}请输入自定义域名（多个域名用空格分隔）：${NC}"
            read -p "> " custom_domains
            if [ ! -z "$custom_domains" ]; then
                IFS=' ' read -ra selected_domains <<< "$custom_domains"
            else
                log_error "未输入域名"
                select_domains
                return
            fi
            ;;
        *)
            # 支持多位数字选择（如 10、11 等）
            if [[ "$choice" =~ ^[0-9]+$ ]] && [ "$choice" -ge 1 ] && [ "$choice" -le "${#DOMAINS[@]}" ]; then
                selected_domains=("${DOMAINS[$((choice-1))]}")
            else
                log_error "无效选择"
                select_domains
                return
            fi
            ;;
    esac

    DOMAINS=("${selected_domains[@]}")
    echo ""
    log_success "已选择域名: ${DOMAINS[*]}"
}

# 增强的域名解析检查
check_domain_resolution() {
    local domain="$1"
    local skip_check="$2"

    if [ "$skip_check" = "true" ]; then
        log_warning "跳过 $domain 的DNS解析检查"
        return 0
    fi

    echo -ne "${BLUE}检查 $domain 的域名解析... ${NC}"

    # 方法1: 使用nslookup
    if nslookup "$domain" > /dev/null 2>&1; then
        echo -e "${GREEN}${CHECKMARK}${NC}"
        return 0
    fi

    # 方法2: 使用dig（如果可用）
    if command -v dig > /dev/null 2>&1; then
        if dig "$domain" +short | grep -E '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' > /dev/null; then
            echo -e "${GREEN}${CHECKMARK}${NC}"
            return 0
        fi
    fi

    # 方法3: 使用host（如果可用）
    if command -v host > /dev/null 2>&1; then
        if host "$domain" | grep "has address" > /dev/null 2>&1; then
            echo -e "${GREEN}${CHECKMARK}${NC}"
            return 0
        fi
    fi

    # 方法4: 使用ping测试连通性
    if ping -c 1 -W 3 "$domain" > /dev/null 2>&1; then
        echo -e "${GREEN}${CHECKMARK}${NC}"
        return 0
    fi

    # 方法5: 使用curl测试HTTP连接
    if command -v curl > /dev/null 2>&1; then
        if curl -s --connect-timeout 5 --max-time 10 "http://$domain" > /dev/null 2>&1; then
            echo -e "${GREEN}${CHECKMARK}${NC}"
            return 0
        fi
    fi

    echo -e "${RED}${CROSS}${NC}"
    return 1
}

# 显示服务器信息
show_server_info() {
    echo ""
    echo -e "${CYAN}服务器信息：${NC}"
    show_separator
    # 使用多个备用服务获取公网 IP
    local public_ip=""
    public_ip=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null)
    if [ -z "$public_ip" ]; then
        public_ip=$(curl -s --connect-timeout 5 ipinfo.io/ip 2>/dev/null)
    fi
    if [ -z "$public_ip" ]; then
        public_ip=$(curl -s --connect-timeout 5 api.ipify.org 2>/dev/null)
    fi
    echo -e "${WHITE}服务器 IP:${NC} ${public_ip:-无法获取}"
    echo -e "${WHITE}当前时间:${NC} $(date)"
    echo -e "${WHITE}操作系统:${NC} $(uname -s) $(uname -r)"
    echo -e "${WHITE}DNS服务器:${NC}"
    cat /etc/resolv.conf | grep nameserver | head -3 | sed 's/^/  /'
    echo ""
}

# 检查证书状态（增强版 - 检查 SAN 扩展）
check_certificates() {
    show_title
    echo -e "${CYAN}${STAR} 检查 SSL 证书状态${NC}"
    show_separator

    local all_valid=true
    local main_cert="/etc/letsencrypt/live/sidifensen.com/cert.pem"

    # 首先检查主域名证书是否存在
    if [ ! -f "$main_cert" ]; then
        echo -e "${RED}${CROSS} 主域名证书不存在：$main_cert${NC}"
        echo ""
        log_error "请先申请 SSL 证书"
        pause
        return 1
    fi

    # 检查主域名证书的 SAN 扩展（包含哪些域名）
    echo -e "${WHITE}主域名证书包含的域名：${NC}"
    local san_domains=$(openssl x509 -in "$main_cert" -noout -text | grep -A1 "Subject Alternative Name" | tail -1)
    echo -e "${CYAN}  $san_domains${NC}"
    echo ""

    # 检查主域名证书的有效期
    expiry_date=$(openssl x509 -in "$main_cert" -noout -enddate | cut -d= -f2)
    expiry_epoch=$(date -d "$expiry_date" +%s)
    current_epoch=$(date +%s)
    days_left=$(( (expiry_epoch - current_epoch) / 86400 ))
    echo -e "${WHITE}主域名证书剩余有效期：${days_left} 天${NC}"
    echo ""

    # 检查每个域名的证书状态
    for domain in "${DOMAINS[@]}"; do
        echo -ne "${WHITE}检查 $domain... ${NC}"

        # 检查是否在主域名证书的 SAN 列表中
        if echo "$san_domains" | grep -q "$domain"; then
            if [ $days_left -gt 30 ]; then
                echo -e "${GREEN}${CHECKMARK} 共享证书 (剩余 $days_left 天)${NC}"
            elif [ $days_left -gt 7 ]; then
                echo -e "${YELLOW}⚠ 共享证书即将过期 (剩余 $days_left 天)${NC}"
                all_valid=false
            else
                echo -e "${RED}${CROSS} 共享证书即将过期 (剩余 $days_left 天)${NC}"
                all_valid=false
            fi
        else
            # 检查是否有独立证书
            if [ -f "/etc/letsencrypt/live/$domain/cert.pem" ]; then
                echo -e "${BLUE}独立证书${NC}"
            else
                echo -e "${RED}${CROSS} 证书不包含此域名${NC}"
                all_valid=false
            fi
        fi
    done

    echo ""
    echo ""
    if [ "$all_valid" = true ]; then
        log_success "所有证书状态正常"
    else
        log_warning "部分证书需要处理"
        echo ""
        echo -e "${YELLOW}提示：如果某个子域名显示'证书不包含此域名'，${NC}"
        echo -e "${YELLOW}      需要重新申请证书，确保在申请时包含所有子域名${NC}"
        echo ""
        echo -e "${CYAN}重新申请证书命令：${NC}"
        echo -e "  ${WHITE}sudo bash ssl.sh${NC}"
        echo -e "  选择选项 [1] 首次设置 SSL 证书"
        echo -e "  选择选项 [a] 全部域名"
    fi

    pause
}

# 显示证书详细信息
show_certificate_details() {
    show_title
    echo -e "${CYAN}${STAR} SSL证书详细信息${NC}"
    show_separator

    for domain in "${DOMAINS[@]}"; do
        cert_file="/etc/letsencrypt/live/$domain/cert.pem"
        if [ -f "$cert_file" ]; then
            echo -e "${WHITE}域名: $domain${NC}"
            echo -e "${CYAN}─────────────────────${NC}"
            openssl x509 -in "$cert_file" -noout -text | grep -E "(Subject:|Issuer:|Not Before|Not After)" | sed 's/^/  /'
            echo ""
        else
            echo -e "${WHITE}域名: $domain${NC}"
            echo -e "${RED}  证书不存在${NC}"
            echo ""
        fi
    done

    pause
}

# 首次设置SSL证书
setup_certificates() {
    show_title
    echo -e "${CYAN}${STAR} 首次设置SSL证书${NC}"
    show_separator

    # 输入邮箱
    input_email

    # 选择域名
    select_domains

    # 检查是否已有证书
    if [ -f "/etc/letsencrypt/live/${DOMAINS[0]}/cert.pem" ]; then
        log_warning "检测到已存在SSL证书"
        if ! confirm "是否要重新获取证书？这将覆盖现有证书"; then
            log_info "操作已取消"
            return 0
        fi
    fi

    # 显示服务器信息
    show_server_info

    # DNS解析检查选项
    echo -e "${CYAN}DNS解析检查选项：${NC}"
    echo -e "${WHITE}[1]${NC} 执行完整DNS检查（推荐）"
    echo -e "${WHITE}[2]${NC} 跳过DNS检查（如果确定域名解析正常）"
    echo ""
    read -p "请选择 [1-2]: " dns_choice

    local skip_dns_check=false
    if [ "$dns_choice" = "2" ]; then
        skip_dns_check=true
    fi

    # 检查域名解析
    if [ "$skip_dns_check" = false ]; then
        echo ""
        log_info "开始DNS解析检查..."
        local dns_failed=false

        for domain in "${DOMAINS[@]}"; do
            if ! check_domain_resolution "$domain" "$skip_dns_check"; then
                dns_failed=true
            fi
        done

        if [ "$dns_failed" = true ]; then
            echo ""
            log_error "域名解析检查失败！"
            echo ""
            echo -e "${YELLOW}可能的解决方案：${NC}"
            echo "1. 检查域名是否正确解析到此服务器IP"
            echo "2. 检查服务器的DNS配置 (/etc/resolv.conf)"
            echo "3. 尝试使用其他DNS服务器"
            echo "4. 等待DNS传播完成（可能需要几分钟到几小时）"
            echo ""

            if ! confirm "是否要跳过DNS检查继续？"; then
                log_info "操作已取消"
                return 1
            fi
        fi
    fi

    # 检查端口占用并自动处理
    echo ""
    log_info "检查端口占用情况..."

    # 查找占用 80 端口的容器
    CONTAINER_80=""
    if docker ps --format '{{.Names}}:{{.Ports}}' | grep -q ':80->'; then
        CONTAINER_80=$(docker ps --filter "publish=80" --format '{{.Names}}' | head -n 1)
        log_warning "端口 80 被容器占用：$CONTAINER_80"
        echo ""
        echo -e "${WHITE}正在自动停止容器：${CYAN}$CONTAINER_80${NC}"
        docker stop "$CONTAINER_80" 2>/dev/null
        sleep 1
        log_success "已停止容器：$CONTAINER_80"
    else
        log_success "端口 80 可用"
    fi

    # 最终确认
    echo ""
    echo -e "${CYAN}即将申请SSL证书：${NC}"
    echo -e "${WHITE}域名: ${DOMAINS[*]}${NC}"
    echo -e "${WHITE}邮箱: $EMAIL${NC}"
    echo ""

    if ! confirm "确定要继续获取SSL证书吗？" "y"; then
        log_info "操作已取消"
        # 恢复原来被停止的容器
        if [ -n "$CONTAINER_80" ]; then
            log_info "恢复原容器：$CONTAINER_80"
            docker start "$CONTAINER_80" 2>/dev/null
            log_success "已恢复原容器：$CONTAINER_80"
        fi
        return 0
    fi

    # 开始获取证书
    echo ""
    log_info "开始获取SSL证书，这可能需要几分钟..."

    # 创建必要的目录
    mkdir -p "$SCRIPT_DIR/certbot-webroot"
    mkdir -p /etc/nginx/ssl

    # 停止可能冲突的容器
    docker stop temp-nginx 2>/dev/null || true
    docker rm temp-nginx 2>/dev/null || true

    # 使用临时的HTTP配置（动态生成 server_name）
    log_info "生成临时nginx配置，域名: ${DOMAINS[*]}"
    cat > "$SCRIPT_DIR/nginx-temp.conf" << EOF
server {
    listen 80;
    server_name $(IFS=' '; echo "${DOMAINS[*]}");

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
        try_files \$uri =404;
    }

    location / {
        return 200 'SSL Setup in Progress - Server Time: \$time_iso8601';
        add_header Content-Type text/plain;
    }
}
EOF

    # 启动临时nginx
    echo -ne "${BLUE}启动临时HTTP服务... ${NC}"
    if docker run -d --name temp-nginx -p 80:80 \
        -v "$SCRIPT_DIR/nginx-temp.conf:/etc/nginx/conf.d/default.conf" \
        -v "$SCRIPT_DIR/certbot-webroot:/var/www/certbot" \
        nginx:alpine > /dev/null 2>&1; then
        echo -e "${GREEN}${CHECKMARK}${NC}"

        # 测试HTTP服务
        sleep 2
        for domain in "${DOMAINS[@]}"; do
            echo -ne "${BLUE}测试 $domain HTTP服务... ${NC}"
            if curl -s --connect-timeout 5 "http://$domain" | grep -q "SSL Setup in Progress"; then
                echo -e "${GREEN}${CHECKMARK}${NC}"
            else
                echo -e "${YELLOW}⚠${NC}"
            fi
        done
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "临时HTTP服务启动失败"
        return 1
    fi

    # 构建域名参数
    domain_args=""
    for domain in "${DOMAINS[@]}"; do
        domain_args="$domain_args -d $domain"
    done

    echo ""
    log_info "正在向Let's Encrypt申请证书..."
    echo -e "${YELLOW}这可能需要几分钟时间，请耐心等待...${NC}"

    # 获取证书
    if docker run --rm \
        -v /etc/letsencrypt:/etc/letsencrypt \
        -v "$SCRIPT_DIR/certbot-webroot:/var/www/certbot" \
        certbot/certbot certonly --webroot \
        --webroot-path=/var/www/certbot \
        --email "$EMAIL" \
        --agree-tos \
        --no-eff-email \
        --non-interactive \
        --cert-name sidifensen.com \
        $domain_args; then

        echo ""
        log_success "SSL证书获取成功！"

        # 生成 DH 参数（在宿主机上执行，使用预生成的参数或跳过）
        echo -ne "${BLUE}检查 DH 参数... ${NC}"
        if [ ! -f "/etc/nginx/ssl/dhparam.pem" ]; then
            # DH 参数生成非常耗时（2048 位约需 5-10 分钟），这里使用跳过策略
            # 如需生成，请在宿主机手动执行：openssl dhparam -out /etc/nginx/ssl/dhparam.pem 2048
            log_info "DH 参数不存在，如需生成请在宿主机执行：openssl dhparam -out /etc/nginx/ssl/dhparam.pem 2048"
            echo -e "${YELLOW}跳过${NC}"
        else
            echo -e "${GREEN}${CHECKMARK}${NC}"
        fi

        # 设置权限
        chmod -R 755 /etc/letsencrypt 2>/dev/null || true
        chmod -R 755 /etc/nginx/ssl 2>/dev/null || true

        # 显示证书信息
        if [ -f "/etc/letsencrypt/live/${DOMAINS[0]}/cert.pem" ]; then
            echo ""
            log_info "证书信息:"
            openssl x509 -in "/etc/letsencrypt/live/${DOMAINS[0]}/cert.pem" -noout -text | grep -E "(Subject:|Not After)" | sed 's/^/  /'
        fi

        echo ""
        log_success "SSL证书设置完成！"
        log_info "证书位置: /etc/letsencrypt/live/${DOMAINS[0]}/"
        log_info "现在可以使用 docker-compose-ssl.yml 启动完整的HTTPS服务"

    else
        echo ""
        log_error "SSL证书获取失败"
        echo ""
        echo -e "${YELLOW}可能的原因：${NC}"
        echo "1. 域名没有正确解析到此服务器"
        echo "2. 防火墙阻止了80端口"
        echo "3. Let's Encrypt服务暂时不可用"
        echo "4. 已达到证书申请频率限制"
        echo ""
    fi

    # 清理临时 nginx 容器（释放 80 端口）
    log_info "清理临时 nginx 容器..."
    docker stop temp-nginx 2>/dev/null || true
    sleep 1
    docker rm temp-nginx 2>/dev/null || true
    rm -f "$SCRIPT_DIR/nginx-temp.conf"

    # 恢复原来被停止的容器（在 80 端口释放后）
    if [ -n "$CONTAINER_80" ]; then
        log_info "正在恢复原容器：$CONTAINER_80"
        if docker start "$CONTAINER_80" 2>/dev/null; then
            log_success "已恢复原容器：$CONTAINER_80"
            # 等待容器启动
            sleep 2
            # 验证容器是否正常运行
            if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_80}$"; then
                log_success "原容器运行正常"
            else
                log_warning "原容器启动后未检测到运行状态"
            fi
        else
            log_error "恢复原容器失败，请手动启动：docker start $CONTAINER_80"
        fi
    else
        log_info "没有需要恢复的容器"
    fi

    pause
}

# 续期证书
renew_certificates() {
    show_title
    echo -e "${CYAN}${STAR} 续期 SSL 证书${NC}"
    show_separator

    log_info "检查证书有效期..."

    # 检查证书是否真的需要续期
    local needs_renewew=false
    local cert_file="/etc/letsencrypt/live/sidifensen.com/cert.pem"
    
    if [ -f "$cert_file" ]; then
        expiry_date=$(openssl x509 -in "$cert_file" -noout -enddate 2>/dev/null | cut -d= -f2)
        expiry_epoch=$(date -d "$expiry_date" +%s 2>/dev/null)
        current_epoch=$(date +%s)
        days_left=$(( (expiry_epoch - current_epoch) / 86400 ))
        
        if [ $days_left -le 30 ]; then
            log_info "证书剩余有效期：$days_left 天，需要续期"
            needs_renewew=true
        else
            log_warning "证书剩余有效期：$days_left 天，还不需要续期（> 30 天）"
            echo ""
            if ! confirm "是否要强制续期？（通常不建议）"; then
                log_info "操作已取消"
                pause
                return 0
            fi
            needs_renewew=true
        fi
    else
        log_error "证书文件不存在：$cert_file"
        pause
        return 1
    fi

    echo ""
    log_info "开始续期证书，这可能需要几分钟..."

    # 创建必要的目录
    mkdir -p "$SCRIPT_DIR/certbot-webroot"

    # 停止可能冲突的容器
    docker stop temp-nginx 2>/dev/null || true
    docker rm temp-nginx 2>/dev/null || true

    # 获取所有域名
    local domains=("sidifensen.com" "www.sidifensen.com" "admin.sidifensen.com" "image.sidifensen.com" "minio.sidifensen.com" "api.sidifensen.com")
    local domain_args=""
    for domain in "${domains[@]}"; do
        domain_args="$domain_args -d $domain"
    done

    # 生成临时 nginx 配置
    log_info "生成临时 nginx 配置，域名：${domains[*]}"
    cat > "$SCRIPT_DIR/nginx-temp.conf" << NGINX_EOF
server {
    listen 80;
    server_name sidifensen.com www.sidifensen.com admin.sidifensen.com image.sidifensen.com minio.sidifensen.com api.sidifensen.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
        try_files \$uri =404;
    }

    location / {
        return 200 'SSL Renewal in Progress';
        add_header Content-Type text/plain;
    }
}
NGINX_EOF


    # 检查 80 端口占用情况
    echo -ne "${BLUE}检查 80 端口占用... ${NC}"
    local port_80_used=false
    if docker ps --format '{{.Ports}}' | grep -q '0.0.0.0:80'; then
        port_80_used=true
        echo -e "${YELLOW}占用${NC}"
        log_warning "检测到其他容器占用了 80 端口："
        docker ps --format 'table {{.Names}}\t{{.Ports}}' | grep '0.0.0.0:80'
        echo ""
        
        # 尝试停止占用 80 端口的 nginx 容器
        log_info "尝试停止占用 80 端口的容器..."
        local container_name=$(docker ps --filter "publish=80" --format '{{.Names}}' | head -n 1)
        if [ -n "$container_name" ]; then
            log_info "停止容器：$container_name"
            docker stop "$container_name" 2>/dev/null
            sleep 1
        fi
    else
        echo -e "${GREEN}可用${NC}"
    fi

    # 生成临时 nginx 配置
    log_info "生成临时 nginx 配置，域名：${domains[*]}"
    cat > "$SCRIPT_DIR/nginx-temp.conf" << NGINX_EOF
server {
    listen 80;
    server_name sidifensen.com www.sidifensen.com admin.sidifensen.com image.sidifensen.com minio.sidifensen.com api.sidifensen.com;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
        try_files \$uri =404;
    }

    location / {
        return 200 'SSL Renewal in Progress';
        add_header Content-Type text/plain;
    }
}
NGINX_EOF

    # 启动临时 nginx
    echo -ne "${BLUE}启动临时 HTTP 服务... ${NC}"
    local docker_output
    docker_output=$(docker run -d --name temp-nginx -p 80:80 \
        -v "$SCRIPT_DIR/nginx-temp.conf:/etc/nginx/conf.d/default.conf" \
        -v "$SCRIPT_DIR/certbot-webroot:/var/www/certbot" \
        nginx:alpine 2>&1)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}${CHECKMARK}${NC}"
        sleep 2
        
        # 测试 HTTP 服务是否正常
        log_info "测试 HTTP 挑战目录访问..."
        if curl -s --connect-timeout 5 "http://sidifensen.com/.well-known/acme-challenge/test" | grep -q "SSL Renewal in Progress\|404"; then
            log_success "HTTP 挑战目录配置正确"
        else
            log_warning "HTTP 挑战目录可能配置有误，继续尝试..."
        fi
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "临时 HTTP 服务启动失败"
        echo ""
        echo -e "${YELLOW}错误信息：${NC} $docker_output"
        echo ""
        echo "可能的原因和解决方案："
        echo "1. 80 端口被占用 - 执行 'docker ps | grep :80' 查看并停止占用容器"
        echo "2. 配置文件语法错误 - 检查 $SCRIPT_DIR/nginx-temp.conf"
        echo "3. Docker 服务异常 - 执行 'systemctl restart docker'"
        echo ""
        
        # 恢复之前停止的容器
        if [ -n "$container_name" ]; then
            docker start "$container_name" 2>/dev/null
        fi
        pause
        return 1
    fi

    # 获取证书
    log_info "正在向 Let's Encrypt 申请续期..."
    echo -ne "${BLUE}执行证书续期... ${NC}"
    
    # 获取证书（使用 --force-renewal 强制续期）
    log_info "正在向 Let's Encrypt 申请续期..."
    echo -ne "${BLUE}执行证书续期... ${NC}"
    
    local certbot_output
    certbot_output=$(docker run --rm \
        -v /etc/letsencrypt:/etc/letsencrypt \
        -v "$SCRIPT_DIR/certbot-webroot:/var/www/certbot" \
        certbot/certbot certonly --webroot \
        --webroot-path=/var/www/certbot \
        --email "1848221808@qq.com" \
        --agree-tos \
        --no-eff-email \
        --non-interactive \
        --cert-name sidifensen.com \
        --force-renewal \
        $domain_args 2>&1)
    
    echo "$certbot_output"
    
    if echo "$certbot_output" | grep -qE "successfully|Congratulations"; then
        
        echo -e "${GREEN}${CHECKMARK}${NC}"
        log_success "SSL 证书续期成功！"
        
        # 清理临时 nginx
        docker stop temp-nginx 2>/dev/null || true
        docker rm temp-nginx 2>/dev/null || true
        rm -f "$SCRIPT_DIR/nginx-temp.conf"
        
        # 恢复原来停止的 nginx 容器
        if [ -n "$container_name" ]; then
            log_info "恢复原 nginx 容器：$container_name"
            docker start "$container_name" 2>/dev/null
            log_success "原 nginx 服务已恢复"
        fi
        
        # 询问是否需要重启（证书已更新，需要 reload 而不是 restart）
        echo ""
        log_info "新证书已生效，Nginx 会自动加载新证书"
        if confirm "是否需要重启 Nginx 服务？（通常不需要，除非你想确保万无一失）" "n"; then
            restart_nginx_service
        fi
    elif echo "$certbot_output" | grep -q "Certificate not yet due"; then
        echo -e "${YELLOW}⚠${NC}"
        log_warning "Certbot 认为证书还不需要续期"
        log_info "但你已选择强制续期，这可能是 Let's Encrypt 的速率限制"
        log_info "新证书可能已生成，请检查证书状态"
        
        # 清理临时 nginx
        docker stop temp-nginx 2>/dev/null || true
        docker rm temp-nginx 2>/dev/null || true
        rm -f "$SCRIPT_DIR/nginx-temp.conf"
        
        # 恢复原来停止的容器
        if [ -n "$container_name" ]; then
            log_info "恢复原 nginx 容器：$container_name"
            docker start "$container_name" 2>/dev/null
        fi
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "SSL 证书续期失败"
        echo ""
        echo -e "${YELLOW}详细错误信息：${NC}"
        echo "$certbot_output"
        echo ""
        
        # 清理临时 nginx
        docker stop temp-nginx 2>/dev/null || true
        docker rm temp-nginx 2>/dev/null || true
        
        # 恢复原来停止的容器
        if [ -n "$container_name" ]; then
            log_info "恢复原 nginx 容器：$container_name"
            docker start "$container_name" 2>/dev/null
        fi
        
        rm -f "$SCRIPT_DIR/nginx-temp.conf"
    fi

    pause
}

# 设置自动续期
setup_auto_renewal() {
    show_title
    echo -e "${CYAN}${STAR} 设置SSL证书自动续期${NC}"
    show_separator

    log_info "配置SSL证书自动续期任务..."

    # 检查是否已存在自动续期任务
    if crontab -l 2>/dev/null | grep -q "ssl-auto-renew.sh"; then
        log_warning "检测到已存在自动续期任务"
        if ! confirm "是否要重新配置自动续期？"; then
            log_info "操作已取消"
            return 0
        fi

        # 删除现有任务
        crontab -l 2>/dev/null | grep -v "ssl-auto-renew.sh" | crontab -
    fi

    # 创建续期脚本
    # 创建续期脚本（存放在 ssl.sh 同级目录下）
    cat > "$SCRIPT_DIR/ssl-auto-renew.sh" << 'INNER_EOF'
#!/bin/bash
# SSL 证书自动续期脚本

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-ssl.yml"
LOG_FILE="$SCRIPT_DIR/ssl-renewal.log"

echo "=== SSL 证书自动续期开始 ===" >> "$LOG_FILE"
echo "执行时间: $(date)" >> "$LOG_FILE"

cd "$SCRIPT_DIR"

# 执行续期
if docker run --rm -v /etc/letsencrypt:/etc/letsencrypt certbot/certbot renew --quiet >> "$LOG_FILE" 2>&1; then
    echo "证书续期成功" >> "$LOG_FILE"

    # 重启 nginx（兼容 docker-compose V1 和 docker compose V2）
    if [ -f "$COMPOSE_FILE" ]; then
        if command -v docker compose > /dev/null 2>&1; then
            docker compose -f "$COMPOSE_FILE" restart nginx-gateway >> "$LOG_FILE" 2>&1
        else
            docker-compose -f "$COMPOSE_FILE" restart nginx-gateway >> "$LOG_FILE" 2>&1
        fi
        echo "Nginx 服务已重启" >> "$LOG_FILE"
    fi
else
    echo "证书续期失败" >> "$LOG_FILE"
fi

echo "=== SSL 证书自动续期结束 ===" >> "$LOG_FILE"
echo "" >> "$LOG_FILE"
INNER_EOF

    chmod +x "$SCRIPT_DIR/ssl-auto-renew.sh"
    log_success "自动续期脚本已创建：$SCRIPT_DIR/ssl-auto-renew.sh"

    # 选择续期频率
    echo ""
    echo -e "${CYAN}选择自动续期频率：${NC}"
    echo -e "${WHITE}[1]${NC} 每周日凌晨2点（推荐）"
    echo -e "${WHITE}[2]${NC} 每月1号凌晨2点"
    echo -e "${WHITE}[3]${NC} 每天凌晨3点"
    echo -e "${WHITE}[4]${NC} 自定义"
    echo ""
    read -p "请选择 [1-4]: " freq_choice

    local cron_job=""
    case "$freq_choice" in
        1)
            cron_job="0 2 * * 0 $SCRIPT_DIR/ssl-auto-renew.sh"
            log_info "设置为每周日凌晨2点执行"
            ;;
        2)
            cron_job="0 2 1 * * $SCRIPT_DIR/ssl-auto-renew.sh"
            log_info "设置为每月1号凌晨2点执行"
            ;;
        3)
            cron_job="0 3 * * * $SCRIPT_DIR/ssl-auto-renew.sh"
            log_info "设置为每天凌晨3点执行"
            ;;
        4)
            echo ""
            echo -e "${CYAN}请输入cron表达式（格式：分 时 日 月 周）：${NC}"
            echo -e "${YELLOW}例如：0 2 * * 0 表示每周日凌晨2点${NC}"
            read -p "> " custom_cron
            if [ ! -z "$custom_cron" ]; then
                cron_job="$custom_cron $SCRIPT_DIR/ssl-auto-renew.sh"
            else
                log_error "未输入cron表达式，使用默认设置"
                cron_job="0 2 * * 0 $SCRIPT_DIR/ssl-auto-renew.sh"
            fi
            ;;
        *)
            log_warning "无效选择，使用默认设置（每周日凌晨2点）"
            cron_job="0 2 * * 0 $SCRIPT_DIR/ssl-auto-renew.sh"
            ;;
    esac

    # 添加cron任务
    (crontab -l 2>/dev/null; echo "$cron_job") | crontab -
    log_success "自动续期任务已添加到crontab"


    # 创建日志文件（在 ssl.sh 同级目录下）
    touch "$SCRIPT_DIR/ssl-renewal.log"
    chmod 644 "$SCRIPT_DIR/ssl-renewal.log"

    echo ""
    log_success "自动续期设置完成！"
    log_info "日志文件：$SCRIPT_DIR/ssl-renewal.log"
    log_info "可以使用 'crontab -l' 查看定时任务"

    pause
}


# 重启 nginx 服务（兼容 docker-compose V1 和 docker compose V2）
restart_nginx_service() {
    echo -ne "${BLUE}重启 Nginx 服务... ${NC}"

    cd "$SCRIPT_DIR"
    if [ -f "$COMPOSE_FILE" ]; then
        # 尝试使用 docker compose (V2)，如果失败则使用 docker-compose (V1)
        if command -v docker compose > /dev/null 2>&1; then
            if docker compose -f "$COMPOSE_FILE" restart nginx-gateway > /dev/null 2>&1; then
                echo -e "${GREEN}${CHECKMARK}${NC}"
                log_success "Nginx 服务已重启"
            else
                echo -e "${RED}${CROSS}${NC}"
                log_error "Nginx 服务重启失败"
            fi
        elif docker-compose -f "$COMPOSE_FILE" restart nginx-gateway > /dev/null 2>&1; then
            echo -e "${GREEN}${CHECKMARK}${NC}"
            log_success "Nginx 服务已重启"
        else
            echo -e "${RED}${CROSS}${NC}"
            log_error "Nginx 服务重启失败"
        fi
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "找不到 docker-compose 配置文件"
    fi
}

# 管理菜单
manage_menu() {
    while true; do
        show_title
        echo -e "${CYAN}${STAR} SSL证书管理${NC}"
        show_separator

        echo -e "${WHITE}[1]${NC} 检查证书状态"
        echo -e "${WHITE}[2]${NC} 查看证书详细信息"
        echo -e "${WHITE}[3]${NC} 手动续期证书"
        echo -e "${WHITE}[4]${NC} 重启Nginx服务"
        echo -e "${WHITE}[5]${NC} 查看续期日志"
        echo -e "${WHITE}[0]${NC} 返回主菜单"
        echo ""

        read -p "请选择操作 [0-5]: " choice

        case "$choice" in
            1)
                check_certificates
                ;;
            2)
                show_certificate_details
                ;;
            3)
                renew_certificates
                ;;
            4)
                show_title
                echo -e "${CYAN}${STAR} 重启Nginx服务${NC}"
                show_separator
                restart_nginx_service
                pause
                ;;
            5)
                show_title
                echo -e "${CYAN}${STAR} 续期日志${NC}"
                show_separator
                if [ -f "$SCRIPT_DIR/ssl-renewal.log" ]; then
                    echo -e "${WHITE}最近的续期日志：${NC}"
                    echo ""
                    tail -50 "$SCRIPT_DIR/ssl-renewal.log"
                else
                    log_warning "续期日志文件不存在"
                fi
                pause
                ;;
            0)
                break
                ;;
            *)
                echo -e "${RED}无效选择，请重新输入${NC}"
                sleep 1
                ;;
        esac
    done
}

# 系统信息菜单
system_info_menu() {
    show_title
    echo -e "${CYAN}${STAR} 系统信息${NC}"
    show_separator

    # 显示服务器基本信息
    show_server_info

    # 显示Docker信息
    echo -e "${CYAN}Docker信息：${NC}"
    show_separator
    echo -e "${WHITE}Docker版本:${NC} $(docker --version 2>/dev/null || echo "未安装")"
    echo -e "${WHITE}Docker Compose版本:${NC} $(docker-compose --version 2>/dev/null || echo "未安装")"

    # 显示端口占用情况
    echo ""
    echo -e "${CYAN}端口占用情况：${NC}"
    show_separator
    echo -e "${WHITE}端口80:${NC}"
    if netstat -tlnp 2>/dev/null | grep :80 > /dev/null; then
        netstat -tlnp 2>/dev/null | grep :80 | sed 's/^/  /'
    else
        echo "  未占用"
    fi

    echo -e "${WHITE}端口443:${NC}"
    if netstat -tlnp 2>/dev/null | grep :443 > /dev/null; then
        netstat -tlnp 2>/dev/null | grep :443 | sed 's/^/  /'
    else
        echo "  未占用"
    fi

    # 显示证书文件
    echo ""
    echo -e "${CYAN}证书文件：${NC}"
    show_separator
    if [ -d "/etc/letsencrypt/live" ]; then
        ls -la /etc/letsencrypt/live/ 2>/dev/null | sed 's/^/  /' || echo "  无证书文件"
    else
        echo "  证书目录不存在"
    fi

    pause
}

# 主菜单
main_menu() {
    while true; do
        show_title
        echo -e "${CYAN}${STAR} 主菜单${NC}"
        show_separator

        echo -e "${WHITE}[1]${NC} 首次设置SSL证书"
        echo -e "${WHITE}[2]${NC} SSL证书管理"
        echo -e "${WHITE}[3]${NC} 设置自动续期"
        echo -e "${WHITE}[4]${NC} 系统信息"
        echo -e "${WHITE}[5]${NC} 帮助信息"
        echo -e "${WHITE}[0]${NC} 退出程序"
        echo ""

        read -p "请选择操作 [0-5]: " choice

        case "$choice" in
            1)
                setup_certificates
                ;;
            2)
                manage_menu
                ;;
            3)
                setup_auto_renewal
                ;;
            4)
                system_info_menu
                ;;
            5)
                show_help_info
                ;;
            0)
                echo ""
                log_info "感谢使用SSL证书管理系统！"
                exit 0
                ;;
            *)
                echo -e "${RED}无效选择，请重新输入${NC}"
                sleep 1
                ;;
        esac
    done
}

# 显示帮助信息
show_help_info() {
    show_title
    echo -e "${CYAN}${STAR} 帮助信息${NC}"
    show_separator

    echo -e "${WHITE}SSL证书管理系统使用指南：${NC}"
    echo ""

    echo -e "${CYAN}1. 首次设置SSL证书${NC}"
    echo "   - 为域名申请Let's Encrypt免费SSL证书"
    echo "   - 自动配置nginx和certbot"
    echo "   - 支持多域名证书申请"
    echo ""

    echo -e "${CYAN}2. SSL证书管理${NC}"
    echo "   - 检查证书状态和有效期"
    echo "   - 查看证书详细信息"
    echo "   - 手动续期证书"
    echo "   - 重启nginx服务"
    echo ""

    echo -e "${CYAN}3. 自动续期${NC}"
    echo "   - 设置定时任务自动续期证书"
    echo "   - 支持多种续期频率"
    echo "   - 自动重启nginx服务"
    echo ""

    echo -e "${CYAN}4. 注意事项${NC}"
    echo "   - 确保域名正确解析到服务器IP"
    echo "   - 确保80和443端口可访问"
    echo "   - 建议定期检查证书状态"
    echo "   - 自动续期任务会记录日志"
    echo ""

    echo -e "${CYAN}5. 故障排除${NC}"
    echo "   - 检查DNS解析：nslookup 域名"
    echo "   - 检查端口：netstat -tlnp | grep :80"
    echo "   - 查看日志：$SCRIPT_DIR/ssl-renewal.log"
    echo "   - 手动测试：curl -I https://域名"
    echo ""

    pause
}

# 检查权限
check_permissions() {
    if [[ $EUID -ne 0 ]]; then
        echo -e "${RED}错误: 此脚本需要root权限运行${NC}"
        echo -e "${YELLOW}请使用: sudo bash $0${NC}"
        exit 1
    fi
}

# 检查依赖
check_dependencies() {
    local missing_deps=()

    # 检查必要的命令
    for cmd in docker docker-compose openssl curl; do
        if ! command -v $cmd > /dev/null 2>&1; then
            missing_deps+=("$cmd")
        fi
    done

    if [ ${#missing_deps[@]} -gt 0 ]; then
        echo -e "${RED}错误: 缺少必要的依赖${NC}"
        echo -e "${YELLOW}缺少的命令: ${missing_deps[*]}${NC}"
        echo ""
        echo "请安装缺少的依赖后重试"
        exit 1
    fi
}

# 主程序入口
main() {
    # 检查权限和依赖
    check_permissions
    check_dependencies

    # 启动主菜单
    main_menu
}

# 运行主程序
main "$@"
