package com.sidifensen.domain.vo;

import com.sidifensen.domain.entity.SysMenu;
import com.sidifensen.domain.entity.SysPermission;
import com.sidifensen.domain.entity.SysRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-11
 */
@Data
public class SysUserDetailVo {

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
    private Integer status;

    /**
     * 注册方式 0-用户名/邮箱 1-gitee 2-github 3-QQ
     */
    private Integer registerType;

    /**
     * 注册ip
     */
    private String registerIp;

    /**
     * 注册地址
     */
    private String registerAddress;

    /**
     * 登录方式 0-用户名/邮箱 1-gitee 2-github 3-QQ
     */
    private Integer loginType;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录地址
     */
    private String loginAddress;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    // 角色信息
    private List<SysRole> sysRoles = new ArrayList<>();

    // 菜单信息
    private List<SysMenu> sysMenus = new ArrayList<>();

    // 权限信息
    private List<SysPermission> sysPermissions = new ArrayList<>();


}
