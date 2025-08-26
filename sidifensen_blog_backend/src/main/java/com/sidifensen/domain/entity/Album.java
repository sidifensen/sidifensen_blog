package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
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
