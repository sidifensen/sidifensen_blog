package com.sidifensen.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 访客日志VO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
public class SysVisitorLogVo {

    /**
     * 访客日志id
     */
    private Integer id;

    /**
     * 访客用户ID（登录用户）
     */
    private Integer userId;

    /**
     * 用户名（可能为空，游客访问）
     */
    private String username;

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

