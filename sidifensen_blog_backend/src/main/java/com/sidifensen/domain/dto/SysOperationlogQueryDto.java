package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志查询 DTO
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Data
@Accessors(chain = true)
public class SysOperationlogQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人员 ID
     */
    private Integer operatorId;

    /**
     * 操作人员角色
     */
    private String operatorRole;

    /**
     * 功能模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作状态 0-成功 1-失败 2-异常
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date createTimeStart;

    /**
     * 结束时间
     */
    private Date createTimeEnd;

    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于 1")
    @Max(value = 100, message = "每页大小不能超过 100")
    private Integer pageSize = 10;
}
