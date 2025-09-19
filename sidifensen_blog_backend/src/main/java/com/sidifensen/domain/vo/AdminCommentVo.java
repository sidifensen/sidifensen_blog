package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员评论视图对象
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdminCommentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论id
     */
    private Integer id;

    /**
     * 父级评论id
     */
    private Integer parentId;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 评论用户id
     */
    private Integer userId;

    /**
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像
     */
    private String avatar;

    /**
     * 回复的用户id
     */
    private Integer replyUserId;

    /**
     * 回复的用户昵称
     */
    private String replyUserNickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private Date createTime;

}
