package com.sidifensen.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.CreateVipOrderDto;
import com.sidifensen.domain.dto.PayOrderExpireMessage;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.entity.VipPlan;
import com.sidifensen.domain.enums.PayBizTypeEnum;
import com.sidifensen.domain.enums.PayChannelEnum;
import com.sidifensen.domain.enums.PayClientTypeEnum;
import com.sidifensen.domain.enums.PayOrderStatusEnum;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipOrderCreateVo;
import com.sidifensen.domain.vo.VipOrderVo;
import com.sidifensen.domain.vo.VipPlanVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.PayOrderMapper;
import com.sidifensen.service.AlipayPaymentService;
import com.sidifensen.service.PayOrderService;
import com.sidifensen.service.VipMemberService;
import com.sidifensen.service.VipPlanService;
import com.sidifensen.rabbitmq.PayOrderExpireProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * VIP 支付订单实现，负责下单、查单和支付宝回调处理。
 */
@Service
@Slf4j
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService {

    @Resource
    private PayOrderMapper payOrderMapper;

    @Resource
    private AlipayPaymentService alipayPaymentService;

    @Resource
    private VipMemberService vipMemberService;

    @Resource
    private VipPlanService vipPlanService;

    @Resource
    private PayOrderExpireProducer payOrderExpireProducer;

    /**
     * 套餐展示数据统一从数据库读取，避免发布后还要改配置文件。
     */
    @Override
    public List<VipPlanVo> listVipPlans() {
        // 用户态只展示当前启用的套餐，并在这里补齐元单位金额和说明字段。
        return vipPlanService.listEnabledPlans().stream()
                .map(plan -> {
                    VipPlanVo vipPlanVo = new VipPlanVo();
                    vipPlanVo.setCode(plan.getCode());
                    vipPlanVo.setName(plan.getName());
                    vipPlanVo.setDays(plan.getDays());
                    vipPlanVo.setPriceFen(plan.getPriceFen());
                    vipPlanVo.setPriceYuan(fenToYuan(plan.getPriceFen()));
                    vipPlanVo.setDescription(plan.getDescription());
                    return vipPlanVo;
                })
                .toList();
    }

    /**
     * 创建订单后立即返回支付宝所需的页面跳转参数。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public VipOrderCreateVo createVipOrder(Integer userId, CreateVipOrderDto createVipOrderDto) {
        // 先校验客户端类型，确保后续走到正确的支付宝产品能力。
        PayClientTypeEnum clientType = PayClientTypeEnum.fromCode(createVipOrderDto.getClientType());
        if (clientType == null) {
            throw new BlogException(BlogConstants.ParamError);
        }
        // 下单前只允许选择当前启用中的套餐，避免历史套餐继续售卖。
        VipPlan plan = vipPlanService.getEnabledPlanByCode(createVipOrderDto.getPlanCode());
        if (plan == null) {
            throw new BlogException(BlogConstants.VipPlanNotFound);
        }

        PayOrder payOrder = new PayOrder();
        // 订单里固化套餐快照，避免套餐后改价、改时长影响已创建订单。
        payOrder.setOrderNo(generateOrderNo());
        payOrder.setUserId(userId);
        payOrder.setBizType(PayBizTypeEnum.VIP_SUBSCRIPTION.getCode());
        payOrder.setPlanCode(plan.getCode());
        payOrder.setPlanName(plan.getName());
        // 下单时快照套餐时长，避免后续改套餐配置影响已创建订单。
        payOrder.setPlanDays(plan.getDays());
        payOrder.setAmountFen(plan.getPriceFen());
        payOrder.setStatus(PayOrderStatusEnum.PAYING.getCode());
        payOrder.setChannel(PayChannelEnum.ALIPAY.getCode());
        payOrder.setClientType(clientType.getCode());
        payOrder.setSubject("Sidifensen Blog " + plan.getName());
        payOrder.setExpiredTime(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)));
        if (payOrderMapper.insert(payOrder) != 1) {
            throw new BlogException(BlogConstants.VipOrderCreateError);
        }

        // 发送订单超时延迟消息，15分钟后自动关闭
        PayOrderExpireMessage expireMessage = new PayOrderExpireMessage(payOrder.getOrderNo(), userId);
        payOrderExpireProducer.sendExpireMessage(expireMessage);

        VipOrderCreateVo vipOrderCreateVo = new VipOrderCreateVo();
        vipOrderCreateVo.setOrderNo(payOrder.getOrderNo());
        vipOrderCreateVo.setClientType(clientType.getCode());
        // PC 和 H5 走的支付宝产品不同，前端根据返回字段决定如何跳转。
        if (clientType == PayClientTypeEnum.PC) {
            vipOrderCreateVo.setFormHtml(alipayPaymentService.createPagePayForm(payOrder));
        } else {
            // H5 场景优先返回直达支付链接，支付宝 SDK 未给链接时再回退表单。
            String payContent = alipayPaymentService.createWapPayContent(payOrder);
            if (payContent != null && payContent.startsWith("http")) {
                vipOrderCreateVo.setPayUrl(payContent);
            } else {
                vipOrderCreateVo.setFormHtml(payContent);
            }
        }
        return vipOrderCreateVo;
    }

    /**
     * 只允许用户读取自己的订单，避免订单号枚举带来的信息泄露。
     */
    @Override
    public VipOrderVo getCurrentUserOrder(Integer userId, String orderNo) {
        // 先按订单号读取订单，再做归属校验，避免通过订单号探测他人订单信息。
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .last("limit 1"));
        if (payOrder == null) {
            throw new BlogException(BlogConstants.VipOrderNotFound);
        }
        if (!payOrder.getUserId().equals(userId)) {
            throw new BlogException(BlogConstants.VipOrderAccessDenied);
        }
        // 订单结果页轮询时，如果本地仍待支付则主动向支付宝补查一次状态。
        payOrder = refreshOrderStatusIfNecessary(payOrder);
        return toVipOrderVo(payOrder);
    }

    /**
     * 会员中心订单列表按创建时间倒序返回。
     */
    @Override
    public PageVo<List<VipOrderVo>> listCurrentUserOrders(Integer userId, Integer pageNum, Integer pageSize) {
        Page<PayOrder> page = new Page<>(pageNum, pageSize);
        // 订单列表严格限定当前用户，并按创建时间倒序展示最近支付动作。
        List<PayOrder> records = payOrderMapper.selectPage(page, new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getUserId, userId)
                .orderByDesc(PayOrder::getCreateTime)).getRecords();
        return new PageVo<>(records.stream().map(this::toVipOrderVo).toList(), page.getTotal());
    }

    /**
     * 只有异步回调才会发放会员资格，这里必须保持幂等。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayNotify(Map<String, String> params) {
        // 第一步必须验签，避免伪造回调直接触发会员发放。
        if (!alipayPaymentService.verifyNotify(params)) {
            throw new BlogException(BlogConstants.VipOrderNotifyVerifyError);
        }

        String orderNo = params.get("out_trade_no");
        // 第二步根据商户订单号回查本地订单，确保回调能映射到真实订单。
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo)
                .last("limit 1"));
        if (payOrder == null) {
            throw new BlogException(BlogConstants.VipOrderNotFound);
        }
        // 防重放校验：已支付订单（有 paidTime）直接返回成功，防止支付宝重复通知导致重复续期。
        if (payOrder.getPaidTime() != null) {
            log.info("订单重复回调，跳过处理，orderNo={}", orderNo);
            return true;
        }

        String totalAmount = params.get("total_amount");
        // 第三步校验金额，避免回调金额与本地下单金额不一致时误发会员。
        if (!fenToFixedYuan(payOrder.getAmountFen()).equals(totalAmount)) {
            throw new BlogException(BlogConstants.VipOrderAmountMismatch);
        }

        // 回调原文和支付宝交易字段一并落库，便于后续审计与问题排查。
        payOrder.setNotifyContent(JSON.toJSONString(params));
        payOrder.setAlipayTradeNo(params.get("trade_no"));
        payOrder.setBuyerId(params.get("buyer_id"));

        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 支付成功后统一走收口方法，保持“更新订单 + 发放会员”一处维护。
            markOrderPaid(payOrder, new Date());
            return true;
        }

        if ("TRADE_CLOSED".equals(tradeStatus)) {
            // 关闭订单只更新本地状态，不触发会员发放。
            payOrder.setStatus(PayOrderStatusEnum.CLOSED.getCode());
            payOrderMapper.updateById(payOrder);
            return true;
        }

        return true;
    }

    /**
     * 结果页轮询订单明细时，如果本地仍是待支付，则主动向支付宝补查一次。
     */
    private PayOrder refreshOrderStatusIfNecessary(PayOrder payOrder) {
        if (!PayOrderStatusEnum.PAYING.getCode().equals(payOrder.getStatus())) {
            return payOrder;
        }
        // 仅待支付订单需要主动补查，已终态订单直接返回本地状态即可。
        Map<String, String> tradeInfo = alipayPaymentService.queryTrade(payOrder.getOrderNo());
        if (tradeInfo.isEmpty()) {
            return payOrder;
        }
        String tradeStatus = tradeInfo.getOrDefault("tradeStatus", "");
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            payOrder.setAlipayTradeNo(tradeInfo.get("tradeNo"));
            payOrder.setBuyerId(tradeInfo.get("buyerId"));
            // 主动查询确认支付成功时，也要走统一的支付成功收口逻辑。
            markOrderPaid(payOrder, new Date());
            return payOrderMapper.selectById(payOrder.getId());
        }
        if ("TRADE_CLOSED".equals(tradeStatus)) {
            payOrder.setStatus(PayOrderStatusEnum.CLOSED.getCode());
            payOrder.setAlipayTradeNo(tradeInfo.get("tradeNo"));
            payOrder.setBuyerId(tradeInfo.get("buyerId"));
            payOrder.setNotifyContent(JSON.toJSONString(tradeInfo));
            payOrderMapper.updateById(payOrder);
            return payOrderMapper.selectById(payOrder.getId());
        }
        return payOrder;
    }

    /**
     * 统一收口支付成功后的订单更新与会员发放逻辑。
     */
    private void markOrderPaid(PayOrder payOrder, Date paidTime) {
        // 先把订单落成已支付，确保后续即使重复回调也能被幂等挡住。
        payOrder.setStatus(PayOrderStatusEnum.PAID.getCode());
        payOrder.setPaidTime(paidTime);
        if (payOrder.getNotifyContent() == null) {
            payOrder.setNotifyContent(JSON.toJSONString(Collections.singletonMap("source", "trade_query")));
        }
        payOrderMapper.updateById(payOrder);

        // 优先使用订单快照时长，兼容历史数据时再回查套餐表兜底。
        Integer planDays = payOrder.getPlanDays();
        if (planDays == null || planDays <= 0) {
            VipPlan plan = vipPlanService.getPlanByCode(payOrder.getPlanCode());
            if (plan == null) {
                throw new BlogException(BlogConstants.VipPlanNotFound);
            }
            planDays = plan.getDays();
        }
        // 订单确认支付后再发放会员资格，保证会员有效期始终能追溯到支付订单。
        vipMemberService.activateVip(payOrder.getUserId(), payOrder.getOrderNo(), planDays);
    }

    /**
     * 订单视图额外补齐元单位金额，减少前端重复换算。
     */
    private VipOrderVo toVipOrderVo(PayOrder payOrder) {
        VipOrderVo vipOrderVo = new VipOrderVo();
        vipOrderVo.setOrderNo(payOrder.getOrderNo());
        vipOrderVo.setBizType(payOrder.getBizType());
        vipOrderVo.setPlanCode(payOrder.getPlanCode());
        vipOrderVo.setPlanName(payOrder.getPlanName());
        vipOrderVo.setAmountFen(payOrder.getAmountFen());
        vipOrderVo.setPriceYuan(fenToYuan(payOrder.getAmountFen()));
        vipOrderVo.setStatus(payOrder.getStatus());
        vipOrderVo.setChannel(payOrder.getChannel());
        vipOrderVo.setClientType(payOrder.getClientType());
        vipOrderVo.setSubject(payOrder.getSubject());
        vipOrderVo.setAlipayTradeNo(payOrder.getAlipayTradeNo());
        vipOrderVo.setPaidTime(payOrder.getPaidTime());
        vipOrderVo.setExpiredTime(payOrder.getExpiredTime());
        vipOrderVo.setCreateTime(payOrder.getCreateTime());
        return vipOrderVo;
    }

    /**
     * 订单号使用时间戳加随机串，便于排查也能避免碰撞。
     */
    private String generateOrderNo() {
        return "VIP" + System.currentTimeMillis()
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private String fenToYuan(Integer amountFen) {
        return BigDecimal.valueOf(amountFen)
                .movePointLeft(2)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String fenToFixedYuan(Integer amountFen) {
        return BigDecimal.valueOf(amountFen)
                .movePointLeft(2)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
