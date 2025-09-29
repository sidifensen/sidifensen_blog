package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史视图对象
 *
 * @author sidifensen
 * @since 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HistoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Integer articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章封面
     */
    private String coverUrl;

    /**
     * 文章作者ID
     */
    private Integer authorId;

    /**
     * 文章作者昵称
     */
    private String authorNickname;

    /**
     * 文章作者头像
     */
    private String authorAvatar;

    /**
     * 浏览时间
     */
    private Date viewTime;

}
