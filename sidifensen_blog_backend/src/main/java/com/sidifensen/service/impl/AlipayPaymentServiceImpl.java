package com.sidifensen.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.sidifensen.config.VipProperties;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.exception.BlogException;
import com.sidifensen.service.AlipayPaymentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付实现，负责组装网页支付参数与回调验签。
 */
@Service
@Slf4j
public class AlipayPaymentServiceImpl implements AlipayPaymentService {

    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private VipProperties vipProperties;

    /**
     * 电脑站支付返回的是一段可直接提交的 HTML 表单。
     */
    @Override
    public String createPagePayForm(PayOrder payOrder) {
        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(getRequiredAlipay().getNotifyUrl());
            request.setReturnUrl(getRequiredAlipay().getReturnUrlPc());
            request.setBizContent(JSON.toJSONString(buildBizContent(payOrder, "FAST_INSTANT_TRADE_PAY")));
            AlipayTradePagePayResponse response = createClient().pageExecute(request);
            return response.getBody();
        } catch (Exception e) {
            log.error("创建支付宝PC订单失败，orderNo={}", payOrder.getOrderNo(), e);
            throw new BlogException(BlogConstants.VipOrderCreateError);
        }
    }

    /**
     * 手机站支付同样复用 pageExecute，返回 H5 可跳转内容。
     */
    @Override
    public String createWapPayContent(PayOrder payOrder) {
        try {
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            request.setNotifyUrl(getRequiredAlipay().getNotifyUrl());
            request.setReturnUrl(getRequiredAlipay().getReturnUrlH5());
            request.setBizContent(JSON.toJSONString(buildBizContent(payOrder, "QUICK_WAP_WAY")));
            AlipayTradeWapPayResponse response = createClient().pageExecute(request);
            return response.getBody();
        } catch (Exception e) {
            log.error("创建支付宝H5订单失败，orderNo={}", payOrder.getOrderNo(), e);
            throw new BlogException(BlogConstants.VipOrderCreateError);
        }
    }

    /**
     * 支付宝异步回调必须先验签，再进入业务处理。
     */
    @Override
    public boolean verifyNotify(Map<String, String> params) {
        try {
            VipProperties.Alipay alipay = getRequiredAlipay();
            // 使用支付宝公钥验证签名
            return AlipaySignature.rsaCheckV1(
                    params,                    // 回调参数
                    alipay.getAlipayPublicKey(), // 支付宝公钥
                    "UTF-8",                   // 编码格式
                    SIGN_TYPE                  // 签名类型：RSA2
            );
        } catch (Exception e) {
            log.error("支付宝回调验签异常", e);
            return false;
        }
    }

    /**
     * 异步通知丢失时，结果页可以主动向支付宝查单完成订单补偿。
     * 用于订单状态同步场景（如页面轮询、主动补查）。
     *
     * @param orderNo 商户订单号
     * @return 订单状态信息（tradeStatus、tradeNo、buyerId、sendPayDate），失败返回空 Map
     */
    @Override
    public Map<String, String> queryTrade(String orderNo) {
        try {
            // 构建支付宝交易查询请求
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            request.setBizContent(JSON.toJSONString(bizContent));

            // 执行查询请求
            AlipayTradeQueryResponse response = createClient().execute(request);

            // 判断响应是否成功，失败时记录警告日志并返回空结果
            if (response == null || !response.isSuccess()) {
                log.warn("支付宝主动查单失败，orderNo={}, subCode={}, subMsg={}",
                        orderNo,
                        response == null ? null : response.getSubCode(),
                        response == null ? null : response.getSubMsg());
                return Map.of();
            }

            // 提取查询结果中的关键字段
            Map<String, String> result = new HashMap<>();
            result.put("tradeStatus", response.getTradeStatus());         // 交易状态：TRADE_SUCCESS/TRADE_FINISHED/TRADE_CLOSED 等
            result.put("tradeNo", response.getTradeNo());                 // 支付宝交易号
            result.put("buyerId", response.getBuyerUserId());             // 买家支付宝用户 ID
            result.put("sendPayDate", response.getSendPayDate() == null ? null : response.getSendPayDate().toString()); // 支付时间
            return result;
        } catch (Exception e) {
            // 捕获所有异常，避免查单失败影响主流程
            log.error("支付宝主动查单异常，orderNo={}", orderNo, e);
            return Map.of();
        }
    }

    private AlipayClient createClient() {
        VipProperties.Alipay alipay = getRequiredAlipay();
        return new DefaultAlipayClient(
                alipay.getGatewayUrl(),
                alipay.getAppId(),
                alipay.getPrivateKey(),
                "json",
                "UTF-8",
                alipay.getAlipayPublicKey(),
                SIGN_TYPE
        );
    }

    /**
     * 支付宝金额单位使用元，这里把系统内的分统一转换过去。
     */
    private Map<String, Object> buildBizContent(PayOrder payOrder, String productCode) {
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", payOrder.getOrderNo());
        bizContent.put("total_amount", fenToYuan(payOrder.getAmountFen()));
        bizContent.put("subject", payOrder.getSubject());
        bizContent.put("product_code", productCode);
        bizContent.put("timeout_express", "15m");
        return bizContent;
    }

    private String fenToYuan(Integer amountFen) {
        return BigDecimal.valueOf(amountFen)
                .movePointLeft(2)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * 启动支付前先校验配置完整性，避免生成半残订单。
     */
    private VipProperties.Alipay getRequiredAlipay() {
        VipProperties.Alipay alipay = vipProperties.getAlipay();
        if (alipay == null
                || !StringUtils.hasText(alipay.getAppId())
                || !StringUtils.hasText(alipay.getPrivateKey())
                || !StringUtils.hasText(alipay.getAlipayPublicKey())
                || !StringUtils.hasText(alipay.getGatewayUrl())
                || !StringUtils.hasText(alipay.getNotifyUrl())) {
            throw new BlogException(BlogConstants.VipConfigMissing);
        }
        return alipay;
    }
}
