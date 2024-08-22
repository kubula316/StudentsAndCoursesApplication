package com.siudek.notification.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Message {
    private String message;

    public Message(String courseName) {
        this.message = "Kurs " + courseName + " Właśnie się rozpoczął!";
    }
}
