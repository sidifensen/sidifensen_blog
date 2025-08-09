package com.sidifensen.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
public class SysRolePermissionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id列表
     */
    private List<Integer> roleIds;

    /**
     * 权限id
     */
    private Integer permissionId;


}
