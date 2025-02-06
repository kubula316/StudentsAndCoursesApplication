package com.siudek.courses.service;

import com.siudek.courses.model.Course;
import com.siudek.courses.model.Lecture;
import com.siudek.courses.model.dto.CourseDto;
import com.siudek.courses.model.dto.StudentDto;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<Course> getCourses(Course.Status status, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);
    Course getCourse(String code, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    Course addCourse(Course course, String containerName, MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    void deleteCourse(String code);

    Course patchCourse(String code, Course course);

    Course putCourse(String code, Course course);

    Course addStudentToCourse(String code, Long id, String authHeader);

    List<StudentDto> getCourseMembers(String code);

    void finishEnroll(String code);

    List<CourseDto> getCoursesProjections(Course.Status status, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    List<Course> getCoursesByCodes(List<String> savedList, String token);

    List<CourseDto> findCoursesByNameOrTags(String searchTerm, String token);

    List<CourseDto> findCoursesByCategory(String category, String token);

    List<Course> addSectionToCourse(String courseId, String sectionName, int position ,String token);

    List<Course> addLectureToCourse(String courseId, String sectionName, Lecture lecture, MultipartFile video, String containerName, String token);

    void removeStudentFromCourse(String code, String email, String authHeader);
}
