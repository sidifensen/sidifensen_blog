package com.sidifensen.domain.dto;

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
     * 功能模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

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
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
