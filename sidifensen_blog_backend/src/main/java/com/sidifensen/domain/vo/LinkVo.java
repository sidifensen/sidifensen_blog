package com.sidifensen.domain.vo;

import lombok.Data;

/**
 * 友链展示对象
 *
 * @author sidifensen
 * @since 2025-09-29
 */
@Data
public class LinkVo {

    /**
     * 友链id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

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
}
