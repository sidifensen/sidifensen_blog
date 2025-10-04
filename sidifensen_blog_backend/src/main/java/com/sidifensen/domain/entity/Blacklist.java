package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.time.LocalDateTime;
import java.util.Date;
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
 * @since 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blacklist")
public class Blacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 黑名单id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 黑名单类型 0-用户 1-ip地址
     */
    private Integer type;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 拉黑时间
     */
    private Date banTime;

    /**
     * 到期时间
     */
    private Date expireTime;

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
