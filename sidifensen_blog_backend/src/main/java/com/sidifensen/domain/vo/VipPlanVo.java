package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * VIP 套餐展示对象。
 */
@Data
public class VipPlanVo implements Serializable {

    private String code;

    private String name;

    private Integer days;

    private Integer priceFen;

    private String priceYuan;

    private String description;
}
