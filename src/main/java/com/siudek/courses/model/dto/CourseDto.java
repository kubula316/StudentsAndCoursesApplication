package com.siudek.courses.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseDto {
    private String code;
    private String name;
    private String author;
    private Long participantsLimit;
    private Long participantsNumber;
    private String imageUrl;
}
