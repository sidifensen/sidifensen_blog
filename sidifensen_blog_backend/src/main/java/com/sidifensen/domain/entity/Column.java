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
 * @since 2025-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`column`")
public class Column implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专栏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;

    /**
     * 关注数
     */
    private Integer focusCount;

    /**
     * 文章数
     */
    private Integer articleCount;

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
