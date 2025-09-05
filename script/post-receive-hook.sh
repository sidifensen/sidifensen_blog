#!/bin/bash

# Git Hook: 自动部署脚本
# 将此文件放在服务器的 Git 仓库的 hooks/post-receive

PROJECT_DIR="/opt/sidifensen_blog"
DEPLOY_BRANCH="main"

echo "收到推送，开始自动部署..."

# 检查推送的分支
while read oldrev newrev refname; do
    branch=$(git rev-parse --symbolic --abbrev-ref $refname)
    
    if [ "$branch" == "$DEPLOY_BRANCH" ]; then
        echo "检测到 $DEPLOY_BRANCH 分支更新，开始部署..."
        
        # 进入项目目录
        cd $PROJECT_DIR
        
        # 拉取最新代码
        git --git-dir=$PROJECT_DIR/.git --work-tree=$PROJECT_DIR pull origin $DEPLOY_BRANCH
        
        # 重新构建和启动服务
        cd $PROJECT_DIR/script
        docker-compose up -d --build
        
        echo "部署完成！"
    else
        echo "忽略分支 $branch 的推送"
    fi
done
