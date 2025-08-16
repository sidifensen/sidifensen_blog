package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PhotoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 图片url
     */
    private String url;

    /**
     * 审核状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer examineStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户名称
     */
    private String username;

}
