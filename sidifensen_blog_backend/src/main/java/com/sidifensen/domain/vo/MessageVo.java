package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-17
 */
@Data
public class MessageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    private Integer id;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 消息类型 0-系统 1-评论 2-点赞 3-收藏 4-关注
     */
    private Integer type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送消息的用户id
     */
    private Integer senderId;

    /**
     * 接收消息的用户id
     */
    private Integer receiverId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
