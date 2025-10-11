@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

title Sidifensen Blog Docker 管理

echo 脚本开始运行...
echo 当前目录: %CD%
echo.

:: 检查是否在 script 目录中
if not exist "docker-compose.yml" (
    echo [ERROR] 请在 script 目录中运行此脚本
    echo 当前目录: %CD%
    echo 请切换到 script 目录后重新运行
    pause
    exit /b 1
)

echo 找到 docker-compose.yml 文件，继续执行...
echo.

:: 检查 Docker
echo 正在检查 Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker 未安装，请先安装 Docker Desktop
    echo 请确保 Docker Desktop 已安装并启动
    pause
    exit /b 1
)

:: 检查 Docker Compose
echo 正在检查 Docker Compose...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose 未安装，请先安装 Docker Compose
    echo 请确保 Docker Compose 已正确安装
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('docker --version') do set docker_version=%%i
for /f "tokens=*" %%i in ('docker-compose --version') do set compose_version=%%i

echo [INFO] Docker 版本: !docker_version!
echo [INFO] Docker Compose 版本: !compose_version!
echo.

:: 检查环境变量文件
echo ========================================
echo 检查环境配置
echo ========================================

if not exist "..\.env" (
    echo [WARNING] 未找到 .env 文件，正在创建...
    copy env.example ..\.env >nul
    echo [INFO] 已创建 .env 文件，请根据需要修改配置
    echo [WARNING] 建议修改 .env 文件中的敏感信息（如密码、密钥等）
) else (
    echo [INFO] 找到 .env 文件
)
echo.

:: 跳过函数定义，直接进入主菜单
goto menu_loop

:: ========================================
:: 函数定义区域
:: ========================================

:: 检查打包文件的函数
:check_build_files
echo ========================================
echo 检查项目打包文件
echo ========================================

set build_error=0

:: 检查后端 jar 包
echo [INFO] 检查后端 jar 包...
dir "..\sidifensen_blog_backend\target\*.jar" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] 未找到后端 jar 包
    echo [ERROR] 请先在后端项目目录运行: mvn clean package
    set build_error=1
) else (
    echo [INFO] 后端 jar 包检查通过
)

:: 检查管理端 dist 目录
echo [INFO] 检查管理端 dist 目录...
if not exist "..\sidifensen_blog_frontend\sidifensen_admin\dist" (
    echo [ERROR] 未找到管理端 dist 目录
    echo [ERROR] 请先在管理端项目目录运行: npm run build
    set build_error=1
) else (
    echo [INFO] 管理端 dist 目录检查通过
)

:: 检查用户端 dist 目录
echo [INFO] 检查用户端 dist 目录...
if not exist "..\sidifensen_blog_frontend\sidifensen_user\dist" (
    echo [ERROR] 未找到用户端 dist 目录
    echo [ERROR] 请先在用户端项目目录运行: npm run build
    set build_error=1
) else (
    echo [INFO] 用户端 dist 目录检查通过
)

if !build_error!==1 (
    echo.
    echo [ERROR] 请先完成所有项目的打包后再启动服务
    echo.
    exit /b 1
)

echo [INFO] 所有打包文件检查通过
echo.
exit /b 0

:menu_loop
echo.
echo ========================================
echo Sidifensen Blog Docker 管理脚本
echo ========================================
echo 1. 启动生产环境 (完整服务)
echo 2. 启动开发环境 (仅基础服务)
echo 3. 启动应用服务 (后端+前端)
echo 4. 停止所有服务
echo 5. 查看服务状态
echo 6. 查看服务日志
echo 7. 重启服务
echo 8. 清理数据 (危险操作)
echo 0. 退出
echo.

set /p choice="请选择操作 (0-8): "

if "%choice%"=="1" goto start_production
if "%choice%"=="2" goto start_development
if "%choice%"=="3" goto start_apps_only
if "%choice%"=="4" goto stop_services
if "%choice%"=="5" goto show_status
if "%choice%"=="6" goto show_logs
if "%choice%"=="7" goto restart_services
if "%choice%"=="8" goto clean_data
if "%choice%"=="0" goto exit_script
goto invalid_choice

:start_production
echo.
echo ========================================
echo 启动生产环境
echo ========================================
call :check_build_files
if errorlevel 1 goto menu_continue
echo [INFO] 正在构建并启动服务...
docker-compose -f docker-compose.yml up -d --build
echo [INFO] 等待服务启动...
timeout /t 10 /nobreak >nul
echo [INFO] 检查服务状态...
docker-compose -f docker-compose.yml ps
call :show_access_info
goto menu_continue

