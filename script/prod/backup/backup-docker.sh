#!/bin/bash
# Docker 容器完整备份脚本
# 用法：./backup-docker.sh backup|restore [target_server]

set -e

BACKUP_DIR="/opt/sidifensen-docker-backup"
CONTAINERS=("sidifensen-mysql" "sidifensen-redis" "sidifensen-minio" "sidifensen-rabbitmq" "sidifensen-backend")

# 创建备份目录
mkdir -p "$BACKUP_DIR"

backup() {
    echo "========================================="
    echo "开始备份 Docker 容器..."
    echo "========================================="

    # 提交容器为镜像
    for container in "${CONTAINERS[@]}"; do
        echo "正在提交容器：$container"
        docker commit "$container" "$container:backup" 2>/dev/null || echo "容器 $container 不存在，跳过"
    done

    # 保存镜像
    echo "正在保存镜像..."
    for container in "${CONTAINERS[@]}"; do
        if docker image inspect "$container:backup" &>/dev/null; then
            echo "保存 $container 镜像..."
            docker save -o "$BACKUP_DIR/$container-backup.tar" "$container:backup"
        fi
    done

    # 备份数据卷
    echo "正在备份数据卷..."
    VOLUMES=("sidifensen-mysql-data" "sidifensen-redis-data" "sidifensen-minio-data" "sidifensen-rabbitmq-data")
    for volume in "${VOLUMES[@]}"; do
        if docker volume inspect "$volume" &>/dev/null; then
            echo "备份数据卷：$volume"
            docker run --rm -v "$volume":/data -v "$BACKUP_DIR":/backup alpine \
                tar czf "/backup/$volume-backup.tar.gz" -C /data .
        fi
    done

    # 备份.env 文件
    if [ -f "./script/prod/.env" ]; then
        cp "./script/prod/.env" "$BACKUP_DIR/.env.backup"
    fi

    echo "========================================="
    echo "备份完成！文件保存在：$BACKUP_DIR"
    echo "========================================="
    ls -lh "$BACKUP_DIR"
}

restore() {
    echo "========================================="
    echo "开始恢复 Docker 容器..."
    echo "========================================="

    # 加载镜像
    for container in "${CONTAINERS[@]}"; do
        if [ -f "$BACKUP_DIR/$container-backup.tar" ]; then
            echo "加载 $container 镜像..."
            docker load -i "$BACKUP_DIR/$container-backup.tar"
        fi
    done

    # 恢复数据卷
    echo "正在恢复数据卷..."
    VOLUMES=("sidifensen-mysql-data" "sidifensen-redis-data" "sidifensen-minio-data" "sidifensen-rabbitmq-data")
    for volume in "${VOLUMES[@]}"; do
        if [ -f "$BACKUP_DIR/$volume-backup.tar.gz" ]; then
            echo "创建/清空数据卷：$volume"
            docker volume rm "$volume" 2>/dev/null || true
            docker volume create "$volume"

            echo "恢复数据卷：$volume"
            docker run --rm -v "$volume":/data -v "$BACKUP_DIR":/backup alpine \
                tar xzf "/backup/$volume-backup.tar.gz" -C /data
        fi
    done

    echo "========================================="
    echo "恢复完成！"
    echo "请复制 .env.backup 到项目目录并启动容器"
    echo "========================================="
}

# 传输到新服务器
transfer() {
    if [ -z "$1" ]; then
        echo "请指定目标服务器 IP"
        exit 1
    fi

    echo "正在传输备份到 $1..."
    scp -r "$BACKUP_DIR"/* root@"$1":/opt/sidifensen-docker-backup/
    echo "传输完成！"
}

case "$1" in
    backup)
        backup
        ;;
    restore)
        restore
        ;;
    transfer)
        transfer "$2"
        ;;
    *)
        echo "用法：$0 {backup|restore|transfer [server_ip]}"
        echo "  backup   - 备份所有容器和数据卷"
        echo "  restore  - 恢复容器和数据卷"
        echo "  transfer - 传输备份到指定服务器"
        exit 1
        ;;
esac
