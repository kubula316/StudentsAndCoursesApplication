package com.siudek.notification.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {
    @NotBlank
    @Email
    private String to;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
