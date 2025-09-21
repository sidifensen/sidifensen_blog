package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户专栏管理视图对象
 * 
 * @author sidifensen
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserColumnManageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专栏ID
     */
    private Integer id;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    private Integer showStatus;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 关注数
     */
    private Integer focusCount;

    /**
     * 文章数
     */
    private Integer articleCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
