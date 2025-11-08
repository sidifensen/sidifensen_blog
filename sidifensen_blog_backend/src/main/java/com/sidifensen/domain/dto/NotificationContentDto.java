package com.sidifensen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通知内容DTO
 * 用于封装通知消息的详细信息，将以JSON格式存储在Message表的content字段中
 *
 * @author sidifensen
 * @since 2025-11-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationContentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知标题（消息的实际内容）
     */
    private String title;

    /**
     * 发送者用户ID
     */
    private Integer userId;

    /**
     * 发送者昵称
     */
    private String nickname;

    /**
     * 发送者头像URL
     */
    private String avatar;

    /**
     * 文章ID（如果是文章相关的通知）
     */
    private Integer articleId;

    /**
     * 文章标题（如果是文章相关的通知）
     */
    private String articleTitle;

    /**
     * 文章作者ID（如果是文章相关的通知，用于跳转到正确的文章页面）
     */
    private Integer authorId;

    /**
     * 评论ID（如果是评论相关的通知）
     */
    private Integer commentId;

    /**
     * 评论内容（如果是评论相关的通知）
     */
    private String commentContent;
}
