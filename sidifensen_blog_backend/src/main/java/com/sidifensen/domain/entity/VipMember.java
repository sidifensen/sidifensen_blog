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
 * VIP 会员实体，作为会员有效期的真实数据源。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vip_member")
public class VipMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 ID，关联用户表
     */
    private Integer userId;

    /**
     * 会员状态，参考 VipMemberStatusEnum 枚举
     */
    private String status;

    /**
     * 会员开始时间
     */
    private Date startTime;

    /**
     * 会员过期时间
     */
    private Date expireTime;

    /**
     * 最近一笔订单号，用于订单关联
     */
    private String lastOrderNo;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer isDeleted;
}
