package com.sidifensen.rabbitmq;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.dto.PayOrderExpireMessage;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.enums.IsReadStatusEnum;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.domain.enums.PayOrderStatusEnum;
import com.sidifensen.mapper.PayOrderMapper;
import com.sidifensen.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 死信队列监听器
 * 功能：
 * 1. 监听死信队列，处理重试失败的消息
 * 2. 记录错误日志，便于排查问题
 * 3. 可以发送告警通知管理员
 * 
 * @author sidifensen
 * @since 2025-10-06
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "rabbitmq.listener.enabled", havingValue = "true", matchIfMissing = true)
public class DeadLetterQueueListener {

    /**
     * 使用已有的站内消息能力通知管理员处理死信，避免为死信额外新增表结构。
     */
    @Resource
    private MessageService messageService;

    @Resource
    private PayOrderMapper payOrderMapper;

    /**
     * 监听死信队列
     * 当消息重试多次失败后，会进入死信队列
     * 需要人工介入处理
     * 
     * @param message 死信消息
     */
    @RabbitListener(queues = RabbitMQConstants.Dead_Letter_Queue)
    public void handleDeadLetterMessage(Message message) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            
            // 死信队列消费到的 exchange/routingKey 是死信链路本身，这里额外解析原始业务来源。
            String exchange = message.getMessageProperties().getReceivedExchange();
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            Long deliveryTag = message.getMessageProperties().getDeliveryTag();
            String sourceQueue = extractOriginalQueue(headers);
            String sourceExchange = extractOriginalExchange(headers, exchange);
            String sourceRoutingKey = extractOriginalRoutingKey(headers, routingKey);
            Integer retryCount = extractRetryCount(headers);
            
            // 记录详细的错误日志
            log.error("============ 死信消息 ============");
            log.error("时间: {}", new Date());
            log.error("死信交换机: {}", exchange);
            log.error("死信路由键: {}", routingKey);
            log.error("原始队列: {}", sourceQueue);
            log.error("原始交换机: {}", sourceExchange);
            log.error("原始路由键: {}", sourceRoutingKey);
            log.error("投递标签: {}", deliveryTag);
            log.error("重试次数: {}", retryCount);
            log.error("消息内容: {}", messageBody);
            log.error("消息头: {}", headers);
            log.error("================================");

