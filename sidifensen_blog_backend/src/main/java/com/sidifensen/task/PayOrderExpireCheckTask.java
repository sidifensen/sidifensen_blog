package com.sidifensen.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.enums.PayOrderStatusEnum;
import com.sidifensen.mapper.PayOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 订单超时补偿定时任务
 * 作为 MQ 延迟队列的兜底机制，确保 MQ 发送失败时订单仍能被正确关闭
 */
@Component
@Slf4j
public class PayOrderExpireCheckTask {

    @Resource
    private PayOrderMapper payOrderMapper;

    /**
     * 每30分钟执行一次（兜底机制，无需频繁执行）
     * 只查询最近35分钟内创建的待支付订单，避免全表扫描
     * 订单超时时间为15分钟，所以35分钟足够覆盖所有可能的过期订单
     */
    @Scheduled(fixedRate = 1800000)
    public void closeExpiredOrders() {
        long startTime = System.currentTimeMillis();

        Date now = new Date();
        Date createdAfter = Date.from(Instant.now().minus(35, ChronoUnit.MINUTES));

        LambdaQueryWrapper<PayOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrder::getStatus, PayOrderStatusEnum.PAYING.getCode())
                .lt(PayOrder::getExpiredTime, now)
                .gt(PayOrder::getCreateTime, createdAfter);

        var expiredOrders = payOrderMapper.selectList(queryWrapper);
        if (expiredOrders.isEmpty()) {
            return;
        }

        int closedCount = 0;
        for (PayOrder order : expiredOrders) {
            try {
                order.setStatus(PayOrderStatusEnum.CLOSED.getCode());
                order.setNotifyContent("系统自动关闭：订单超时未支付（定时任务补偿）");
                payOrderMapper.updateById(order);
                closedCount++;
            } catch (Exception e) {
                log.error("补偿关闭超时订单失败，orderNo={}", order.getOrderNo(), e);
            }
        }

        log.info("补偿关闭超时订单任务执行完成，共关闭 {} 个订单，耗时={}ms", closedCount, System.currentTimeMillis() - startTime);
    }
}
