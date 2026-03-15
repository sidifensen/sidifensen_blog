#!/bin/bash

# ========================================
# Sidifensen Blog MySQL 自动备份脚本（Docker 版）
# ========================================
# 功能说明：
# 1. 备份 Docker 中 MySQL 数据库到服务器本地
# 2. 自动压缩备份文件
# 3. 清理过期备份（保留最近 30 份）
# 4. 记录备份日志
# 5. 自动从上级目录 .env 文件读取 MySQL 配置
# 6. 自动创建 cron 定时任务（每天凌晨 3 点）
# 7. 自动生成 auto-backup.sh 定时任务脚本
#
# 执行后会自动：
# - 执行一次手动备份
# - 生成 auto-backup.sh 定时任务脚本
# - 添加 cron 定时任务（每天凌晨 3 点执行 auto-backup.sh）
#
# 使用方式：
# 1. 首次执行：bash backup-mysql.sh（执行备份并自动添加定时任务）
# 2. 移除定时任务：bash backup-mysql.sh --uninstall-cron
# 3. 查看定时任务：crontab -l
# 4. 手动备份：bash backup-mysql.sh（可多次执行）
#
# 定时任务说明：
# - 每天凌晨 3 点自动备份
# - Cron 表达式：0 3 * * *
# ========================================

set -e

# ========================================
# 函数定义（提前定义以便在配置加载时使用）
# ========================================

# 简单日志输出（LOG_FILE 初始化前使用）
log_simple() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[${timestamp}] $1"
}

# ========================================
# 配置区域
# ========================================

# 脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# .env 文件路径（脚本上级目录）
ENV_FILE="${SCRIPT_DIR}/../.env"

# 检查 .env 文件是否存在
if [ ! -f "${ENV_FILE}" ]; then
    echo "ERROR: .env 文件不存在：${ENV_FILE}"
    echo "请确保 script/prod/.env 文件存在"
    exit 1
fi

# 日志配置（先定义，供后面函数使用）
LOG_DIR="${SCRIPT_DIR}/logs"   # 日志目录（脚本当前目录下的 logs 文件夹）
LOG_FILE="${LOG_FILE:-${LOG_DIR}/mysql-backup.log}"

# 日志输出函数
log() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    mkdir -p "${LOG_DIR}" 2>/dev/null || true
    echo "[${timestamp}] $1" | tee -a "${LOG_FILE}"
}

# 从 .env 文件读取配置（处理 Windows 换行符）
log_simple "正在从 .env 文件加载配置..."

# 检查并修复 Windows 换行符
if file "${ENV_FILE}" | grep -q "CRLF"; then
    log_simple "WARNING: 检测到 .env 文件为 Windows 格式，自动转换中..."
    sed -i 's/\r$//' "${ENV_FILE}" 2>/dev/null || true
fi

source "${ENV_FILE}"
log "成功加载 .env 配置文件"

# Docker 容器名称
MYSQL_CONTAINER_NAME="sidifensen-mysql"

# MySQL 配置（从 .env 文件读取）
MYSQL_USER="${MYSQL_USER:-root}"              # 应用连接数据库的用户名
MYSQL_PASSWORD="${MYSQL_PASSWORD:-sidifensen123}"  # 应用连接数据库的密码
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}" # root 密码（备用）
MYSQL_DATABASE="${MYSQL_DATABASE:-sidifensen_blog}" # 数据库名称

# 备份配置
BACKUP_DIR="${SCRIPT_DIR}"   # 备份文件存储目录（脚本当前目录）
MAX_BACKUPS=30               # 最大保留备份数量（超过此数量会删除最旧的）

# Cron 定时任务配置
CRON_TIME="0 3 * * *"                        # 每天凌晨 3 点执行
CRON_COMMENT="# Sidifensen Blog MySQL 自动备份"
AUTO_BACKUP_SCRIPT="${SCRIPT_DIR}/auto-backup.sh"  # 自动备份脚本

