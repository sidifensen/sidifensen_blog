package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员友链视图对象
 *
 * @author sidifensen
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdminLinkVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 友链id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站地址
     */
    private String url;

    /**
     * 网站封面
     */
    private String coverUrl;

    /**
     * 网站描述
     */
    private String description;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 网站邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

}
