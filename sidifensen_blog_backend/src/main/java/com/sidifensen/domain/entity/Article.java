package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 标签
     */
    private String tag;

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
    @Min(value = 0, message = "审核状态错误")
    @Max(value = 2, message = "审核状态错误")
    private Integer examineStatus;

    /**
     * 编辑状态 0-已发布 1-草稿箱 2-回收站
     */
    @Min(value = 0, message = "编辑状态错误")
    @Max(value = 2, message = "编辑状态错误")
    private Integer editStatus;

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;


}
