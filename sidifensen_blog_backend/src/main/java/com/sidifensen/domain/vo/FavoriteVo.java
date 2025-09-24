package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 收藏夹视图对象
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FavoriteVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 展示状态 0-公开 1-私密
     */
    private Integer showStatus;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 当前文章是否被该收藏夹收藏（仅在通过文章ID查询收藏夹列表时有效）
     */
    private Boolean isCollected;

}