# 创建必要目录
init_dirs() {
    mkdir -p "${BACKUP_DIR}"
    mkdir -p "${LOG_DIR}"
}

# 生成自动备份脚本 auto-backup.sh
generate_auto_backup_script() {
    local auto_script="${SCRIPT_DIR}/auto-backup.sh"

    cat > "${auto_script}" << 'AUTO_BACKUP_EOF'
#!/bin/bash
# ========================================
# Sidifensen Blog MySQL 自动备份脚本
# （由 backup-mysql.sh 自动生成）
# ========================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/../.env"
LOG_DIR="${SCRIPT_DIR}/logs"
LOG_FILE="${LOG_DIR}/mysql-backup.log"
BACKUP_DIR="${SCRIPT_DIR}"
MAX_BACKUPS=30
MYSQL_CONTAINER_NAME="sidifensen-mysql"

# 从 .env 读取配置
if [ -f "${ENV_FILE}" ]; then
    source "${ENV_FILE}"
fi

MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-sidifensen123}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"
MYSQL_DATABASE="${MYSQL_DATABASE:-sidifensen_blog}"

log_simple() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    mkdir -p "${LOG_DIR}" 2>/dev/null || true
    echo "[${timestamp}] $1" | tee -a "${LOG_FILE}"
}

check_mysql_container() {
    if docker ps --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER_NAME}$"; then
        return 0
    else
        return 1
    fi
}

# 执行备份
backup_file="${BACKUP_DIR}/${MYSQL_DATABASE}_$(date '+%Y%m%d_%H%M%S').sql"

log_simple "========== 自动备份开始 =========="
log_simple "数据库：${MYSQL_DATABASE}"
log_simple "容器：${MYSQL_CONTAINER_NAME}"

if ! check_mysql_container; then
    log_simple "ERROR: MySQL 容器未运行"
    exit 1
fi

docker exec ${MYSQL_CONTAINER_NAME} mysqldump \
    -u${MYSQL_USER} \
    -p${MYSQL_PASSWORD} \
    --databases ${MYSQL_DATABASE} \
    --single-transaction \
    --quick \
    --lock-tables=false \
    --default-character-set=utf8mb4 \
    > "${backup_file}" 2>> "${LOG_FILE}"

if [ -s "${backup_file}" ]; then
    gzip "${backup_file}"
    log_simple "备份成功：${backup_file}.gz"
else
    log_simple "ERROR: 备份文件为空"
    exit 1
fi

