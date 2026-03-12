package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建 VIP 订单后的前端支付载荷。
 */
@Data
public class VipOrderCreateVo implements Serializable {

    private String orderNo;

    private String clientType;

    private String formHtml;

    private String payUrl;
}
