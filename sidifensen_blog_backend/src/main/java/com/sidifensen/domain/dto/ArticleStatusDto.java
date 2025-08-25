package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
public class ArticleStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

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
     * 排序 0-时间排序 1-阅读量排序
     */
    private Integer orderBy;

    /**
     * 创建时间
     */
    private Date createTime;


}