# 清理旧备份
backup_count=$(ls -1t "${BACKUP_DIR}"/*.sql.gz 2>/dev/null | wc -l)
if [ ${backup_count} -gt ${MAX_BACKUPS} ]; then
    to_delete=$((backup_count - MAX_BACKUPS))
    ls -1t "${BACKUP_DIR}"/*.sql.gz | tail -n ${to_delete} | xargs rm -f
    log_simple "清理了 ${to_delete} 份旧备份"
fi

log_simple "现有备份数量：${backup_count} 份"
log_simple "========== 自动备份结束 =========="
AUTO_BACKUP_EOF

    chmod +x "${auto_script}"
    log "已生成自动备份脚本：${auto_script}"
}

# 检查 MySQL 容器是否运行
check_mysql_container() {
    if docker ps --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER_NAME}$"; then
        return 0
    else
        return 1
    fi
}

# 获取 MySQL 用户名和密码（优先使用 .env 中的配置）
get_mysql_credentials() {
    # 优先使用 MYSQL_USER 和 MYSQL_PASSWORD（应用账号）
    # 如果应用账号不可用，则使用 root 账号
    if [ -n "${MYSQL_USER}" ] && [ -n "${MYSQL_PASSWORD}" ]; then
        log "使用应用账号连接：${MYSQL_USER}"
        return 0
    elif [ -n "${MYSQL_ROOT_PASSWORD}" ]; then
        log "使用 root 账号连接：root"
        MYSQL_USER="root"
        MYSQL_PASSWORD="${MYSQL_ROOT_PASSWORD}"
        return 0
    else
        log "ERROR: 未找到 MySQL 连接凭据"
        return 1
    fi
}

# 执行数据库备份
do_backup() {
    local backup_file="${BACKUP_DIR}/${MYSQL_DATABASE}_$(date '+%Y%m%d_%H%M%S').sql"

    log "=========================================="
    log "开始备份数据库：${MYSQL_DATABASE}"
    log "容器名称：${MYSQL_CONTAINER_NAME}"

    # 检查容器是否运行
    if ! check_mysql_container; then
        log "ERROR: MySQL 容器未运行，容器名：${MYSQL_CONTAINER_NAME}"
        return 1
    fi

    log "使用 Docker 方式备份..."
    docker exec ${MYSQL_CONTAINER_NAME} mysqldump \
        -u${MYSQL_USER} \
        -p${MYSQL_PASSWORD} \
        --databases ${MYSQL_DATABASE} \
        --single-transaction \
        --quick \
        --lock-tables=false \
        --default-character-set=utf8mb4 \
        > "${backup_file}"

    # 检查备份是否成功
    if [ $? -eq 0 ] && [ -s "${backup_file}" ]; then
        local file_size=$(du -h "${backup_file}" | cut -f1)
        log "备份文件创建成功：${backup_file}"
        log "备份文件大小：${file_size}"

        # 压缩备份文件
        log "正在压缩备份文件..."
        gzip "${backup_file}"

        if [ $? -eq 0 ]; then
            local compressed_file="${backup_file}.gz"
            local compressed_size=$(du -h "${compressed_file}" | cut -f1)
            log "压缩成功：${compressed_file}"
            log "压缩后大小：${compressed_size}"

            log "备份完成！"
            return 0
        else
            log "ERROR: 压缩失败"
            return 1
        fi
    else
        log "ERROR: 备份失败"
        return 1
    fi
}

# 清理过期备份（按数量清理，保留最新的 N 份）
cleanup_old_backups() {
    log "开始清理过期备份..."

    local backup_count=$(ls -1t "${BACKUP_DIR}"/*.sql.gz 2>/dev/null | wc -l)
    local deleted_count=0

    if [ ${backup_count} -gt ${MAX_BACKUPS} ]; then
        local to_delete=$((backup_count - MAX_BACKUPS))
        log "当前备份数量：${backup_count}，将删除最旧的 ${to_delete} 份备份"
        ls -1t "${BACKUP_DIR}"/*.sql.gz | tail -n ${to_delete} | xargs rm -f
        deleted_count=${to_delete}
    else
        log "当前备份数量：${backup_count}，无需清理"
    fi

    log "清理完成 - 删除：${deleted_count} 份备份"
}

# 显示备份状态
show_backup_status() {
    log "=========================================="
    log "当前备份状态："
    log "备份目录：${BACKUP_DIR}"
    log "最大保留数量：${MAX_BACKUPS} 份"
    log ""

    if [ -d "${BACKUP_DIR}" ]; then
        local count=$(ls -1 "${BACKUP_DIR}"/*.sql.gz 2>/dev/null | wc -l)
        local total_size=$(du -sh "${BACKUP_DIR}" 2>/dev/null | cut -f1)
        log "现有备份数量：${count}"
        log "总占用空间：${total_size}"
        log ""
        log "最近的备份文件："
        ls -lht "${BACKUP_DIR}"/*.sql.gz 2>/dev/null | head -5 | while read line; do
            log "  ${line}"
        done
    fi
}

# ========================================
# Cron 定时任务管理函数
# ========================================

# 安装 cron 定时任务
install_cron() {
    log_simple "=========================================="
    log_simple "正在添加 MySQL 自动备份定时任务..."
    log_simple "=========================================="

    # 检查自动备份脚本是否存在
    if [ ! -f "${AUTO_BACKUP_SCRIPT}" ]; then
        log_simple "ERROR: 自动备份脚本不存在：${AUTO_BACKUP_SCRIPT}"
        return 1
    fi

    local cron_entry="${CRON_TIME} ${AUTO_BACKUP_SCRIPT}"

    # 检查是否已存在
    local current_cron=$(crontab -l 2>/dev/null || echo "")

    if echo "${current_cron}" | grep -q "${AUTO_BACKUP_SCRIPT}"; then
        log_simple "INFO: 定时任务已存在，跳过添加"
        return 0
    fi

    # 添加新的 cron 任务
    if [ -z "${current_cron}" ]; then
        (echo "${CRON_COMMENT}"; echo "${cron_entry}") | crontab -
    else
        (echo "${current_cron}"; echo "${CRON_COMMENT}"; echo "${cron_entry}") | crontab -
    fi

    log_simple "SUCCESS: 定时任务添加成功！"
    log_simple "备份时间：每天凌晨 3 点"
    log_simple "Cron 表达式：${CRON_TIME}"
    log_simple "自动备份脚本：${AUTO_BACKUP_SCRIPT}"
    log_simple ""
    log_simple "查看当前所有定时任务：crontab -l"
    log_simple "移除定时任务：bash $0 --uninstall-cron"
}

# 卸载 cron 定时任务
uninstall_cron() {
    log_simple "=========================================="
    log_simple "正在移除 MySQL 自动备份定时任务..."
    log_simple "=========================================="

    local current_cron=$(crontab -l 2>/dev/null || echo "")

    if [ -z "${current_cron}" ]; then
        log_simple "INFO: 当前没有 cron 任务"
        return 0
    fi

    # 移除包含自动备份脚本的行和注释行
    local new_cron=$(echo "${current_cron}" | grep -v "${AUTO_BACKUP_SCRIPT}" | grep -v "${CRON_COMMENT}" || echo "")

    if [ -z "${new_cron}" ]; then
        crontab -r 2>/dev/null || true
    else
        echo "${new_cron}" | crontab -
    fi

    log_simple "SUCCESS: 定时任务已移除！"
}

# 显示帮助信息
show_help() {
    echo "Sidifensen Blog MySQL 自动备份脚本"
    echo ""
    echo "用法：bash $0 [选项]"
    echo ""
    echo "选项:"
    echo "  (无参数)           执行备份并自动添加定时任务（每天凌晨 3 点）"
    echo "  --uninstall-cron   移除定时任务"
    echo "  --help, -h         显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  bash $0                    # 执行备份并添加定时任务"
    echo "  bash $0 --uninstall-cron   # 移除定时任务"
    echo "  crontab -l                 # 查看当前所有定时任务"
}

# ========================================
# 主程序
# ========================================

main() {
    # 检查命令行参数
    case "${1:-}" in
        --uninstall-cron)
            uninstall_cron
            exit 0
            ;;
        --help|-h)
            show_help
            exit 0
            ;;
    esac

    # 初始化目录
    init_dirs

    # 生成自动备份脚本
    generate_auto_backup_script

    log "=========================================="
    log "Sidifensen Blog MySQL 备份任务启动"
    log "=========================================="
    log "配置文件：${ENV_FILE}"
    log "数据库名：${MYSQL_DATABASE}"
    log "MySQL 用户：${MYSQL_USER}"
    log "容器名称：${MYSQL_CONTAINER_NAME}"

    # 获取 MySQL 凭据
    if ! get_mysql_credentials; then
        log "ERROR: 无法获取 MySQL 连接凭据"
        exit 1
    fi

    # 执行备份
    if do_backup; then
        # 清理过期备份
        cleanup_old_backups

        # 显示状态
        show_backup_status

        # 自动添加定时任务
        install_cron

        log "=========================================="
        log "备份任务执行成功"
        log "=========================================="
    else
        log "=========================================="
        log "ERROR: 备份任务执行失败"
        log "=========================================="
        exit 1
    fi
}

# 执行主程序
main "$@"
