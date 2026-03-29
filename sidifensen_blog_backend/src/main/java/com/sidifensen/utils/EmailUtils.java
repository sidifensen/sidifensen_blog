package com.sidifensen.utils;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.UserSettingsService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件发送工具类
 */
@Component
@Slf4j
public class EmailUtils {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserSettingsService userSettingsService;

    @Resource
    private SysUserMapper sysUserMapper;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 发送HTML邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param templateName 邮件模板名称
     * @param data 邮件模板数据
     * @return
     */
    public void sendHtmlMail(String toEmail, String subject, String templateName, Map<String, Object> data)  {
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        // 创建 MimeMessageHelper 对象, 用于设置邮件内容
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom(username, "sidifensen");
            // 邮件正文
            Context context = new Context();
            // 给模板传递数据
            context.setVariables(data);
            // 解析Thymeleaf模板
            String htmlContent = templateEngine.process(templateName, context);
            // 设置邮件正文内容,第二个参数表示是否是HTML内容
            helper.setText(htmlContent, true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            // 处理异常
            log.error("发送邮件失败：{}", e.getMessage());
        }
    }

    /**
     * 发送HTML邮件
     * @param subject 邮件主题
     * @param templateName 邮件模板名称
     * @param data 邮件模板数据
     * @return
     */
    public void sendHtmlMailToAdmin(String subject, String templateName, Map<String, Object> data)  {
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        // 创建 MimeMessageHelper 对象, 用于设置邮件内容
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(username);
            helper.setSubject(subject);
            helper.setFrom(username, "sidifensen");
            // 邮件正文
            Context context = new Context();
            // 给模板传递数据
            context.setVariables(data);
            // 解析Thymeleaf模板
            String htmlContent = templateEngine.process(templateName, context);
            // 设置邮件正文内容,第二个参数表示是否是HTML内容
            helper.setText(htmlContent, true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            // 处理异常
            log.error("发送邮件失败：{}", e.getMessage());
        }
    }

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendEmail(String to, String subject, String content) {
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        // 创建 MimeMessageHelper
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, false);
            // 发件人邮箱和名称
            helper.setFrom(username, "sidifensen");
            // 收件人邮箱
            helper.setTo(to);
            // 邮件标题
            helper.setSubject(subject);
            // 邮件正文，第二个参数表示是否是HTML正文
            helper.setText(content, false);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 发送
        javaMailSender.send(message);
    }

    /**
     * 发送系统邮件通知到消息队列
     *
     * @param userId              用户ID
     * @param notificationTitle    通知标题
     * @param notificationContent  通知内容
     * @param extraInfo           额外信息
     * @param linkUrl             链接URL
     * @param linkText            链接文本
     */
    public void sendSystemEmailNotification(Integer userId, String notificationTitle, String notificationContent,
            String extraInfo, String linkUrl, String linkText) {
        try {
            // 检查用户是否开启了系统邮件通知
            Integer receiveSystemEmail = userSettingsService.getReceiveSystemEmail(userId);
            if (receiveSystemEmail == 0) {
                log.debug("用户 {} 关闭了系统邮件通知，跳过发送", userId);
                return;
            }

            // 查询用户邮箱
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null || user.getEmail() == null) {
                log.warn("发送系统邮件通知失败：用户 {} 不存在或邮箱为空", userId);
                return;
            }

            // 构建邮件消息
            HashMap<String, Object> emailMessage = new HashMap<>();
            emailMessage.put("email", user.getEmail());
            emailMessage.put("recipientNickname", user.getNickname());
            emailMessage.put("notificationTitle", notificationTitle);
            emailMessage.put("notificationContent", notificationContent);
            emailMessage.put("extraInfo", extraInfo != null ? extraInfo : "");
            emailMessage.put("sendTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            emailMessage.put("linkUrl", linkUrl != null ? linkUrl : "");
            emailMessage.put("linkText", linkText != null ? linkText : "");
            emailMessage.put("type", "systemNotification");

            // 发送邮件到队列
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.Email_Exchange,
                    RabbitMQConstants.System_Email_Routing_Key,
                    emailMessage);

            log.info("系统邮件通知已发送到队列：to={}, title={}", user.getEmail(), notificationTitle);
        } catch (Exception e) {
            log.error("发送系统邮件通知失败：userId={}", userId, e);
        }
    }


}