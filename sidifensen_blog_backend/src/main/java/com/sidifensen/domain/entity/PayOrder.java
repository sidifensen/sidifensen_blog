package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付订单实体，对应支付宝会员开通订单。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pay_order")
public class PayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 平台订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 业务类型 VIP_SUBSCRIPTION(会员开通)
     */
    private String bizType;

    /**
     * 套餐编码
     */
    private String planCode;

    /**
     * 套餐名称
     */
    private String planName;

    /**
     * 套餐时长(天)
     */
    private Integer planDays;

    /**
     * 订单金额(分)
     */
    private Integer amountFen;

    /**
     * 订单状态 CREATED/CREATED(已创建)/PAYING(待支付)/PAID(已支付)/CLOSED(已关闭)/FAILED(失败)
     */
    private String status;

    /**
     * 支付渠道 ALIPAY(支付宝)
     */
    private String channel;

    /**
     * 客户端类型 PC/H5
     */
    private String clientType;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 支付宝交易号
     */
    private String alipayTradeNo;

    /**
     * 支付宝买家id
     */
    private String buyerId;

    /**
     * 支付时间
     */
    private Date paidTime;

    /**
     * 订单过期时间
     */
    private Date expiredTime;

    /**
     * 异步回调原始内容
     */
    private String notifyContent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}
