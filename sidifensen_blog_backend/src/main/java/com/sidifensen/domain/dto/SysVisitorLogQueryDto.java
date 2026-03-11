package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小不能小于 1")
    @Max(value = 100, message = "每页大小不能超过 100")
    private Integer pageSize;
}

