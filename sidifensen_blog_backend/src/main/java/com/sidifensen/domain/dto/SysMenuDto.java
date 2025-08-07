package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class SysMenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单id
     */
    private Integer id;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态 0-正常 1-禁用
     */
    private Integer status;


}
