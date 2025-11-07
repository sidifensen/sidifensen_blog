package com.sidifensen.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 私信 VO
 */
@Data
public class PrivateMessageVo {

    private Integer id;

    private Integer fromUserId;

    private String fromUserNickname;

    private String fromUserAvatar;

    private Integer toUserId;

    private String toUserNickname;

    private String toUserAvatar;

    private String content;

    private Integer messageType;

    private String imageUrl;

    private Integer isRead;

    private Integer isRevoked;

    private Date readTime;

    private Date createTime;
}
