package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户评论管理视图对象
 * 
 * @author sidifensen
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserCommentManageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Integer id;

    /**
     * 回复的用户ID
     */
    private Integer replyUserId;

    /**
     * 回复的用户昵称
     */
    private String replyUserNickname;

    /**
     * 回复的用户头像
     */
    private String replyUserAvatar;

    /**
     * 回复的评论内容
     */
    private String replyCommentContent;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章封面
     */
    private String articleCoverUrl;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章ID
     */
    private Integer articleId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

}
