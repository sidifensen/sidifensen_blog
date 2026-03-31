package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 公告表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("announcement")
public class Announcement {

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 发送方式(JSON数组): system=系统通知(指定用户走message表), website=公告(全部用户走announcement表), email=邮件
     */
    private String sendMethod;

    /**
     * 发送对象：1-全部用户 2-指定用户
     */
    private Integer targetType;

    /**
     * 发送对象id(JSON数组)
     */
    private String targetUsers;

    /**
     * 状态：0-待发送 1-发送中 2-已发送 3-发送失败
     */
    private Integer status;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;
}
