package com.sidifensen.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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

    /**
     * 粉丝数
     */
    private Integer fansCount;

    /**
     * 关注数
     */
    private Integer followCount;

    // 角色信息
    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();

    // 菜单信息
    @TableField(exist = false)
    private List<SysMenu> sysMenus = new ArrayList<>();

    // 权限信息
    @TableField(exist = false)
    private List<SysPermission> sysPermissions = new ArrayList<>();

}
