package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端套餐列表展示对象。
 */
@Data
public class VipAdminPlanVo implements Serializable {

    private Integer id;

    private String code;

    private String name;

    private Integer days;

    private Integer priceFen;

    private String priceYuan;

    private Boolean enabled;

    private String description;

    private Date createTime;

    private Date updateTime;
}
