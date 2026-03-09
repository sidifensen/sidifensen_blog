package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志 VO
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Data
@Accessors(chain = true)
public class SysOperationlogVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 功能模块
     */
    private String module;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 请求方法 (类名：方法名)
     */
    private String method;

    /**
     * 请求方式 (get/post/put/delete)
     */
    private String requestMethod;

    /**
     * 请求 url
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回结果
     */
    private String responseResult;

    /**
     * 操作人员 ID
     */
    private Integer operatorId;

    /**
     * 操作人员角色
     */
    private String operatorRole;

    /**
     * 操作人员名字
     */
    private String operatorName;

    /**
     * 操作 IP
     */
    private String ip;

    /**
     * 操作地址
     */
    private String address;

    /**
     * 消耗时间 (ms)
     */
    private Long time;

    /**
     * 操作状态 0-成功 1-失败 2-异常
     */
    private Integer status;

    /**
     * 异常消息
     */
    private String exception;

    /**
     * 创建时间
     */
    private Date createTime;
}
