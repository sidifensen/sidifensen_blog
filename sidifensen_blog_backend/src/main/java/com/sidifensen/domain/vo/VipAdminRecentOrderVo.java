package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端会员详情中的最近订单摘要。
 */
@Data
public class VipAdminRecentOrderVo implements Serializable {

    private String orderNo;

    private String planCode;

    private String planName;

    private Integer amountFen;

    private String priceYuan;

    private String status;

    private String channel;

    private String clientType;

    private Date paidTime;

    private Date createTime;
}
