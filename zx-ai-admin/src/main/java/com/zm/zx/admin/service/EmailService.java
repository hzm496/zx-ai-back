package com.zm.zx.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;
    
    /**
     * 发送简单文本邮件
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("邮件发送成功：to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("邮件发送失败：to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送反馈回复邮件
     */
    public void sendFeedbackReplyEmail(String to, String username, String feedbackContent, String replyContent) {
        String subject = "【智学AI伴侣】您的反馈已收到回复";
        
        String content = String.format(
            "尊敬的 %s，您好！\n\n" +
            "感谢您对智学AI伴侣的反馈，我们已经查看了您的问题并做出了回复。\n\n" +
            "您的反馈内容：\n%s\n\n" +
            "我们的回复：\n%s\n\n" +
            "如有其他问题，欢迎随时联系我们。\n\n" +
            "此致\n" +
            "智学AI伴侣团队\n" +
            "联系邮箱：%s",
            username,
            feedbackContent,
            replyContent,
            from
        );
        
        sendSimpleEmail(to, subject, content);
    }
}

