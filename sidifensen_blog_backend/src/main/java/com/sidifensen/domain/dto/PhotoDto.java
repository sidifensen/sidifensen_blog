package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Data
public class PhotoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

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
     * 图片url
     */
    private String url;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    // 创建时间开始
    private Date createTimeStart;

    // 创建时间结束
    private Date createTimeEnd;

}
