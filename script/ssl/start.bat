@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

title Sidifensen Blog SSL Docker 管理 (docker-compose-ssl.yml)

echo 脚本开始运行...
echo 当前目录: %CD%
echo.

:: 检查 docker-compose-ssl.yml 是否存在
if not exist "docker-compose-ssl.yml" (
    echo [ERROR] 未找到 docker-compose-ssl.yml，请在 script\ssl 目录中运行
    pause
    exit /b 1
)

:: 检查 Docker
echo 正在检查 Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker 未安装或未启动
    pause
    exit /b 1
)

:: 检查 Docker Compose
echo 正在检查 Docker Compose...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose 未安装
    pause
    exit /b 1
)

:: 计算 PROJECT_ROOT（为 compose 文件提供绝对路径）
for %%I in ("%~dp0..\..") do set "PROJECT_ROOT=%%~fI"
set "PROJECT_ROOT=%PROJECT_ROOT%"
echo [INFO] PROJECT_ROOT: %PROJECT_ROOT%

:: 写入/更新本目录下 .env 的 PROJECT_ROOT，确保 docker-compose 能读取
if exist ".env" (
  >nul 2>&1 findstr /b /c:"PROJECT_ROOT=" .env && (
    powershell -NoProfile -Command "(Get-Content .env) -replace '^PROJECT_ROOT=.*$', 'PROJECT_ROOT=%PROJECT_ROOT%' | Set-Content -NoNewline .env"
  ) || (
    echo PROJECT_ROOT=%PROJECT_ROOT%>>.env
  )
) else (
  echo PROJECT_ROOT=%PROJECT_ROOT%>.env
)

:menu_loop
echo.
echo ========================================
echo Sidifensen Blog SSL Docker 管理脚本
echo ========================================
echo 1. 启动生产环境 (SSL 全量服务)
echo 2. 停止所有服务
echo 3. 查看服务状态
echo 4. 查看服务日志
echo 5. 重启服务
echo 6. 清理数据 (危险操作)
echo 0. 退出
echo.

set /p choice="请选择操作 (0-6): "
if "%choice%"=="1" goto start_stack
if "%choice%"=="2" goto stop_stack
if "%choice%"=="3" goto show_status
if "%choice%"=="4" goto show_logs
if "%choice%"=="5" goto restart_stack
if "%choice%"=="6" goto clean_data
if "%choice%"=="0" goto exit_script
goto invalid_choice

:start_stack
echo.
echo ========================================
echo 启动 SSL 生产环境
echo ========================================
echo [INFO] 正在构建并启动服务...
echo [INFO] 使用项目路径: %PROJECT_ROOT%
docker-compose -f docker-compose-ssl.yml up -d --build
echo [INFO] 等待服务启动...
timeout /t 8 /nobreak >nul
echo [INFO] 检查服务状态...
docker-compose -f docker-compose-ssl.yml ps
call :show_access_info
goto menu_continue

:stop_stack
echo.
echo ========================================
echo 停止服务
echo ========================================
docker-compose -f docker-compose-ssl.yml down
echo [INFO] 所有服务已停止
goto menu_continue

:show_status
echo.
echo ========================================
echo 服务状态
echo ========================================
docker-compose -f docker-compose-ssl.yml ps
goto menu_continue

:show_logs
echo.
echo ========================================
echo 服务日志
echo ========================================
echo 选择要查看的服务日志:
echo 1) 所有服务
echo 2) Nginx 网关
echo 3) 后端服务
echo 4) 管理端前端
echo 5) 用户端前端
echo 6) MySQL
echo 7) Redis
echo 8) MinIO
echo 9) RabbitMQ
echo.
set /p log_choice="请选择 (1-9): "
if "%log_choice%"=="1" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 & goto menu_continue )
if "%log_choice%"=="2" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 nginx-gateway & goto menu_continue )
if "%log_choice%"=="3" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 backend & goto menu_continue )
if "%log_choice%"=="4" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 frontend-admin & goto menu_continue )
if "%log_choice%"=="5" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 frontend-user & goto menu_continue )
if "%log_choice%"=="6" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 mysql & goto menu_continue )
if "%log_choice%"=="7" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 redis & goto menu_continue )
if "%log_choice%"=="8" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 minio & goto menu_continue )
if "%log_choice%"=="9" ( docker-compose -f docker-compose-ssl.yml logs --tail=50 rabbitmq & goto menu_continue )
echo [ERROR] 无效选择
goto menu_continue

:restart_stack
echo.
echo ========================================
echo 重启服务
echo ========================================
docker-compose -f docker-compose-ssl.yml restart
echo [INFO] 服务重启完成
goto menu_continue

:clean_data
echo.
echo ========================================
echo 清理数据 (危险操作)
echo ========================================
echo [WARNING] 这将删除所有数据卷中的数据，包括数据库数据！
set /p "confirm=确定要继续吗？(yes/no): "
if /i "%confirm%"=="yes" (
    echo [INFO] 停止并删除所有容器和数据卷...
    docker-compose -f docker-compose-ssl.yml down -v
    echo [INFO] 删除命名数据卷...
    docker volume rm sidifensen-mysql-data >nul 2>&1
    docker volume rm sidifensen-redis-data >nul 2>&1
    docker volume rm sidifensen-minio-data >nul 2>&1
    docker volume rm sidifensen-rabbitmq-data >nul 2>&1
    echo [INFO] 数据清理完成
) else (
    echo [INFO] 操作已取消
)
goto menu_continue

:invalid_choice
echo [ERROR] 无效选择，请重新输入
goto menu_continue

:exit_script
echo [INFO] 再见！
exit /b 0

:menu_continue
echo.
pause
goto menu_loop

:show_access_info
echo.
echo ========================================
echo 服务访问信息
echo ========================================
echo 用户站点(HTTPS):    https://sidifensen.com/
echo 管理后台(HTTPS):     https://admin.sidifensen.com/
goto :eof


