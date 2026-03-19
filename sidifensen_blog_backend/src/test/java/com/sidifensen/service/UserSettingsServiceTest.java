package com.sidifensen.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.UserSettings;
import com.sidifensen.mapper.UserSettingsMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 用户设置服务测试
 */
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.rabbitmq.host=none",
    "spring.rabbitmq.port=12345",
    "spring.rabbitmq.username=test",
    "spring.rabbitmq.password=test",
    "spring.data.redis.host=none",
    "spring.data.redis.port=12345",
    "rabbitmq.listener.enabled=false"
})
public class UserSettingsServiceTest {

    @Resource
    private UserSettingsService userSettingsService;

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Test
    public void testCreateDefaultSettings() {
        // 创建一个测试用户
        Integer testUserId = 99999;

        // 调用创建默认设置
        userSettingsService.createDefaultSettings(testUserId);

        // 验证设置已创建
        UserSettings settings = userSettingsService.getSettings(testUserId);

        assertNotNull(settings);
        assertEquals(testUserId, settings.getUserId());
        assertEquals(0, settings.getReceivePrivateMessageEmail());
        assertEquals(0, settings.getReceiveCommentEmail());
        assertEquals(0, settings.getReceiveSystemEmail());
    }

    @Test
    public void testCreateDefaultSettings_Duplicate_Call() {
        // 测试重复调用不会创建多条记录
        Integer testUserId = 99998;

        // 第一次创建
        userSettingsService.createDefaultSettings(testUserId);

        // 第二次调用（应该被跳过）
        userSettingsService.createDefaultSettings(testUserId);

        // 验证只有一条记录
        long count = userSettingsMapper.selectCount(
            new LambdaQueryWrapper<UserSettings>()
                .eq(UserSettings::getUserId, testUserId)
        );

        assertEquals(1, count);
    }
}