            // 死信消息最终需要人工确认，统一生成管理员站内消息，便于在后台集中处理。
            if (RabbitMQConstants.Email_Queue.equals(sourceQueue)) {
                handleEmailDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Examine_Queue.equals(sourceQueue)) {
                handleExamineDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Link_Approval_Queue.equals(sourceQueue)) {
                handleLinkApprovalDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Blacklist_Queue.equals(sourceQueue)) {
                handleBlacklistDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.WebSocket_Queue.equals(sourceQueue)) {
                handleWebSocketDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Operationlog_Queue.equals(sourceQueue)) {
                handleOperationlogDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Visitor_Queue.equals(sourceQueue)) {
                handleVisitorDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            } else if (RabbitMQConstants.Order_Expire_Queue.equals(sourceQueue)) {
                // 订单超时消息，需要实际关闭订单
                handleOrderExpireDeadLetter(messageBody);
            } else {
                handleUnknownDeadLetter(messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
            }
            
        } catch (Exception e) {
            log.error("处理死信消息时发生异常", e);
            // 死信队列处理失败也不抛出异常，避免死循环
        }
    }
    
    /**
     * 处理邮件死信
     */
    private void handleEmailDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("邮件发送失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("邮件发送", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }
    
    /**
     * 处理审核通知死信
     */
    private void handleExamineDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("审核通知发送失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("审核通知", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }
    
    /**
     * 处理友链审核死信
     */
    private void handleLinkApprovalDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("友链审核通知发送失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("友链审核通知", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }

    /**
     * 处理黑名单通知死信
     */
    private void handleBlacklistDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("黑名单通知发送失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("黑名单通知", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }

    /**
     * 处理 WebSocket 死信
     */
    private void handleWebSocketDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("WebSocket 异步消息发送失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("WebSocket 异步消息", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }

    /**
     * 处理操作日志死信
     */
    private void handleOperationlogDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("操作日志写入失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("操作日志写入", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }

    /**
     * 处理访客记录死信
     */
    private void handleVisitorDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("访客记录写入失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("访客记录写入", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }
    
    /**
     * 处理未知类型死信
     */
    private void handleUnknownDeadLetter(String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        log.error("未知类型消息失败，已达最大重试次数，消息内容: {}", messageBody);
        saveDeadLetterNotice("未知类型消息", messageBody, sourceQueue, sourceExchange, sourceRoutingKey, retryCount);
    }

    /**
     * 处理订单超时死信（TTL + 死信队列方式）
     * 订单超时后自动关闭
     * 注意：此方法是正常业务流程（TTL 到期自动处理），不是失败处理，所以不记录告警日志
     */
    private void handleOrderExpireDeadLetter(String messageBody) {
        try {
            PayOrderExpireMessage message = JSON.parseObject(messageBody, PayOrderExpireMessage.class);
            String orderNo = message.getOrderNo();

            // 检查订单状态，只有待支付订单才关闭
            PayOrder payOrder = payOrderMapper.selectOne(
                new LambdaQueryWrapper<PayOrder>()
                    .eq(PayOrder::getOrderNo, orderNo)
                    .last("limit 1")
            );

            if (payOrder == null) {
                log.warn("订单不存在，orderNo={}", orderNo);
                return;
            }

            if (!PayOrderStatusEnum.PAYING.getCode().equals(payOrder.getStatus())) {
                log.info("订单状态非待支付，无需关闭，orderNo={}, status={}", orderNo, payOrder.getStatus());
                return;
            }

            // 关闭超时订单
            payOrder.setStatus(PayOrderStatusEnum.CLOSED.getCode());
            payOrder.setNotifyContent("系统自动关闭：订单超时未支付");
            payOrderMapper.updateById(payOrder);

            log.info("订单已关闭，orderNo={}", orderNo);
        } catch (Exception e) {
            // 订单关闭失败属于异常，需要记录告警并保存站内消息通知管理员
            log.error("处理订单超时死信失败，messageBody={}", messageBody, e);
            saveDeadLetterNotice("订单超时关闭", messageBody,
                RabbitMQConstants.Order_Expire_Queue,
                RabbitMQConstants.Order_Expire_Exchange,
                RabbitMQConstants.Order_Expire_Routing_Key,
                null);
        }
    }

    /**
     * 将死信信息保存为管理员站内消息，便于后续人工排查和补偿处理。
     */
    private void saveDeadLetterNotice(String scene, String messageBody, String sourceQueue, String sourceExchange,
            String sourceRoutingKey, Integer retryCount) {
        try {
            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
            messageDto.setIsRead(IsReadStatusEnum.UNREAD.getCode());
            messageDto.setContent(String.format(
                    "死信告警：%s处理失败，来源队列=%s，来源交换机=%s，来源路由键=%s，重试次数=%s，消息内容=%s",
                    scene,
                    defaultText(sourceQueue),
                    defaultText(sourceExchange),
                    defaultText(sourceRoutingKey),
                    retryCount == null ? "未知" : retryCount,
                    abbreviate(messageBody, 500)));
            messageService.sendToAdmin(messageDto);
        } catch (Exception e) {
            log.error("保存死信告警到管理员消息失败，scene={}, sourceQueue={}", scene, sourceQueue, e);
        }
    }

    /**
     * 优先从 RabbitMQ 的首个死信记录中恢复原始队列，避免误用死信队列自身的 routingKey。
     */
    private String extractOriginalQueue(Map<String, Object> headers) {
        Object firstDeathQueue = headers.get("x-first-death-queue");
        if (firstDeathQueue != null) {
            return String.valueOf(firstDeathQueue);
        }
        Map<String, ?> deathInfo = extractFirstDeathInfo(headers);
        if (deathInfo == null || deathInfo.get("queue") == null) {
            return null;
        }
        return String.valueOf(deathInfo.get("queue"));
    }

    /**
     * 解析原始交换机，优先使用 RabbitMQ 自动附带的首死信头信息。
     */
    private String extractOriginalExchange(Map<String, Object> headers, String defaultExchange) {
        Object firstDeathExchange = headers.get("x-first-death-exchange");
        if (firstDeathExchange != null) {
            return String.valueOf(firstDeathExchange);
        }
        Map<String, ?> deathInfo = extractFirstDeathInfo(headers);
        if (deathInfo == null || deathInfo.get("exchange") == null) {
            return defaultExchange;
        }
        return String.valueOf(deathInfo.get("exchange"));
    }

    /**
     * 解析原始路由键，优先读取 x-death 中的 routing-keys 列表。
     */
    private String extractOriginalRoutingKey(Map<String, Object> headers, String defaultRoutingKey) {
        Map<String, ?> deathInfo = extractFirstDeathInfo(headers);
        if (deathInfo == null) {
            return defaultRoutingKey;
        }

        Object routingKeys = deathInfo.get("routing-keys");
        if (routingKeys instanceof List<?> routingKeyList && !routingKeyList.isEmpty()) {
            return String.valueOf(routingKeyList.get(0));
        }
        return defaultRoutingKey;
    }

    /**
     * 兼容自定义重试头与 RabbitMQ 原生 x-death.count，统一输出最终重试次数。
     */
    private Integer extractRetryCount(Map<String, Object> headers) {
        Object retryCount = headers.get("x-retry-count");
        if (retryCount instanceof Number number) {
            return number.intValue();
        }

        Map<String, ?> deathInfo = extractFirstDeathInfo(headers);
        if (deathInfo == null) {
            return null;
        }

        Object deadCount = deathInfo.get("count");
        if (deadCount instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    /**
     * 提取首条 x-death 记录，用于还原死信来源。
     */
    private Map<String, ?> extractFirstDeathInfo(Map<String, Object> headers) {
        Object xDeath = headers.get("x-death");
        if (!(xDeath instanceof List<?> xDeathList) || xDeathList.isEmpty()) {
            return null;
        }
        Object firstDeath = xDeathList.get(0);

        // Java 16+模式匹配 检查 firstDeath 是否是 Map 类型如果是，自动将 firstDeath 赋值给新变量 deathInfo
        if (firstDeath instanceof Map<?, ?> deathInfo) {
            return (Map<String, ?>) deathInfo;
        }
        return null;
    }

    /**
     * 为消息展示提供兜底值，避免保存 null 文本。
     */
    private String defaultText(String value) {
        return value == null || value.isBlank() ? "未知" : value;
    }

    /**
     * 站内消息仅保留关键信息，避免超长死信内容影响后台阅读。
     */
    private String abbreviate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
    
}

