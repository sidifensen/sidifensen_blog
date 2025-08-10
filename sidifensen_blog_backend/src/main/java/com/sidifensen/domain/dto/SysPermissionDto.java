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
 * @since 2025-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermissionDto implements Serializable {

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

}
