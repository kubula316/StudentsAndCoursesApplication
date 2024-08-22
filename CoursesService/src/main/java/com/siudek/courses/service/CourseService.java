package com.siudek.courses.service;

import com.siudek.courses.model.Course;
import com.siudek.courses.model.dto.StudentDto;

import java.util.List;

public interface CourseService {
    List<Course> getCourses(Course.Status status);
    Course getCourse(String code);

    Course addCourse(Course course);

    void deleteCourse(String code);

    Course patchCourse(String code, Course course);

    Course putCourse(String code, Course course);

    Course addStudentToCourse(String code, Long id);

    List<StudentDto> getCourseMembers(String code);

    void finishEnroll(String code);
}