:start_development
echo.
echo ========================================
echo 启动开发环境
echo ========================================
echo [INFO] 正在构建并启动服务...
docker-compose -f docker-compose-service.yml up -d --build
echo [INFO] 等待服务启动...
timeout /t 10 /nobreak >nul
echo [INFO] 检查服务状态...
docker-compose -f docker-compose-service.yml ps
echo [INFO] 开发环境基础服务已启动，可以手动启动后端和前端服务进行开发
goto menu_continue

:start_apps_only
echo.
echo ========================================
echo 启动应用服务 (后端+前端)
echo ========================================
call :check_build_files
if errorlevel 1 goto menu_continue
echo [INFO] 正在启动后端和前端服务...
docker-compose -f docker-compose-apps.yml up -d --build
echo [INFO] 等待服务启动...
timeout /t 10 /nobreak >nul
echo [INFO] 检查服务状态...
docker-compose -f docker-compose-apps.yml ps
call :show_access_info
goto menu_continue

:stop_services
echo.
echo ========================================
echo 停止服务
echo ========================================
if exist "docker-compose.yml" (
    echo [INFO] 停止生产环境服务...
    docker-compose down
)
if exist "docker-compose-service.yml" (
    echo [INFO] 停止开发环境服务...
    docker-compose -f docker-compose-service.yml down
)
echo [INFO] 所有服务已停止
goto menu_continue

:show_status
echo.
echo ========================================
echo 服务状态
echo ========================================
echo [DEBUG] 正在执行 docker-compose ps...
docker-compose ps
echo [DEBUG] docker-compose ps 执行完成
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:show_logs
echo.
echo ========================================
echo 服务日志
echo ========================================
echo 选择要查看的服务日志:
echo 1) 所有服务
echo 2) 后端服务
echo 3) 管理端前端
echo 4) 用户端前端
echo 5) MySQL
echo 6) Redis
echo 7) MinIO
echo 8) RabbitMQ
echo.
set /p log_choice="请选择 (1-8): "

if "%log_choice%"=="1" goto log_all
if "%log_choice%"=="2" goto log_backend
if "%log_choice%"=="3" goto log_admin
if "%log_choice%"=="4" goto log_user
if "%log_choice%"=="5" goto log_mysql
if "%log_choice%"=="6" goto log_redis
if "%log_choice%"=="7" goto log_minio
if "%log_choice%"=="8" goto log_rabbitmq
echo [ERROR] 无效选择
pause
goto menu_loop

:log_all
echo [DEBUG] 正在查看所有服务日志...
docker-compose logs --tail=50
echo [DEBUG] 日志查看完成
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_backend
docker-compose logs --tail=50 backend
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_admin
docker-compose logs --tail=50 frontend-admin
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_user
docker-compose logs --tail=50 frontend-user
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_mysql
docker-compose logs --tail=50 mysql
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_redis
docker-compose logs --tail=50 redis
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_minio
docker-compose logs --tail=50 minio
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:log_rabbitmq
docker-compose logs --tail=50 rabbitmq
echo.
echo [INFO] 按任意键返回主菜单...
pause
goto menu_loop

:restart_services
echo.
echo ========================================
echo 重启服务
echo ========================================
docker-compose restart
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
    docker-compose down -v
    
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

:: 从环境变量文件读取端口配置
for /f "tokens=2 delims==" %%i in ('findstr "^BACKEND_PORT=" .env 2^>nul') do set backend_port=%%i
for /f "tokens=2 delims==" %%i in ('findstr "^ADMIN_PORT=" .env 2^>nul') do set admin_port=%%i
for /f "tokens=2 delims==" %%i in ('findstr "^USER_PORT=" .env 2^>nul') do set user_port=%%i
for /f "tokens=2 delims==" %%i in ('findstr "^MINIO_CONSOLE_PORT=" .env 2^>nul') do set minio_console_port=%%i
for /f "tokens=2 delims==" %%i in ('findstr "^RABBITMQ_MANAGEMENT_PORT=" .env 2^>nul') do set rabbitmq_management_port=%%i

:: 设置默认值
if not defined backend_port set backend_port=5000
if not defined admin_port set admin_port=8000
if not defined user_port set user_port=7000
if not defined minio_console_port set minio_console_port=9001
if not defined rabbitmq_management_port set rabbitmq_management_port=15672

echo 后端 API:        http://localhost:%backend_port%
echo 管理端前端:      http://localhost:%admin_port%
echo 用户端前端:      http://localhost:%user_port%
echo MinIO 控制台:    http://localhost:%minio_console_port%
echo RabbitMQ 管理:   http://localhost:%rabbitmq_management_port%

echo.
echo [INFO] 常用命令:
echo   查看日志: docker-compose logs -f
echo   停止服务: docker-compose down
echo   重启服务: docker-compose restart
exit /b 0