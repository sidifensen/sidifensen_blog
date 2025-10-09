package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 访客记录实体类
 * 用于记录网站访客信息，支持按日去重统计
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_visitorlog")
public class SysVisitorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 访客用户ID（登录用户，未登录为null）
     */
    private Integer userId;

    /**
     * 访客IP地址
     */
    private String ip;

    /**
     * 访客地理位置
     */
    private String address;

    /**
     * 设备类型（PC/Mobile）
     */
    private String device;

    /**
     * 访问时间
     */
    private Date visitTime;
}

