package com.siudek.courses.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document
public class Lecture implements Comparable<Lecture>{

    @Id
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private String title;

    private String videoUrl;

    private String description;

    private List<String> materialsUrls = new ArrayList<>();

    private int position;


    @Override
    public int compareTo(Lecture other) {
        int positionComparison = Integer.compare(this.position, other.position);
        return positionComparison != 0 ? positionComparison : this.title.compareToIgnoreCase(other.title);
    }
}
