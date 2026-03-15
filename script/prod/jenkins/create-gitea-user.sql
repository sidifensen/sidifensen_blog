-- ========================================
-- Gitea MySQL 用户创建脚本
-- ========================================
-- 用途：为 Gitea 创建专用的数据库用户
-- 执行方式：
--   1. 通过 MySQL 客户端执行
--   2. 或通过 docker exec 执行：docker exec -i sidifensen-mysql mysql -uroot -p < create-gitea-user.sql
-- ========================================

-- 创建 gitea 数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS gitea CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 创建 gitea 用户（允许从任何主机连接，适合 Docker 网络）
-- ⚠️ 请修改密码为强密码！
CREATE USER IF NOT EXISTS 'gitea'@'%' IDENTIFIED BY 'your_strong_password_here';

-- 授予 gitea 用户对 gitea 数据库的所有权限
GRANT ALL PRIVILEGES ON gitea.* TO 'gitea'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 验证用户创建（可选）
-- SELECT user, host FROM mysql.user WHERE user = 'gitea';
-- SHOW GRANTS FOR 'gitea'@'%';

