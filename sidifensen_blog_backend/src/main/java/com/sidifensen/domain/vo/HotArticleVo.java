package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 热门文章视图对象（精简版）
 * 只包含热门文章列表所需的核心字段：ID、标题、总阅读量、热度分数
 * 
 * @author sidifensen
 * @since 2025-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HotArticleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 热度分数（近7天访问量）
     */
    private Long hotScore;

}

