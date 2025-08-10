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
 * @since 2025-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUserRoleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer roleId;

    /**
     * 用户id
     */
    private List<Integer> userIds;

    /**
     * 角色id
     */
    private List<Integer> roleIds;


}
