package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * VIP 订单详情展示对象。
 */
@Data
public class VipOrderVo implements Serializable {

    private String orderNo;

    private String bizType;

    private String planCode;

    private String planName;

    private Integer amountFen;

    private String priceYuan;

    private String status;

    private String channel;

    private String clientType;

    private String subject;

    private String alipayTradeNo;

    private Date paidTime;

    private Date expiredTime;

    private Date createTime;
}
