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
     * 图片url
     */
    private String url;

    /**
     * 相册id
     */
    private Integer albumId;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 创建时间
     */
    private Date createTime;

}
