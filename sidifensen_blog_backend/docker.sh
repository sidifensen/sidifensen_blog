#!/bin/bash

# 容器名称
CONTAINER_NAME=sidifensen_backend

# 镜像名称
IMAGE_NAME=sidifensen_backend

# 宿主机日志目录 - 设置为脚本所在目录下的logs文件夹
HOST_LOG_DIR=./logs

# 检查jar文件是否存在
if [ ! -f "./sidifensen_blog_backend-1.0-SNAPSHOT.jar" ]; then
    echo "错误: 找不到jar文件 ./sidifensen_blog_backend-1.0-SNAPSHOT.jar"
    echo "请确保已将jar文件上传到正确位置"
    exit 1
fi

echo "找到jar文件，开始构建镜像..."

# 停止并删除容器
if docker ps -a | grep -q $CONTAINER_NAME; then
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
  echo "$CONTAINER_NAME 停止并删除容器"
else
  echo "$CONTAINER_NAME 未启动"
fi

# 删除旧镜像 (如果存在)
if docker images | grep -q $IMAGE_NAME; then
  docker rmi $IMAGE_NAME
  echo "删除旧镜像 $IMAGE_NAME"
fi

# 构建新镜像
if ! docker build -t $IMAGE_NAME .; then
    echo "镜像构建失败，请检查 Dockerfile 和构建过程。"
    exit 1
fi

# 创建宿主机日志目录（如果不存在）
mkdir -p $HOST_LOG_DIR

# 运行新容器，映射日志目录
docker run -d \
  --name $CONTAINER_NAME \
  -p 5000:5000 \
  -v $HOST_LOG_DIR:/app/logs $IMAGE_NAME

echo "$CONTAINER_NAME 已启动"
echo "容器信息:"
docker ps --filter "name=$CONTAINER_NAME" --format "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"

echo "日志目录已映射到 $HOST_LOG_DIR"
