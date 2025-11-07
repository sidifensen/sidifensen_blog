package com.sidifensen.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 会话 VO
 */
@Data
public class ConversationVo {

    private Integer id;

    private Integer userId;

    private Integer targetUserId;

    private String targetUserNickname;

    private String targetUserAvatar;

    private String lastMessageContent;

    private Date lastMessageTime;

    private Integer unreadCount;

    private Boolean isOnline; // 对方是否在线
}
