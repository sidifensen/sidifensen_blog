package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 待审核数量统计 VO
 *
 * @author sidifensen
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
public class ExamineCountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 待审核文章数
     */
    private Long articleCount;

    /**
     * 待审核评论数
     */
    private Long commentCount;

    /**
     * 待审核图片数
     */
    private Long photoCount;

}
