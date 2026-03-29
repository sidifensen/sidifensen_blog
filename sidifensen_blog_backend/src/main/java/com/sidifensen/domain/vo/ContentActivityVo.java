package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;

/**
 * 内容活跃度 VO
 *
 * @author sidifensen
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
public class ContentActivityVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 模块名称：文章、评论、相册、收藏、私信 */
    private String item;

    /** 活跃度评分（0-100） */
    private Integer score;
}
