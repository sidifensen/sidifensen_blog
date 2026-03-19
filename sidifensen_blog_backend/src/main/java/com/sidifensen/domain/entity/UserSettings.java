package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户个人设置实体类
 * 每个用户一条记录，每个设置项作为独立字段
 * </p>
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_settings")
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 是否接收私信邮件通知：0-关闭，1-开启
     */
    private Integer receivePrivateMessageEmail;

    /**
     * 是否接收评论邮件通知：0-关闭，1-开启
     */
    private Integer receiveCommentEmail;

    /**
     * 是否接收系统邮件通知：0-关闭，1-开启
     */
    private Integer receiveSystemEmail;

    /**
     * 是否接收私信邮件通知（别名，用于 JSON 序列化）
     * @return 是否接收私信邮件通知
     */
    public Integer getIsReceivePrivateMessageEmail() {
        return this.receivePrivateMessageEmail;
    }

    /**
     * 是否接收评论邮件通知（别名，用于 JSON 序列化）
     * @return 是否接收评论邮件通知
     */
    public Integer getIsReceiveCommentEmail() {
        return this.receiveCommentEmail;
    }

    /**
     * 是否接收系统邮件通知（别名，用于 JSON 序列化）
     * @return 是否接收系统邮件通知
     */
    public Integer getIsReceiveSystemEmail() {
        return this.receiveSystemEmail;
    }

}
