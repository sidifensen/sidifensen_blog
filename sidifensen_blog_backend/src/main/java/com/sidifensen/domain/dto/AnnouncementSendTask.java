package com.sidifensen.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 公告发送任务 DTO（RabbitMQ 消息体）
 */
@Data
public class AnnouncementSendTask {

    /**
     * announcement.id
     */
    private Integer announcementId;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发送方式列表: ["system", "website", "email"]
     */
    private List<String> sendMethods;

    /**
     * 发送对象：1-全部用户 2-指定用户
     */
    private Integer targetType;

    /**
     * 指定用户 ID 列表（targetType=2 时）
     */
    private List<Integer> targetUsers;

    /**
     * 游标翻页：上次处理的最大用户 ID，初始为 0
     */
    private Integer lastId = 0;
}
