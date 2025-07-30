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
@TableName("album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 相册id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册描述
     */
    private String description;

    /**
     * 相册封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私有
     */
    private Integer showStatus;

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
