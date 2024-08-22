package com.siudek.notification.controller;

import com.siudek.notification.model.dto.EmailDto;
import com.siudek.notification.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid EmailDto email){
        try {
            notificationService.sendEmail(email.getTo(), email.getTitle(), email.getContent());
        } catch (MessagingException e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wiadomość sie nie wysłała");
        }
        return ResponseEntity.ok("Wiadomośc została wysłana do: " + email.getTo());
    }
}
