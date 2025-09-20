package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 创作中心统计VO
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CreationStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章统计信息
     */
    private ArticleStatisticsVo articleStatistics;

    /**
     * 专栏总数
     */
    private Long columnCount;

    /**
     * 评论总数
     */
    private Long commentCount;

    /**
     * 总阅读量
     */
    private Long totalReadCount;

    /**
     * 总点赞数
     */
    private Long totalLikeCount;

    /**
     * 粉丝数
     */
    private Long fansCount;

    /**
     * 关注数
     */
    private Long followCount;

}
