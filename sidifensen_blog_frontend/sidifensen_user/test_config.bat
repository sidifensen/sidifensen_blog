@echo off

echo 正在构建Docker镜像...
docker build -t sidifensen_blog_frontend_user .

if %ERRORLEVEL% NEQ 0 (
    echo 镜像构建失败，请检查Dockerfile和相关文件。
    pause
    exit /b 1
)

echo 镜像构建成功，正在运行容器...
docker run -d -p 8000:80 --name sidifensen_blog_frontend_user sidifensen_blog_frontend_user

if %ERRORLEVEL% NEQ 0 (
    echo 容器运行失败，请检查端口是否被占用或其他Docker相关问题。
    pause
    exit /b 1
)

echo 容器运行成功！请访问 http://localhost:8000 测试
pause