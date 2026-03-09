package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志列表 VO（精简版）
 * 用于列表展示，只包含必要字段
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Data
@Accessors(chain = true)
public class SysOperationlogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 操作人员 ID
     */
    private Integer operatorId;

    /**
     * 操作人员名字
     */
    private String operatorName;

    /**
     * 操作人员角色
     */
    private String operatorRole;

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
     * 请求方式 (get/post/put/delete)
     */
    private String requestMethod;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 操作 IP
     */
    private String ip;

    /**
     * 操作地址
     */
    private String address;

    /**
     * 操作状态 0-成功 1-失败 2-异常
     */
    private Integer status;

    /**
     * 消耗时间 (ms)
     */
    private Long time;

    /**
     * 创建时间
     */
    private Date createTime;
}
