package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.UserSettings;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.UserSettingsMapper;
import com.sidifensen.service.UserSettingsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户个人设置服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@Service
@Slf4j
public class UserSettingsServiceImpl extends ServiceImpl<UserSettingsMapper, UserSettings> implements UserSettingsService {

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Override
    public UserSettings getSettings(Integer userId) {
        LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSettings::getUserId, userId);
        return userSettingsMapper.selectOne(wrapper);
    }

    @Override
    public Integer getReceivePrivateMessageEmail(Integer userId) {
        UserSettings settings = getSettings(userId);
        if (settings == null) {
            // 如果没有设置记录，返回默认值 0（关闭）
            return 0;
        }
        return settings.getReceivePrivateMessageEmail() != null ? settings.getReceivePrivateMessageEmail() : 0;
    }

    @Override
    public void setReceivePrivateMessageEmail(Integer userId, Integer isReceive) {
        UserSettings settings = getSettings(userId);

        if (settings == null) {
            // 创建新记录
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setReceivePrivateMessageEmail(isReceive);
            settings.setReceiveCommentEmail(1);
            settings.setReceiveSystemEmail(1);
            userSettingsMapper.insert(settings);
            log.info("创建用户设置记录：userId={}, receivePrivateMessageEmail={}", userId, isReceive);
        } else {
            // 更新现有记录
            settings.setReceivePrivateMessageEmail(isReceive);
            userSettingsMapper.updateById(settings);
            log.info("更新用户设置：userId={}, receivePrivateMessageEmail={}", userId, isReceive);
        }
    }

}
