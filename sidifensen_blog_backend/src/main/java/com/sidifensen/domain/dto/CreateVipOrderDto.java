package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建 VIP 订单的请求参数。
 */
@Data
public class CreateVipOrderDto implements Serializable {

    @NotBlank(message = "套餐编码不能为空")
    private String planCode;

    @NotBlank(message = "客户端类型不能为空")
    private String clientType;
}
