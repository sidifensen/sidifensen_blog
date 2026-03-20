package com.sidifensen.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.PayOrderExpireMessage;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.enums.PayOrderStatusEnum;
import com.sidifensen.mapper.PayOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单超时 MQ 消费者
 * 负责处理订单超时延迟消息，自动关闭超时未支付的订单
 */
@Component
@Slf4j
public class PayOrderExpireConsumer {

    @Resource
    private PayOrderMapper payOrderMapper;

    /**
     * 处理订单超时消息（监听死信队列）
     * 消息在 order_expire_queue 中存活 15 分钟后，自动转发到 dead_letter_queue
     */
    @RabbitListener(queues = RabbitMQConstants.Dead_Letter_Queue)
    @Transactional(rollbackFor = Exception.class)
    public void handleExpireMessage(PayOrderExpireMessage message) {
        String orderNo = message.getOrderNo();
        log.info("收到订单超时消息（死信队列）, orderNo={}", orderNo);

        try {
            PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                    .eq(PayOrder::getOrderNo, orderNo)
                    .last("limit 1"));

            if (payOrder == null) {
                log.warn("订单不存在，orderNo={}", orderNo);
                return;
            }

            // 只有待支付订单才需要关闭
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
            log.error("处理订单超时消息失败，orderNo={}, error={}", orderNo, e.getMessage(), e);
            throw e; // 抛出异常让 MQ 自动重试
        }
    }
}