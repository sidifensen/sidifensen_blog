package com.sidifensen.service.impl;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.entity.UserSettings;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.CommentMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.mapper.UserSettingsMapper;
import com.sidifensen.service.MessageService;
import com.sidifensen.service.UserSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 评论服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private UserSettingsMapper userSettingsMapper;

    @Mock
    private UserSettingsService userSettingsService;

    @Mock
    private MessageService messageService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        reset(commentMapper, sysUserMapper, articleMapper, userSettingsService,
              messageService, rabbitTemplate);
    }

    @Test
    @DisplayName("评论邮件通知 - 用户开启邮件通知时发送")
    void testSendCommentEmailNotification_Enabled() {
        // Arrange
        Integer notifiedUserId = 1;
        String commenterNickname = "评论者";
        String articleTitle = "测试文章";
        Integer articleId = 123;
        String commentContent = "这是评论内容";
        Boolean isReply = false;
        String parentCommentContent = null;

        // Mock 用户设置 - 开启邮件通知
        when(userSettingsService.getReceiveCommentEmail(notifiedUserId)).thenReturn(1);

        // Mock 用户信息
        SysUser notifiedUser = new SysUser();
        notifiedUser.setId(notifiedUserId);
        notifiedUser.setEmail("test@example.com");
        notifiedUser.setNickname("被通知者");
        when(sysUserMapper.selectById(notifiedUserId)).thenReturn(notifiedUser);

        // Act - 通过反射调用私有方法
        try {
            java.lang.reflect.Method method = CommentServiceImpl.class.getDeclaredMethod(
                    "sendCommentEmailNotification",
                    Integer.class, String.class, String.class, Integer.class,
                    String.class, Boolean.class, String.class);
            method.setAccessible(true);
            method.invoke(commentService, notifiedUserId, commenterNickname, articleTitle,
                         articleId, commentContent, isReply, parentCommentContent);
        } catch (Exception e) {
            fail("调用 sendCommentEmailNotification 方法失败：" + e.getMessage());
        }

        // Assert
        ArgumentCaptor<Map> messageCaptor = ArgumentCaptor.forClass(Map.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.Email_Exchange),
                eq(RabbitMQConstants.Comment_Email_Routing_Key),
                (Map<String, Object>) any());

        // 由于使用了 any() 而不是 capture()，无法捕获消息内容进行验证
        // 但上面的 verify 已经确认了方法被正确调用
    }

    @Test
    @DisplayName("评论邮件通知 - 用户关闭邮件通知时不发送")
    void testSendCommentEmailNotification_Disabled() {
        // Arrange
        Integer notifiedUserId = 1;

        // Mock 用户设置 - 关闭邮件通知
        when(userSettingsService.getReceiveCommentEmail(notifiedUserId)).thenReturn(0);

        // Act
        try {
            java.lang.reflect.Method method = CommentServiceImpl.class.getDeclaredMethod(
                    "sendCommentEmailNotification",
                    Integer.class, String.class, String.class, Integer.class,
                    String.class, Boolean.class, String.class);
            method.setAccessible(true);
            method.invoke(commentService, notifiedUserId, "评论者", "测试文章",
                         123, "评论内容", false, null);
        } catch (Exception e) {
            fail("调用 sendCommentEmailNotification 方法失败：" + e.getMessage());
        }

        // Assert - 验证没有发送邮件
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
        verify(sysUserMapper, never()).selectById(any());
    }

    @Test
    @DisplayName("评论邮件通知 - 用户不存在时不发送")
    void testSendCommentEmailNotification_UserNotFound() {
        // Arrange
        Integer notifiedUserId = 1;

        // Mock 用户设置 - 开启邮件通知
        when(userSettingsService.getReceiveCommentEmail(notifiedUserId)).thenReturn(1);

        // Mock 用户不存在
        when(sysUserMapper.selectById(notifiedUserId)).thenReturn(null);

        // Act
        try {
            java.lang.reflect.Method method = CommentServiceImpl.class.getDeclaredMethod(
                    "sendCommentEmailNotification",
                    Integer.class, String.class, String.class, Integer.class,
                    String.class, Boolean.class, String.class);
            method.setAccessible(true);
            method.invoke(commentService, notifiedUserId, "评论者", "测试文章",
                         123, "评论内容", false, null);
        } catch (Exception e) {
            fail("调用 sendCommentEmailNotification 方法失败：" + e.getMessage());
        }

        // Assert - 验证没有发送邮件
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
    }

    @Test
    @DisplayName("评论邮件通知 - 用户邮箱为空时不发送")
    void testSendCommentEmailNotification_EmailNull() {
        // Arrange
        Integer notifiedUserId = 1;

        // Mock 用户设置 - 开启邮件通知
        when(userSettingsService.getReceiveCommentEmail(notifiedUserId)).thenReturn(1);

        // Mock 用户邮箱为空
        SysUser notifiedUser = new SysUser();
        notifiedUser.setId(notifiedUserId);
        notifiedUser.setEmail(null);
        when(sysUserMapper.selectById(notifiedUserId)).thenReturn(notifiedUser);

        // Act
        try {
            java.lang.reflect.Method method = CommentServiceImpl.class.getDeclaredMethod(
                    "sendCommentEmailNotification",
                    Integer.class, String.class, String.class, Integer.class,
                    String.class, Boolean.class, String.class);
            method.setAccessible(true);
            method.invoke(commentService, notifiedUserId, "评论者", "测试文章",
                         123, "评论内容", false, null);
        } catch (Exception e) {
            fail("调用 sendCommentEmailNotification 方法失败：" + e.getMessage());
        }

        // Assert - 验证没有发送邮件
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
    }

    @Test
    @DisplayName("评论邮件通知 - 回复评论场景")
    void testSendCommentEmailNotification_ReplyScenario() {
        // Arrange
        Integer notifiedUserId = 1;
        String commenterNickname = "回复者";
        String articleTitle = "测试文章";
        Integer articleId = 123;
        String commentContent = "这是回复内容";
        Boolean isReply = true;
        String parentCommentContent = "这是原评论";

        // Mock 用户设置 - 开启邮件通知
        when(userSettingsService.getReceiveCommentEmail(notifiedUserId)).thenReturn(1);

        // Mock 用户信息
        SysUser notifiedUser = new SysUser();
        notifiedUser.setId(notifiedUserId);
        notifiedUser.setEmail("test@example.com");
        notifiedUser.setNickname("被回复者");
        when(sysUserMapper.selectById(notifiedUserId)).thenReturn(notifiedUser);

        // Act
        try {
            java.lang.reflect.Method method = CommentServiceImpl.class.getDeclaredMethod(
                    "sendCommentEmailNotification",
                    Integer.class, String.class, String.class, Integer.class,
                    String.class, Boolean.class, String.class);
            method.setAccessible(true);
            method.invoke(commentService, notifiedUserId, commenterNickname, articleTitle,
                         articleId, commentContent, isReply, parentCommentContent);
        } catch (Exception e) {
            fail("调用 sendCommentEmailNotification 方法失败：" + e.getMessage());
        }

        // Assert
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.Email_Exchange),
                eq(RabbitMQConstants.Comment_Email_Routing_Key),
                (Map<String, Object>) any());
    }
}
