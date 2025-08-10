package com.sidifensen.domain.vo;

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
 * @since 2025-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermissionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限id
     */
    private Integer id;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 权限
     */
    private String permission;

    /**
     * 菜单id
     */
    private Integer menuId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 对应的菜单名称
     */
    private String menuName;

    /**
     * 对应的菜单图标
     */
    private String icon;


}
