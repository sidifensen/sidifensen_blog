package com.sidifensen.utils;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 邮件发送工具类
 */
@Component
@Slf4j
public class EmailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

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


}