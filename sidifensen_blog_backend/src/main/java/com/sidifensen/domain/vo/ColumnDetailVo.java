package com.sidifensen.domain.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 专栏详情视图对象
 *
 * @author sidifensen
 * @since 2025-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ColumnDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专栏id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 关注数
     */
    private Integer focusCount;

    /**
     * 文章数
     */
    private Integer articleCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 专栏内的文章列表
     */
    private List<ColumnArticleVo> articles;
}
