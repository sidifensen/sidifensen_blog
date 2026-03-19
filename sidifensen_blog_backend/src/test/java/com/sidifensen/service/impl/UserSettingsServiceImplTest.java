package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.UserSettings;
import com.sidifensen.mapper.UserSettingsMapper;
import com.sidifensen.service.UserSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户设置服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserSettingsServiceImplTest {

    @InjectMocks
    private UserSettingsServiceImpl userSettingsService;

    @Mock
    private UserSettingsMapper userSettingsMapper;

    @BeforeEach
    void setUp() {
        // 每个测试前重置 mock 行为
        reset(userSettingsMapper);
    }

    @Test
    @DisplayName("获取评论邮件设置 - 无记录时返回默认值 1")
    void testGetReceiveCommentEmail_NoRecord() {
        // Arrange
        when(userSettingsMapper.selectOne(any())).thenReturn(null);

        // Act
        Integer result = userSettingsService.getReceiveCommentEmail(999);

        // Assert
        assertEquals(1, result, "默认应该开启评论邮件通知");
        verify(userSettingsMapper).selectOne(any());
    }

    @Test
    @DisplayName("获取评论邮件设置 - 有记录时返回实际值")
    void testGetReceiveCommentEmail_WithRecord() {
        // Arrange
        UserSettings settings = new UserSettings();
        settings.setUserId(1);
        settings.setReceiveCommentEmail(0); // 关闭
        when(userSettingsMapper.selectOne(any())).thenReturn(settings);

        // Act
        Integer result = userSettingsService.getReceiveCommentEmail(1);

        // Assert
        assertEquals(0, result, "应该返回用户设置的值");
    }

    @Test
    @DisplayName("获取评论邮件设置 - 记录值为 null 时返回默认值 1")
    void testGetReceiveCommentEmail_NullValue() {
        // Arrange
        UserSettings settings = new UserSettings();
        settings.setUserId(1);
        settings.setReceiveCommentEmail(null);
        when(userSettingsMapper.selectOne(any())).thenReturn(settings);

        // Act
        Integer result = userSettingsService.getReceiveCommentEmail(1);

        // Assert
        assertEquals(1, result, "null 值应该返回默认值 1");
    }

    @Test
    @DisplayName("获取系统邮件设置 - 无记录时返回默认值 1")
    void testGetReceiveSystemEmail_NoRecord() {
        // Arrange
        when(userSettingsMapper.selectOne(any())).thenReturn(null);

        // Act
        Integer result = userSettingsService.getReceiveSystemEmail(999);

        // Assert
        assertEquals(1, result, "默认应该开启系统邮件通知");
        verify(userSettingsMapper).selectOne(any());
    }

    @Test
    @DisplayName("获取系统邮件设置 - 有记录时返回实际值")
    void testGetReceiveSystemEmail_WithRecord() {
        // Arrange
        UserSettings settings = new UserSettings();
        settings.setUserId(1);
        settings.setReceiveSystemEmail(0); // 关闭
        when(userSettingsMapper.selectOne(any())).thenReturn(settings);

        // Act
        Integer result = userSettingsService.getReceiveSystemEmail(1);

        // Assert
        assertEquals(0, result, "应该返回用户设置的值");
    }

    @Test
    @DisplayName("获取系统邮件设置 - 记录值为 null 时返回默认值 1")
    void testGetReceiveSystemEmail_NullValue() {
        // Arrange
        UserSettings settings = new UserSettings();
        settings.setUserId(1);
        settings.setReceiveSystemEmail(null);
        when(userSettingsMapper.selectOne(any())).thenReturn(settings);

        // Act
        Integer result = userSettingsService.getReceiveSystemEmail(1);

        // Assert
        assertEquals(1, result, "null 值应该返回默认值 1");
    }

    @Test
    @DisplayName("设置系统邮件设置 - 无记录时创建新记录")
    void testSetReceiveSystemEmail_CreateNew() {
        // Arrange
        when(userSettingsMapper.selectOne(any())).thenReturn(null);

        // Act
        userSettingsService.setReceiveSystemEmail(999, 1);

        // Assert
        ArgumentCaptor<UserSettings> captor = ArgumentCaptor.forClass(UserSettings.class);
        verify(userSettingsMapper).insert(captor.capture());
        UserSettings captured = captor.getValue();
        assertEquals(999, captured.getUserId());
        assertEquals(1, captured.getReceiveSystemEmail());
    }

    @Test
    @DisplayName("设置系统邮件设置 - 有记录时更新现有记录")
    void testSetReceiveSystemEmail_UpdateExisting() {
        // Arrange
        UserSettings existingSettings = new UserSettings();
        existingSettings.setUserId(1);
        existingSettings.setReceiveSystemEmail(0);
        when(userSettingsMapper.selectOne(any())).thenReturn(existingSettings);

        // Act
        userSettingsService.setReceiveSystemEmail(1, 1);

        // Assert
        ArgumentCaptor<UserSettings> captor = ArgumentCaptor.forClass(UserSettings.class);
        verify(userSettingsMapper).updateById(captor.capture());
        UserSettings captured = captor.getValue();
        assertEquals(1, captured.getUserId());
        assertEquals(1, captured.getReceiveSystemEmail());
    }

    @Test
    @DisplayName("获取私信邮件设置 - 无记录时返回默认值 0")
    void testGetReceivePrivateMessageEmail_NoRecord() {
        // Arrange
        when(userSettingsMapper.selectOne(any())).thenReturn(null);

        // Act
        Integer result = userSettingsService.getReceivePrivateMessageEmail(999);

        // Assert
        assertEquals(0, result, "默认应该关闭私信邮件通知");
    }

    @Test
    @DisplayName("获取私信邮件设置 - 有记录时返回实际值")
    void testGetReceivePrivateMessageEmail_WithRecord() {
        // Arrange
        UserSettings settings = new UserSettings();
        settings.setUserId(1);
        settings.setReceivePrivateMessageEmail(1); // 开启
        when(userSettingsMapper.selectOne(any())).thenReturn(settings);

        // Act
        Integer result = userSettingsService.getReceivePrivateMessageEmail(1);

        // Assert
        assertEquals(1, result, "应该返回用户设置的值");
    }
}
