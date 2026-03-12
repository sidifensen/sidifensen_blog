package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端会员订单分页查询参数。
 */
@Data
public class VipAdminOrderPageDto implements Serializable {

    @Min(value = 1, message = "页码不能为空")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页大小不能为空")
    private Integer pageSize = 10;

    /**
     * 订单号筛选。
     */
    private String orderNo;

    /**
     * 用户关键词，支持用户ID、用户名、昵称、邮箱。
     */
    private String userKeyword;

    /**
     * 套餐编码。
     */
    private String planCode;

    /**
     * 订单状态。
     */
    private String status;

    /**
     * 支付渠道。
     */
    private String channel;

    /**
     * 客户端类型。
     */
    private String clientType;

    /**
     * 订单创建时间开始。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    /**
     * 订单创建时间结束。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;
}
