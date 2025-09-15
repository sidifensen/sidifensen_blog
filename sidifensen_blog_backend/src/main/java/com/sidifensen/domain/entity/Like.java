package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
 * @author
 * @since 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`like`")
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /**
     * 类型 0-文章 1-评论
     */
    @Min(value = 0, message = "类型错误")
    @Max(value = 1, message = "类型错误")
    @NotNull(message = "类型不能为空")
    private Integer type;

    /**
     * 类型id
     */
    @NotNull(message = "类型id不能为空")
    private Integer typeId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
