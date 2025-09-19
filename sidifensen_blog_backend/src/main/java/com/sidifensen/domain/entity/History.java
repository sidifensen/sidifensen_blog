package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史实体类
 * 用于记录用户或访客对文章的浏览历史，防止重复增加阅读量
 * 
 * @author sidifensen
 * @since 2025-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("history")
public class History implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Integer articleId;

    /**
     * 用户ID（登录用户）
     */
    private Integer userId;

    /**
     * 访客IP地址（未登录用户）
     */
    private String ipAddress;

    /**
     * 浏览器指纹（用于访客识别，已包含user-agent等信息）
     */
    private String fingerprint;

    /**
     * 浏览时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date viewTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer isDeleted;
}
