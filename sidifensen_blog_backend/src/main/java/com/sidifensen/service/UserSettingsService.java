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

}
