package com.siudek.courses.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Section implements Comparable<Section>{

    @NotBlank
    private String title;

    private int position;

    private List<Lecture> lessons = new ArrayList<>();

    public Section(@NotNull String title, int position) {
        this.title = title;
        this.position = position;
    }

    @Override
    public int compareTo(Section other) {
        int positionComparison = Integer.compare(this.position, other.position);
        return positionComparison != 0 ? positionComparison : this.title.compareToIgnoreCase(other.title);
    }
}
