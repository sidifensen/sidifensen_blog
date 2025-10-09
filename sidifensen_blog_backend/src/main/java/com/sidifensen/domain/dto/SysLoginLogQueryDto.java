package com.sidifensen.domain.dto;

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
}

