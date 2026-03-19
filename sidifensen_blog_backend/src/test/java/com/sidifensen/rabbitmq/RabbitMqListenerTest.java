package com.sidifensen.rabbitmq;

import com.sidifensen.utils.EmailUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RabbitMQ 监听器单元测试
 */
@ExtendWith(MockitoExtension.class)
class RabbitMqListenerTest {

    @InjectMocks
    private RabbitMqListener rabbitMqListener;

    @Mock
    private EmailUtils emailUtils;

    @BeforeEach
    void setUp() {
        // 设置必要的配置值
        ReflectionTestUtils.setField(rabbitMqListener, "frontendUserHost", "http://localhost:3000");
        ReflectionTestUtils.setField(rabbitMqListener, "frontendAdminHost", "http://localhost:3001");
        reset(emailUtils);
    }

    @Test
    @DisplayName("处理评论邮件 - 成功发送")
    void testHandleCommentNotificationEmail() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "recipientNickname", "testUser",
                "commenterNickname", "commenterUser",
                "articleTitle", "测试文章",
                "articleId", 123,
                "commentContent", "这是一条评论内容",
                "isReply", false,
                "parentCommentContent", "",
                "type", "commentNotification"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        ArgumentCaptor<Map> templateVarsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 评论通知"),
                eq("comment-notification"),
                templateVarsCaptor.capture()
        );

        Map<String, Object> capturedVars = templateVarsCaptor.getValue();
        assertEquals("testUser", capturedVars.get("recipientNickname"));
        assertEquals("commenterUser", capturedVars.get("commenterNickname"));
        assertEquals("测试文章", capturedVars.get("articleTitle"));
        assertEquals(123, capturedVars.get("articleId"));
        assertEquals("这是一条评论内容", capturedVars.get("commentContent"));
        assertEquals(false, capturedVars.get("isReply"));
    }

    @Test
    @DisplayName("处理评论邮件 - 回复评论场景")
    void testHandleCommentNotificationEmail_Reply() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "recipientNickname", "testUser",
                "commenterNickname", "commenterUser",
                "articleTitle", "测试文章",
                "articleId", 123,
                "commentContent", "这是回复内容",
                "isReply", true,
                "parentCommentContent", "这是原评论",
                "type", "commentNotification"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        ArgumentCaptor<Map> templateVarsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 评论通知"),
                eq("comment-notification"),
                templateVarsCaptor.capture()
        );

        Map<String, Object> capturedVars = templateVarsCaptor.getValue();
        assertEquals(true, capturedVars.get("isReply"));
        assertEquals("这是原评论", capturedVars.get("parentCommentContent"));
    }

    @Test
    @DisplayName("处理系统邮件 - 成功发送")
    void testHandleSystemNotificationEmail() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "recipientNickname", "testUser",
                "notificationTitle", "系统通知",
                "notificationContent", "这是通知内容",
                "extraInfo", "附加信息",
                "sendTime", "2026-03-19 10:00:00",
                "linkUrl", "http://localhost:3000/article/123",
                "linkText", "查看详情",
                "type", "systemNotification"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        ArgumentCaptor<Map> templateVarsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 系统通知"),
                eq("system-notification"),
                templateVarsCaptor.capture()
        );

        Map<String, Object> capturedVars = templateVarsCaptor.getValue();
        assertEquals("testUser", capturedVars.get("recipientNickname"));
        assertEquals("系统通知", capturedVars.get("notificationTitle"));
        assertEquals("这是通知内容", capturedVars.get("notificationContent"));
        assertEquals("附加信息", capturedVars.get("extraInfo"));
        assertEquals("2026-03-19 10:00:00", capturedVars.get("sendTime"));
        assertEquals("http://localhost:3000/article/123", capturedVars.get("linkUrl"));
        assertEquals("查看详情", capturedVars.get("linkText"));
    }

    @Test
    @DisplayName("处理系统邮件 - 空值处理")
    void testHandleSystemNotificationEmail_NullValues() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "recipientNickname", "testUser",
                "notificationTitle", "系统通知",
                "notificationContent", "这是通知内容",
                "type", "systemNotification"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        ArgumentCaptor<Map> templateVarsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 系统通知"),
                eq("system-notification"),
                templateVarsCaptor.capture()
        );

        Map<String, Object> capturedVars = templateVarsCaptor.getValue();
        assertEquals("", capturedVars.get("extraInfo"));
        assertEquals("", capturedVars.get("sendTime"));
        assertEquals("", capturedVars.get("linkUrl"));
        assertEquals("", capturedVars.get("linkText"));
    }

    @Test
    @DisplayName("注册邮件 - 成功发送")
    void testReceiveEmail_Register() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "checkCode", "123456",
                "type", "register"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("欢迎注册 sidifensen_blog"),
                eq("register"),
                anyMap()
        );
    }

    @Test
    @DisplayName("重置密码邮件 - 成功发送")
    void testReceiveEmail_ResetPassword() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "checkCode", "654321",
                "type", "resetPassword"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 重置密码"),
                eq("reset-password"),
                anyMap()
        );
    }

    @Test
    @DisplayName("重置邮箱邮件 - 成功发送")
    void testReceiveEmail_ResetEmail() {
        // Arrange
        Map<String, Object> message = Map.of(
                "email", "test@example.com",
                "checkCode", "111222",
                "type", "resetEmail"
        );

        // Act
        rabbitMqListener.receiveEmail(message);

        // Assert
        verify(emailUtils).sendHtmlMail(
                eq("test@example.com"),
                eq("sidifensen_blog 重置邮箱"),
                eq("reset-email"),
                anyMap()
        );
    }
}
