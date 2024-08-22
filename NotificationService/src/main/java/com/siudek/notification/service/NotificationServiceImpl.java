package com.siudek.notification.service;

import com.siudek.notification.model.Message;
import com.siudek.notification.model.dto.NotificationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService{
    private final RabbitTemplate rabbitTemplate;
    private final JavaMailSender javaMailSender;

    public NotificationServiceImpl(RabbitTemplate rabbitTemplate, JavaMailSender javaMailSender) {
        this.rabbitTemplate = rabbitTemplate;
        this.javaMailSender = javaMailSender;
    }


    @Override
    @RabbitListener(queues = "enroll_finish")
    public void listenMessage(NotificationDto notification) {
        sendEmails(notification);
    }


    @Override
    public void sendEmail(String to, String title, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(title);
        mimeMessageHelper.setText(content, false);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendEmails(NotificationDto notification) {
        String title = "Kurs Rozpoczęty!";
        StringBuilder builder = new StringBuilder();
        builder.append("Kurs ");
        builder.append(notification.getCourseCode());
        builder.append(" rozpoczyna się: ");
        builder.append(notification.getCourseStartDate());
        builder.append("\n");
        builder.append("Opis kursu: ");
        builder.append("\n");
        builder.append(notification.getCourseDescription());
        builder.append("\n");
        builder.append("Przewidywana data ukończenia kursu: ");
        builder.append(notification.getCourseEndDate());
        builder.append("\n");
        builder.append("Czekamy na ciebie!");
        notification.getEmails().forEach(email-> {
            try {
                sendEmail(email, title, builder.toString());
            } catch (MessagingException e) {
                log.error("notyfikacja się nie wysłała" + e);
            }
        });
    }
}
