package com.siudek.notification.service;

import com.siudek.notification.model.dto.NotificationDto;
import jakarta.mail.MessagingException;

public interface NotificationService {
    public void listenMessage(NotificationDto notification);

    void sendEmail(String to, String title, String content) throws MessagingException;

    void sendEmails(NotificationDto notification);
}
