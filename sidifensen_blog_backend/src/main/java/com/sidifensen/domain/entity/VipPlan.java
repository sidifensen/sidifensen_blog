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
 * VIP 套餐实体，作为会员价格和时长的真实数据源。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vip_plan")
public class VipPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 套餐编码，唯一标识，如 "monthly"、"yearly"
     */
    private String code;

    /**
     * 套餐名称，如 "月度会员"、"年度会员"
     */
    private String name;

    /**
     * 套餐天数，如 30、365
     */
    private Integer days;

    /**
     * 套餐价格，单位为分
     */
    private Integer priceFen;

    /**
     * 套餐是否启用
     */
    private Boolean enabled;

    /**
     * 套餐描述
     */
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer isDeleted;
}
