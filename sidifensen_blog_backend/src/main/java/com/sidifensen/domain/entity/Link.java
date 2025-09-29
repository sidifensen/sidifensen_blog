package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("link")
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 友链id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站地址
     */
    private String url;

    /**
     * 网站封面
     */
    private String coverUrl;

    /**
     * 网站描述
     */
    private String description;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 网站邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;


}
