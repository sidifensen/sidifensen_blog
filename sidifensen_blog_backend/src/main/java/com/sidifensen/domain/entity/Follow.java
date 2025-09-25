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
 * @since 2025-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关注者用户id
     */
    private Integer followerId;

    /**
     * 被关注者用户id
     */
    private Integer followedId;

    /**
     * 关注时间
     */
    private LocalDateTime createTime;


}
