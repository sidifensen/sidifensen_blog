package com.sidifensen.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告 VO
 */
@Data
public class AnnouncementVo {

    private Integer id;

    private String title;

    private String content;

    private String sendMethod;

    private Integer targetType;

    private String targetUsers;

    private Integer status;

    private LocalDateTime sendTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
