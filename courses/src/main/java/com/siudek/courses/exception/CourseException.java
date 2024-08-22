package com.siudek.courses.exception;

public class CourseException extends RuntimeException{
    private CourseError courseError;

    public CourseException(CourseError courseError) {
        this.courseError = courseError;
    }

    public CourseError getCourseError() {return courseError;}
}

