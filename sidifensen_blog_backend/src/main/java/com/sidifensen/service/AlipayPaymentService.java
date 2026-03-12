package com.sidifensen.service;

import com.sidifensen.domain.entity.PayOrder;

import java.util.Map;

/**
 * 支付宝支付能力抽象，负责下单参数生成和回调验签。
 */
public interface AlipayPaymentService {

    /**
     * 生成 PC 端电脑网站支付表单。
     */
    String createPagePayForm(PayOrder payOrder);

    /**
     * 生成 H5 端手机网站支付内容。
     */
    String createWapPayContent(PayOrder payOrder);

    /**
     * 验证支付宝异步回调签名。
     */
    boolean verifyNotify(Map<String, String> params);

    /**
     * 主动向支付宝查询交易状态，用于补偿异步回调丢失的场景。
     */
    Map<String, String> queryTrade(String orderNo);
}
