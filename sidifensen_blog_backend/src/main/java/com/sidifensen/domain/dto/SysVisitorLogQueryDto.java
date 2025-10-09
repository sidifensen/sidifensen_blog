package com.sidifensen.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 访客日志查询DTO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
public class SysVisitorLogQueryDto {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 访客IP地址
     */
    private String ip;

    /**
     * 设备类型（PC/Mobile）
     */
    private String device;

    /**
     * 访问时间开始
     */
    private Date visitTimeStart;

    /**
     * 访问时间结束
     */
    private Date visitTimeEnd;
}

