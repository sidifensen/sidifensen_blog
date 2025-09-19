package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章收藏关联表
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("article_favorite")
public class ArticleFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 被收藏的文章id
     */
    private Integer articleId;

    /**
     * 收藏夹id
     */
    private Integer favoriteId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;


}
