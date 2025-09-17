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
 * 
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("favorite")
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    private LocalDateTime createTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;


}
