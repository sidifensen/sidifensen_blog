package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;

/**
 * 互动趋势单日数据 VO
 * 用于展示评论数、点赞数、收藏数的每日趋势
 *
 * @author sidifensen
 * @since 2026-03-29
 */
@Data
@Accessors(chain = true)
public class InteractionTrendVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 日期，格式：2026-03-22 */
    private String date;

    /** 当日评论数 */
    private Integer commentCount;

    /** 当日点赞数 */
    private Integer likeCount;

    /** 当日收藏数 */
    private Integer favoriteCount;
}
