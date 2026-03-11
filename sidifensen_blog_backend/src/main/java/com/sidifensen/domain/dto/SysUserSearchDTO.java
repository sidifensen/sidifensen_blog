package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Date;

/**
 * @author sidifensen
 * @since 2025-08-11
 */
@Data
public class SysUserSearchDTO {

    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常 1-禁用
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

    // 创建时间开始
    private Date createTimeStart;

    // 创建时间结束
    private Date createTimeEnd;
}
