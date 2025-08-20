#!/bin/bash

# 容器名称
CONTAINER_NAME=sidifensen_admin

# 镜像名称
IMAGE_NAME=sidifensen_admin

# 设置脚本执行出错时立即退出
set -e

# 确认dist文件夹存在
if [ ! -d "dist" ]; then
    echo "错误: dist文件夹不存在，请确保已上传构建好的dist文件。"
    exit 1
fi

# nginx日志目录映射配置 - 宿主机目录
# 使用绝对路径避免相对路径问题
LOG_DIR=$(cd $(dirname $0); pwd)/nginx

# 创建宿主机日志目录（如果不存在）
if [ ! -d "$LOG_DIR" ]; then
    echo "创建宿主机日志目录: $LOG_DIR"
    mkdir -p $LOG_DIR
    # 使用更安全的权限设置
    chmod -R 755 $LOG_DIR
fi

echo "dist文件夹存在，开始部署..."

# 停止并删除容器
if docker ps -a --filter "name=$CONTAINER_NAME" | grep -q $CONTAINER_NAME; then
  echo "停止容器: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME
  echo "删除容器: $CONTAINER_NAME"
  docker rm $CONTAINER_NAME
else
  echo "容器 $CONTAINER_NAME 不存在或未启动"
fi

# 删除旧镜像 (如果存在)
# --filter "reference=$IMAGE_NAME" : 使用更精确的镜像过滤，仅匹配指定名称的镜像
if docker images --filter "reference=$IMAGE_NAME" | grep -q $IMAGE_NAME; then
  echo "删除旧镜像: $IMAGE_NAME"
  # 添加 -f 参数强制删除，防止因依赖导致无法删除
  docker rmi -f $IMAGE_NAME
fi

# 构建新镜像
# 使用buildx构建器以避免legacy构建器的弃用警告
# 添加 --no-cache 参数确保每次构建都是最新的
 echo "开始构建镜像: $IMAGE_NAME"
# 检查是否安装了buildx
if ! docker buildx version >/dev/null 2>&1; then
    echo "警告: buildx未安装，使用传统构建器"
    if ! docker build --no-cache -t $IMAGE_NAME .; then
        echo "错误: 镜像构建失败，请检查 Dockerfile 和构建过程。"
        exit 1
    fi
else
    if ! docker buildx build --no-cache -t $IMAGE_NAME .; then
        echo "错误: 镜像构建失败，请检查 Dockerfile 和构建过程。"
        exit 1
    fi
fi

# 运行新容器，添加日志映射和健康检查
# 添加 --restart=always 确保容器自动重启
# 添加健康检查
#  --health-cmd : 指定健康检查命令。这里使用 curl -f http://localhost/ || exit 1 ，意思是尝试访问容器内的 localhost 主页，如果访问失败则退出并返回非零状态码。
# --health-interval : 健康检查的时间间隔，这里设置为 30 秒执行一次。
# --health-timeout : 健康检查命令的超时时间，如果命令执行超过 10 秒仍未完成，则视为失败。
# --health-retries : 连续失败的最大次数，若连续 3 次检查失败，则容器被标记为不健康。
if docker run -d \
  --name $CONTAINER_NAME \
  --restart=always \
  -p 8000:80 \
  -v $LOG_DIR:/var/log/nginx \
  --health-cmd="curl -f http://localhost/ || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  $IMAGE_NAME; then
    echo "成功: $CONTAINER_NAME 已启动，日志映射到: $LOG_DIR"
else
    echo "错误: 容器启动失败，请检查配置。"
    exit 1
fi

# 输出容器信息
echo "容器信息:" 
docker ps --filter "name=$CONTAINER_NAME" --format "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"

echo "Nginx访问日志路径: $LOG_DIR/access.log"
echo "Nginx错误日志路径: $LOG_DIR/error.log"
echo "部署完成! 请访问 http://localhost:8000 查看应用"