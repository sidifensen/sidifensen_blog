package com.sidifensen.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**

 * @author sidifensen
 * @since 2025-08-17
 */
@Data
public class MessageDto implements Serializable {

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


}
