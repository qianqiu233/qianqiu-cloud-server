package com.qianqiu.clouddisk.utils;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class MailUtil {
    @Value("${spring.mail.username}")
    private String username;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送简单邮件
     * @param to
     * @param subject
     * @param context
     */
    public void sendSampleMail(String to, String subject, String context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(context);
        mailSender.send(message);
    }

    /**
     * 发送一个复杂的邮件
     * @param to
     * @param subject
     * @param context
     * @param attachmentName
     * @param filePath
     * @throws Exception
     */
    public void sendAttachmentMail(String to, String subject, String context, String attachmentName, String filePath) throws Exception {
        //创建一个复杂的消息邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(context);

        //上传文件
        helper.addAttachment(attachmentName, new File(filePath));
        mailSender.send(mimeMessage);
    }
}
