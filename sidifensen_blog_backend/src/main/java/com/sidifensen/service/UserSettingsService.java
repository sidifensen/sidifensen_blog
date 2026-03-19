package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.UserSettings;

/**
 * <p>
 * 用户个人设置服务接口
 * </p>
 *
 * @author sidifensen
 * @since 2026-03-18
 */
public interface UserSettingsService extends IService<UserSettings> {

    /**
     * 获取用户的设置
     *
     * @param userId 用户 ID
     * @return 用户设置对象
     */
    UserSettings getSettings(Integer userId);

    /**
     * 为新用户创建默认的个人设置
     * 所有邮件通知默认关闭
     *
     * @param userId 用户 ID
     */
    void createDefaultSettings(Integer userId);

    /**
     * 获取用户的私信邮件通知设置
     *
     * @param userId 用户 ID
     * @return 是否接收（0-关闭，1-开启）
     */
    Integer getReceivePrivateMessageEmail(Integer userId);

    /**
     * 更新用户的私信邮件通知设置
     *
     * @param userId 用户 ID
     * @param isReceive 是否接收（0-关闭，1-开启）
     */
    void setReceivePrivateMessageEmail(Integer userId, Integer isReceive);

    /**
     * 获取用户的评论邮件通知设置
     *
     * @param userId 用户 ID
     * @return 是否接收（0-关闭，1-开启），如果没有记录则返回默认值 1（开启）
     */
    Integer getReceiveCommentEmail(Integer userId);

    /**
     * 获取用户的系统邮件通知设置
     *
     * @param userId 用户 ID
     * @return 是否接收（0-关闭，1-开启），如果没有记录则返回默认值 1（开启）
     */
    Integer getReceiveSystemEmail(Integer userId);

}
