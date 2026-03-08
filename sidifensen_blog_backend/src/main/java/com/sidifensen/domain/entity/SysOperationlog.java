package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体类
 * 用于记录后台管理员和查看者的操作行为
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_operationlog")
public class SysOperationlog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 操作人员 id
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
     * 操作 ip
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
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除 0-正常 1-删除
     */
    @TableLogic
    private Integer isDeleted;
}
