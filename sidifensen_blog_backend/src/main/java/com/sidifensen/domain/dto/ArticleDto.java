package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ArticleDto implements Serializable {

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
     * 标签
     */
    private String tag;

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
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 可见范围 0-全部可见 1-仅我可见 2-粉丝可见 3-vip可见
     */
    @Min(value = 0, message = "可见范围错误")
    @Max(value = 3, message = "可见范围错误")
    private Integer visibleRange;

    /**
     * 转载类型 0-原创 1-转载
     */
    @Min(value = 0, message = "转载类型错误")
    @Max(value = 1, message = "转载类型错误")
    private Integer reprintType;

    /**
     * 转载链接
     */
    private String reprintUrl;

    /**
     * 分栏
     */
    private List<Integer> columnIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}