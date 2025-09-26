package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 文章状态统计VO
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ArticleStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总数
     */
    private Long totalCount;

    /**
     * 已发布数量 (editStatus=0 && examineStatus=1)
     */
    private Long publishedCount;

    /**
     * 审核中数量 (editStatus=0 && examineStatus=0)
     */
    private Long reviewingCount;

    /**
     * 草稿箱数量 (editStatus=1)
     */
    private Long draftCount;

    /**
     * 回收站数量 (editStatus=2)
     */
    private Long garbageCount;

    /**
     * 总阅读量
     */
    private Long totalReadCount;

}
