package com.sidifensen.config;

import com.sidifensen.exception.BlogException;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 服务健康检查器
 * 应用启动时检查所有服务连接状态，连接失败则终止启动
 */
@Component
@Slf4j
public class ServiceHealthChecker implements ApplicationRunner {

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private ConnectionFactory rabbitConnectionFactory;

    @Resource
    private MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========== 开始检查服务连接状态 ==========");

        boolean allHealthy = true;

        // 检查 MySQL
        if (!checkMySql()) {
            allHealthy = false;
        }

        // 检查 Redis
        if (!checkRedis()) {
            allHealthy = false;
        }

        // 检查 RabbitMQ
        if (!checkRabbitMQ()) {
            allHealthy = false;
        }

        // 检查 MinIO
        if (!checkMinIO()) {
            allHealthy = false;
        }

        log.info("========== 服务连接状态检查完成 ==========");

        if (!allHealthy) {
            // 连接失败，抛出异常终止应用启动
            throw new BlogException("服务异常连接失败");
        }

        log.info("\u001B[32m" + "所有服务连接正常，系统状态正常" + "\u001B[0m");
    }

    /**
     * 检查 MySQL 数据库连接
     */
    private boolean checkMySql() {
        String serviceName = "MySQL";
        try (java.sql.Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                log.info("\u001B[32m✓\u001B[0m {} 连接正常", serviceName);
                return true;
            } else {
                log.error("\u001B[31m✗\u001B[0m {} 连接无效", serviceName);
                return false;
            }
        } catch (Exception e) {
            log.error("\u001B[31m✗\u001B[0m {} 连接失败: {}", serviceName, e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Redis 连接
     */
    private boolean checkRedis() {
        String serviceName = "Redis";
        try {
            org.springframework.data.redis.connection.RedisConnection redisConn = redisConnectionFactory.getConnection();
            // 使用 PING 命令检测连接
            String pong = redisConn.ping();
            redisConn.close();
            if ("PONG".equals(pong)) {
                log.info("\u001B[32m✓\u001B[0m {} 连接正常", serviceName);
                return true;
            } else {
                log.error("\u001B[31m✗\u001B[0m {} 连接无效", serviceName);
                return false;
            }
        } catch (Exception e) {
            log.error("\u001B[31m✗\u001B[0m {} 连接失败: {}", serviceName, e.getMessage());
            return false;
        }
    }

    /**
     * 检查 RabbitMQ 连接
     */
    private boolean checkRabbitMQ() {
        String serviceName = "RabbitMQ";
        try {
            Connection rabbitConn = rabbitConnectionFactory.createConnection();
            if (rabbitConn.isOpen()) {
                rabbitConn.close();
                log.info("\u001B[32m✓\u001B[0m {} 连接正常", serviceName);
                return true;
            } else {
                log.error("\u001B[31m✗\u001B[0m {} 连接无效", serviceName);
                return false;
            }
        } catch (Exception e) {
            log.error("\u001B[31m✗\u001B[0m {} 连接失败: {}", serviceName, e.getMessage());
            return false;
        }
    }

    /**
     * 检查 MinIO 连接
     */
    private boolean checkMinIO() {
        String serviceName = "MinIO";
        try {
            minioClient.listBuckets();
            log.info("\u001B[32m✓\u001B[0m {} 连接正常", serviceName);
            return true;
        } catch (Exception e) {
            log.error("\u001B[31m✗\u001B[0m {} 连接失败: {}", serviceName, e.getMessage());
            return false;
        }
    }
}
