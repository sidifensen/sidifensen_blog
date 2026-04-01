package com.sidifensen.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    private Date createTime;

    private Date updateTime;
}
