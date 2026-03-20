package com.sidifensen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单超时消息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderExpireMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Integer userId;
}