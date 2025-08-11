package com.sidifensen.domain.dto;

import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysPermission;
import com.sidifensen.domain.entity.SysRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
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
public class SysUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别 0-男 1-女
     */
    private Integer sex;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 0-正常 1-禁用
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

    /**
     * 注册方式 0-用户名/邮箱 1-gitee 2-github 3-QQ
     */
    private Integer registerType;


    /**
     * 登录方式 0-用户名/邮箱 1-gitee 2-github 3-QQ
     */
    private Integer loginType;


    // 角色信息
    private List<SysRole> sysRoles = new ArrayList<>();

    // 菜单信息
    private List<SysMenu> sysMenus = new ArrayList<>();

    // 权限信息
    private List<SysPermission> sysPermissions = new ArrayList<>();


}
