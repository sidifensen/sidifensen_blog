@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: Sidifensen Blog Docker 启动脚本 (Windows)

title Sidifensen Blog Docker 管理

:: 颜色定义
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:: 打印带颜色的消息
:print_message
echo %GREEN%[INFO]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

:print_title
echo.
echo %BLUE%========================================%NC%
echo %BLUE%%~1%NC%
echo %BLUE%========================================%NC%
goto :eof

:: 检查 Docker 和 Docker Compose
:check_requirements
call :print_title "检查环境要求"

docker --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker 未安装，请先安装 Docker Desktop"
    pause
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker Compose 未安装，请先安装 Docker Compose"
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('docker --version') do set docker_version=%%i
for /f "tokens=*" %%i in ('docker-compose --version') do set compose_version=%%i

call :print_message "Docker 版本: !docker_version!"
call :print_message "Docker Compose 版本: !compose_version!"
goto :eof

:: 检查环境变量文件
:check_env_file
call :print_title "检查环境配置"

if not exist "..\.env" (
    call :print_warning "未找到 .env 文件，正在创建..."
    copy env.example ..\.env >nul
    call :print_message "已创建 .env 文件，请根据需要修改配置"
    call :print_warning "建议修改 .env 文件中的敏感信息（如密码、密钥等）"
) else (
    call :print_message "找到 .env 文件"
)
goto :eof

:: 启动服务
:start_services
set compose_file=%~1
set service_name=%~2

call :print_title "启动 %service_name%"

call :print_message "正在构建并启动服务..."
docker-compose -f %compose_file% up -d --build

call :print_message "等待服务启动..."
timeout /t 10 /nobreak >nul

call :print_message "检查服务状态..."
docker-compose -f %compose_file% ps
goto :eof

:: 显示访问信息
:show_access_info
call :print_title "服务访问信息"

echo %GREEN%✅ 后端 API:%NC%        http://localhost:5000
echo %GREEN%✅ 管理端前端:%NC%      http://localhost:8000
echo %GREEN%✅ 用户端前端:%NC%      http://localhost:7000
echo %GREEN%✅ MinIO 控制台:%NC%    http://localhost:9001 (minioadmin/minioadmin123)
echo %GREEN%✅ RabbitMQ 管理:%NC%   http://localhost:15672 (admin/admin123)

echo.
call :print_message "常用命令:"
echo   查看日志: docker-compose logs -f
echo   停止服务: docker-compose down
echo   重启服务: docker-compose restart
goto :eof

:: 显示主菜单
:show_menu
echo.
call :print_title "Sidifensen Blog Docker 管理脚本"
echo 1. 启动生产环境 (完整服务)
echo 2. 启动开发环境 (仅基础服务)
echo 3. 停止所有服务
echo 4. 查看服务状态
echo 5. 查看服务日志
echo 6. 重启服务
echo 7. 清理数据 (危险操作)
echo 0. 退出
echo.
goto :eof

:: 停止服务
:stop_services
call :print_title "停止服务"

if exist "docker-compose.yml" (
    call :print_message "停止生产环境服务..."
    docker-compose down
)

if exist "docker-compose.dev.yml" (
    call :print_message "停止开发环境服务..."
    docker-compose -f docker-compose.dev.yml down
)

call :print_message "所有服务已停止"
goto :eof

:: 查看服务状态
:show_status
call :print_title "服务状态"
docker-compose ps
goto :eof

:: 查看日志
:show_logs
call :print_title "服务日志"
echo 选择要查看的服务日志:
echo 1. 所有服务
echo 2. 后端服务
echo 3. MySQL
echo 4. Redis
echo 5. MinIO
echo 6. RabbitMQ

set /p log_choice="请选择 (1-6): "

if "%log_choice%"=="1" docker-compose logs -f
if "%log_choice%"=="2" docker-compose logs -f backend
if "%log_choice%"=="3" docker-compose logs -f mysql
if "%log_choice%"=="4" docker-compose logs -f redis
if "%log_choice%"=="5" docker-compose logs -f minio
if "%log_choice%"=="6" docker-compose logs -f rabbitmq
goto :eof

:: 重启服务
:restart_services
call :print_title "重启服务"
docker-compose restart
call :print_message "服务重启完成"
goto :eof

:: 清理数据
:clean_data
call :print_title "清理数据 (危险操作)"
call :print_warning "这将删除所有数据卷中的数据，包括数据库数据！"
set /p confirm="确定要继续吗？(yes/no): "

if /i "%confirm%"=="yes" (
    call :print_message "停止并删除所有容器和数据卷..."
    docker-compose down -v
    docker-compose -f docker-compose.dev.yml down -v 2>nul
    call :print_message "数据清理完成"
) else (
    call :print_message "操作已取消"
)
goto :eof

:: 主程序
:main
:: 检查是否在 script 目录中
if not exist "docker-compose.yml" (
    call :print_error "请在 script 目录中运行此脚本"
    pause
    exit /b 1
)

call :check_requirements

:menu_loop
call :show_menu
set /p choice="请选择操作 (0-7): "

if "%choice%"=="1" (
    call :check_env_file
    call :start_services "docker-compose.yml" "生产环境"
    call :show_access_info
) else if "%choice%"=="2" (
    call :check_env_file
    call :start_services "docker-compose.dev.yml" "开发环境"
    call :print_message "开发环境基础服务已启动，可以手动启动后端和前端服务进行开发"
) else if "%choice%"=="3" (
    call :stop_services
) else if "%choice%"=="4" (
    call :show_status
) else if "%choice%"=="5" (
    call :show_logs
) else if "%choice%"=="6" (
    call :restart_services
) else if "%choice%"=="7" (
    call :clean_data
) else if "%choice%"=="0" (
    call :print_message "再见！"
    exit /b 0
) else (
    call :print_error "无效选择，请重新输入"
)

echo.
pause
goto menu_loop

:: 运行主程序
call :main
