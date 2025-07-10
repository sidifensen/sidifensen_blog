package com.sidifensen;

import cn.hutool.core.date.DateTime;
import com.sidifensen.domain.enums.MailEnum;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.utils.EmailUtil;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Map;


/**
 * @author sidifensen
 * @since 2025-07-08
 */
@SpringBootTest
public class EmailTest {
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private EmailUtil emailUtil;

    @Test
    public void test() throws Exception {

        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        // 创建 MimeMessageHelper
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        // 发件人邮箱和名称
        helper.setFrom("1848221808@qq.com", "sidifensen");
        // 收件人邮箱
        helper.setTo("sidifensen@163.com");
        // 邮件标题
        helper.setSubject("Hello");
        // 邮件正文，第二个参数表示是否是HTML正文
        helper.setText("Hello <strong> World</strong>！", true);

        // 发送
        javaMailSender.send(message);
    }

    @Test
    public void test2()  {
        System.out.println("开始发送邮件：" + new DateTime());//开始发送邮件：2025-07-09 17:10:30
        emailUtil.sendEmail("sidifensen@163.com", "Hello", "Hello <strong> World</strong>！");
        System.out.println("结束发送邮件：" + new DateTime());//结束发送邮件：2025-07-09 17:10:40
    }

    @Test
    public void test3() {
        emailUtil.sendEmail("sidifensen@163.com", "测试邮件", "这是一封测试邮件");
    }
    @Test
    public void testQQMail() {
        emailUtil.sendHtmlMail("1848221808@qq.com", MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", 123456));
    }
    @Test
    public void test163Mail() {
        emailUtil.sendHtmlMail("sidifensen@163.com", MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", 123456));
    }
    @Test
    public void testGMail() {
        emailUtil.sendHtmlMail("sidifensen16@gmail.com", MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", 123456));
    }

//    @Test
//    public void testOutlook() {
//        emailUtil.sendHtmlMail("outlook_e4c1e56080f8e76c@outlook.com", MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", 123456));
//    }


    @Test
    public void testResetPassword() {
        emailUtil.sendHtmlMail("sidifensen@163.com", MailEnum.RESET.getSubject(), MailEnum.RESET.getTemplateName(), Map.of("checkCode", 123456));

    }
}
