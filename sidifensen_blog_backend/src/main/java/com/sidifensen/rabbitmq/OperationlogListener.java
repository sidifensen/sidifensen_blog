package com.sidifensen.rabbitmq;

import cn.hutool.core.bean.BeanUtil;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.OperationlogMessage;
import com.sidifensen.domain.entity.SysOperationlog;
import com.sidifensen.service.SysOperationlogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 操作日志 MQ 消费者
 * 监听操作日志队列，将消息写入数据库
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Component
@Slf4j
public class OperationlogListener {

    @Resource
    private SysOperationlogService sysOperationlogService;

    /**
     * 监听操作日志队列
     * 处理操作日志记录插入
     *
     * 重试机制：
     * - 最大重试次数：3 次（包括首次消费）
     * - 重试间隔：5s, 10s, 20s（指数退避）
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(queues = RabbitMQConstants.Operationlog_Queue)
    public void receiveOperationlogMessage(Map<String, Object> message) {
        try {
            // 从 Map 中提取消息数据
            OperationlogMessage operationlogMessage = BeanUtil.fillBeanWithMap(message, new OperationlogMessage(), false, false);

            // 转换为实体对象
            SysOperationlog operationlog = BeanUtil.copyProperties(operationlogMessage, SysOperationlog.class);
            operationlog.setCreateTime(new Date());
            operationlog.setIsDeleted(0);

            // 插入数据库
            sysOperationlogService.insertOperationlogRecord(operationlog);

            log.debug("操作日志写入成功，module: {}, operation: {}, operator: {}",
                    operationlog.getModule(), operationlog.getOperation(), operationlog.getOperatorName());

        } catch (Exception e) {
            log.error("处理操作日志时出现异常，message={}", message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("操作日志写入失败：" + e.getMessage(), e);
        }
    }
}
