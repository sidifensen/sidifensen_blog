package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史实体类
 * 用于记录登录用户对文章的浏览历史，防止重复增加阅读量
 * 注：访客浏览记录仅存储在Redis缓存中，不存储到数据库
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
     * 浏览时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date viewTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer isDeleted;
}
