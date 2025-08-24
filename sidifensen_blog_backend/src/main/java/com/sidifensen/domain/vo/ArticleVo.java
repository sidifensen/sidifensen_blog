package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ArticleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 标签名称
     */
    private List<String> tag;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面url
     */
    private String coverUrl;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏量
     */
    private Integer collectCount;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 编辑状态 0-已发布 1-草稿箱 2-回收站
     */
    private Integer editStatus;

    /**
     * 可见范围 0-全部可见 1-仅我可见 2-粉丝可见 3-vip可见
     */
    private Integer visibleRange;

    /**
     * 转载类型 0-原创 1-转载
     */
    private Integer reprintType;

    /**
     * 转载链接
     */
    private String reprintUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
