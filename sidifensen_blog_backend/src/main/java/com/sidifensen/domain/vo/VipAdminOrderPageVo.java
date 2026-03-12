package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端会员订单列表行数据。
 */
@Data
public class VipAdminOrderPageVo implements Serializable {

    private Integer userId;

    private String username;

    private String nickname;

    private String email;

    private String orderNo;

    private String planCode;

    private String planName;

    private Integer planDays;

    private Integer amountFen;

    private String priceYuan;

    private String status;

    private String channel;

    private String clientType;

    private Date paidTime;

    private Date createTime;
}
