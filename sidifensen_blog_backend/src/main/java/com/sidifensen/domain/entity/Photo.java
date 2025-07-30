package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("photo")
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 图片url
     */
    private String url;

    /**
     * 相册id
     */
    private Long albumId;

    /**
     * 展示状态 0-公开 1-私有
     */
    private Integer showStatus;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

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
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;


}
