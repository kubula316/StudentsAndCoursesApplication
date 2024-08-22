package com.siudek.courses.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class CourseMember {
    @NotBlank
    private String email;
    @NotBlank
    private LocalDateTime dateOfSign;

    public CourseMember(@NotNull String email) {
        this.email = email;
        this.dateOfSign = LocalDateTime.now();
    }
}
