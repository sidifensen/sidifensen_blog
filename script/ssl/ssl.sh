#!/bin/bash

# ========================================
# SSL证书管理脚本（交互式版本）
# 用于证书续期、检查和维护
# ========================================

# 配置变量
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-ssl.yml"
DOMAINS=("sidifensen.com" "www.sidifensen.com" "admin.sidifensen.com" "image.sidifensen.com" "minio.sidifensen.com")
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
        echo -e "${YELLOW}$message [Y/n]: ${NC}\c"
    else
        echo -e "${YELLOW}$message [y/N]: ${NC}\c"
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
        [1-9])
            if [ "$choice" -le "${#DOMAINS[@]}" ]; then
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
            log_error "无效选择"
            select_domains
            return
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
    echo -e "${WHITE}服务器IP:${NC} $(curl -s ifconfig.me 2>/dev/null || echo "无法获取")"
    echo -e "${WHITE}当前时间:${NC} $(date)"
    echo -e "${WHITE}操作系统:${NC} $(uname -s) $(uname -r)"
    echo -e "${WHITE}DNS服务器:${NC}"
    cat /etc/resolv.conf | grep nameserver | head -3 | sed 's/^/  /'
    echo ""
}

# 检查证书状态
check_certificates() {
    show_title
    echo -e "${CYAN}${STAR} 检查SSL证书状态${NC}"
    show_separator
    
    local all_valid=true
    
    for domain in "${DOMAINS[@]}"; do
        echo -ne "${WHITE}检查 $domain... ${NC}"
        
        if [ -f "/etc/letsencrypt/live/$domain/cert.pem" ]; then
            expiry_date=$(openssl x509 -in "/etc/letsencrypt/live/$domain/cert.pem" -noout -enddate | cut -d= -f2)
            expiry_epoch=$(date -d "$expiry_date" +%s)
            current_epoch=$(date +%s)
            days_left=$(( (expiry_epoch - current_epoch) / 86400 ))
            
            if [ $days_left -gt 30 ]; then
                echo -e "${GREEN}${CHECKMARK} 有效 (剩余 $days_left 天)${NC}"
            elif [ $days_left -gt 7 ]; then
                echo -e "${YELLOW}⚠ 即将过期 (剩余 $days_left 天)${NC}"
                all_valid=false
            else
                echo -e "${RED}${CROSS} 即将过期 (剩余 $days_left 天)${NC}"
                all_valid=false
            fi
        else
            # 检查是否存在主域名证书
            if [ -f "/etc/letsencrypt/live/sidifensen.com/cert.pem" ]; then
                echo -e "${BLUE}使用主域名证书${NC}"
            else
                echo -e "${RED}${CROSS} 证书不存在${NC}"
                all_valid=false
            fi
        fi
    done
    
    echo ""
    if [ "$all_valid" = true ]; then
        log_success "所有证书状态正常"
    else
        log_warning "部分证书需要处理"
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
    
    # 检查端口占用
    echo ""
    log_info "检查端口占用情况..."
    if netstat -tlnp 2>/dev/null | grep :80 > /dev/null; then
        log_warning "端口80已被占用"
        netstat -tlnp 2>/dev/null | grep :80
        echo ""
        if ! confirm "是否要继续？这可能会导致证书获取失败"; then
            log_info "操作已取消"
            return 0
        fi
    else
        log_success "端口80可用"
    fi
    
    # 最终确认
    echo ""
    echo -e "${CYAN}即将申请SSL证书：${NC}"
    echo -e "${WHITE}域名: ${DOMAINS[*]}${NC}"
    echo -e "${WHITE}邮箱: $EMAIL${NC}"
    echo ""
    
    if ! confirm "确定要继续获取SSL证书吗？" "y"; then
        log_info "操作已取消"
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
    
    # 使用临时的HTTP配置
    cat > "$SCRIPT_DIR/nginx-temp.conf" << 'EOF'
server {
    listen 80;
    server_name sidifensen.com www.sidifensen.com admin.sidifensen.com image.sidifensen.com minio.sidifensen.com;
    
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
        try_files $uri =404;
    }
    
    location / {
        return 200 'SSL Setup in Progress - Server Time: $time_iso8601';
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
        
        # 生成DH参数
        echo -ne "${BLUE}生成DH参数... ${NC}"
        if [ ! -f "/etc/nginx/ssl/dhparam.pem" ]; then
            openssl dhparam -out /etc/nginx/ssl/dhparam.pem 2048 2>/dev/null
        fi
        echo -e "${GREEN}${CHECKMARK}${NC}"
        
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
    
    # 清理临时文件
    docker stop temp-nginx 2>/dev/null || true
    docker rm temp-nginx 2>/dev/null || true
    rm -f "$SCRIPT_DIR/nginx-temp.conf"
    
    pause
}

# 续期证书
renew_certificates() {
    show_title
    echo -e "${CYAN}${STAR} 续期SSL证书${NC}"
    show_separator
    
    log_info "检查证书续期状态..."
    
    # 执行干运行测试
    echo -ne "${BLUE}执行续期预检查... ${NC}"
    if docker run --rm \
        -v /etc/letsencrypt:/etc/letsencrypt \
        certbot/certbot renew --dry-run --quiet 2>/dev/null; then
        echo -e "${GREEN}${CHECKMARK}${NC}"
        
        if confirm "预检查通过，是否要执行证书续期？" "y"; then
            echo ""
            echo -ne "${BLUE}执行证书续期... ${NC}"
            if docker run --rm \
                -v /etc/letsencrypt:/etc/letsencrypt \
                certbot/certbot renew --quiet 2>/dev/null; then
                echo -e "${GREEN}${CHECKMARK}${NC}"
                log_success "证书续期成功"
                
                # 询问是否重启nginx
                if confirm "是否要重启Nginx服务以应用新证书？" "y"; then
                    restart_nginx_service
                fi
            else
                echo -e "${RED}${CROSS}${NC}"
                log_error "证书续期失败"
            fi
        fi
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "证书续期预检查失败，可能不需要续期或存在配置问题"
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
    cat > /usr/local/bin/ssl-auto-renew.sh << EOF
#!/bin/bash
# SSL证书自动续期脚本
# 生成时间: $(date)

LOG_FILE="/var/log/ssl-renewal.log"

echo "=== SSL证书自动续期开始 ===" >> \$LOG_FILE
echo "执行时间: \$(date)" >> \$LOG_FILE

cd "$SCRIPT_DIR"

# 执行续期
if docker run --rm -v /etc/letsencrypt:/etc/letsencrypt certbot/certbot renew --quiet >> \$LOG_FILE 2>&1; then
    echo "证书续期成功" >> \$LOG_FILE
    
    # 重启nginx
    if [ -f "$COMPOSE_FILE" ]; then
        docker-compose -f "$COMPOSE_FILE" restart nginx-gateway >> \$LOG_FILE 2>&1
        echo "Nginx服务已重启" >> \$LOG_FILE
    fi
else
    echo "证书续期失败" >> \$LOG_FILE
fi

echo "=== SSL证书自动续期结束 ===" >> \$LOG_FILE
echo "" >> \$LOG_FILE
EOF
    
    chmod +x /usr/local/bin/ssl-auto-renew.sh
    log_success "自动续期脚本已创建"
    
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
            cron_job="0 2 * * 0 /usr/local/bin/ssl-auto-renew.sh"
            log_info "设置为每周日凌晨2点执行"
            ;;
        2)
            cron_job="0 2 1 * * /usr/local/bin/ssl-auto-renew.sh"
            log_info "设置为每月1号凌晨2点执行"
            ;;
        3)
            cron_job="0 3 * * * /usr/local/bin/ssl-auto-renew.sh"
            log_info "设置为每天凌晨3点执行"
            ;;
        4)
            echo ""
            echo -e "${CYAN}请输入cron表达式（格式：分 时 日 月 周）：${NC}"
            echo -e "${YELLOW}例如：0 2 * * 0 表示每周日凌晨2点${NC}"
            read -p "> " custom_cron
            if [ ! -z "$custom_cron" ]; then
                cron_job="$custom_cron /usr/local/bin/ssl-auto-renew.sh"
            else
                log_error "未输入cron表达式，使用默认设置"
                cron_job="0 2 * * 0 /usr/local/bin/ssl-auto-renew.sh"
            fi
            ;;
        *)
            log_warning "无效选择，使用默认设置（每周日凌晨2点）"
            cron_job="0 2 * * 0 /usr/local/bin/ssl-auto-renew.sh"
            ;;
    esac
    
    # 添加cron任务
    (crontab -l 2>/dev/null; echo "$cron_job") | crontab -
    log_success "自动续期任务已添加到crontab"
    
    # 创建日志文件
    touch /var/log/ssl-renewal.log
    chmod 644 /var/log/ssl-renewal.log
    
    echo ""
    log_success "自动续期设置完成！"
    log_info "日志文件: /var/log/ssl-renewal.log"
    log_info "可以使用 'crontab -l' 查看定时任务"
    
    pause
}

# 重启nginx服务
restart_nginx_service() {
    echo -ne "${BLUE}重启Nginx服务... ${NC}"
    
    cd "$SCRIPT_DIR"
    if [ -f "$COMPOSE_FILE" ]; then
        if docker-compose -f "$COMPOSE_FILE" restart nginx-gateway > /dev/null 2>&1; then
            echo -e "${GREEN}${CHECKMARK}${NC}"
            log_success "Nginx服务已重启"
        else
            echo -e "${RED}${CROSS}${NC}"
            log_error "Nginx服务重启失败"
        fi
    else
        echo -e "${RED}${CROSS}${NC}"
        log_error "找不到docker-compose配置文件"
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
                if [ -f "/var/log/ssl-renewal.log" ]; then
                    echo -e "${WHITE}最近的续期日志：${NC}"
                    echo ""
                    tail -50 /var/log/ssl-renewal.log
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
    echo "   - 查看日志：/var/log/ssl-renewal.log"
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