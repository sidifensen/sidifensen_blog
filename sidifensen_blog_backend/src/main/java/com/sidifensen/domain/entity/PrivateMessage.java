package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 私信实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("private_message")
public class PrivateMessage implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer fromUserId;

    private Integer toUserId;

    private String content;

    private Integer messageType;

    private String imageUrl;

    private Integer isRead;

    private Integer isRevoked;

    @TableField(fill = FieldFill.UPDATE)
    private Date readTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer isDeleted;
}
