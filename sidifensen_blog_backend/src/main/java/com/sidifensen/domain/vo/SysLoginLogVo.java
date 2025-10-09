package com.sidifensen.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 登录日志VO
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Data
public class SysLoginLogVo {

    /**
     * 登录日志id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录方式 0-用户名/邮箱 1-gitee 2-github
     */
    private Integer loginType;

    /**
     * 登录方式描述
     */
    private String loginTypeDesc;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录地址
     */
    private String loginAddress;

    /**
     * 登录状态 0-成功 1-失败
     */
    private Integer status;

    /**
     * 登录状态描述
     */
    private String statusDesc;

    /**
     * 登录时间
     */
    private Date loginTime;
}

