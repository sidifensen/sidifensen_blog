#!/bin/bash

# 容器名称
CONTAINER_NAME=sidifensen_blog_frontend_admin

# 镜像名称
IMAGE_NAME=sidifensen_blog_frontend_admin

# 确认dist文件夹存在
if [ ! -d "dist" ]; then
    echo "错误: dist文件夹不存在，请确保已上传构建好的dist文件。"
exit 1
fi

echo "dist文件夹存在，开始部署..."

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

# 运行新容器
docker run -d \
  --name $CONTAINER_NAME \
  -p 8000:80 $IMAGE_NAME

echo "$CONTAINER_NAME 已启动"