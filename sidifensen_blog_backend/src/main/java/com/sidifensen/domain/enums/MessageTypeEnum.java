package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author sidifensen
 * @since 2025-08-14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MessageTypeEnum {
    //0-系统 1-评论 2-点赞 3-收藏 4-关注

    SYSTEM(0, "系统消息"),
    COMMENT(1, "评论消息"),
    LIKE(2, "点赞消息"),
    COLLECT(3, "收藏消息"),
    FOLLOW(4, "关注消息");

    private int code;
    private String desc;

}
