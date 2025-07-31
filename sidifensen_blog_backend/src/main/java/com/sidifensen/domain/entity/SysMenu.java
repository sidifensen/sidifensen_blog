package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2025-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 类型 0-目录 1-菜单 2-按钮
     */
    private Integer type;

    /**
     * 路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态 0-正常 1-禁用
     */
    private Integer status;

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
