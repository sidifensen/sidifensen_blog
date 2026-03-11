package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 登录日志查询DTO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
public class SysLoginLogQueryDto {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 登录方式 0-用户名/邮箱 1-gitee 2-github
     */
    private Integer loginType;

    /**
     * 登录状态 0-成功 1-失败
     */
    private Integer status;

    /**
     * 登录时间开始
     */
    private Date loginTimeStart;

    /**
     * 登录时间结束
     */
    private Date loginTimeEnd;

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

