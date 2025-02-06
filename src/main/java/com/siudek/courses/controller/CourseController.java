package com.siudek.courses.controller;


import com.siudek.courses.model.Course;
import com.siudek.courses.model.Lecture;
import com.siudek.courses.model.dto.CourseDto;
import com.siudek.courses.model.dto.StudentDto;
import com.siudek.courses.service.CourseService;
import com.siudek.courses.storage.ImageStorageClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {


    private final CourseService courseService;
    private final ImageStorageClient imageStorageClient;

    public CourseController(CourseService courseService, ImageStorageClient imageStorageClient) {
        this.courseService = courseService;
        this.imageStorageClient = imageStorageClient;
    }

    @GetMapping("/projections")
    public List<CourseDto> getCoursesProjections(@RequestParam(required = false) Course.Status status, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
    return courseService.getCoursesProjections(status, authHeader);
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) Course.Status status, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return courseService.getCourses(status, authHeader);
    }

    @GetMapping("/search")
    public List<CourseDto> searchCourses(@RequestParam String searchTerm, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return courseService.findCoursesByNameOrTags(searchTerm, token);
    }

    @GetMapping("/search/category")
    public List<CourseDto> searchCoursesByCategory(@RequestParam String category, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return courseService.findCoursesByCategory(category, token);
    }

    @PostMapping("/add/section")
    public List<Course> addSectionToCourse(@RequestParam String courseId, @RequestParam String sectionName, @RequestParam int position ,@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return courseService.addSectionToCourse(courseId, sectionName, position ,token);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/add/lecture")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Course> addLectureToSection(@RequestParam String courseId, @RequestParam String sectionName, @Valid @RequestPart Lecture lecture,  @RequestPart MultipartFile video, @RequestParam String containerName, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return courseService.addLectureToCourse(courseId, sectionName, lecture, video, containerName, token);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Course addCourse(@Valid @RequestPart Course course, @RequestParam String containerName, @RequestPart MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return courseService.addCourse(course, containerName, file, authHeader);
    }

    @GetMapping("/{code}")
    public Course getCourse(@PathVariable String code, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return courseService.getCourse(code, authHeader);
    }

    @PostMapping("/{code}/student/{id}")
    public Course addCourseMember(@PathVariable String code, @PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        return courseService.addStudentToCourse(code, id, authHeader);
    }

    @DeleteMapping("/{code}/student/{email}")
    public void removeCourseMember(@PathVariable String code, @PathVariable String email, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        courseService.removeStudentFromCourse(code, email, authHeader);
    }

    @GetMapping("/{code}/members")
    public List<StudentDto> showCourseMembersInformation(@PathVariable String code){
        return courseService.getCourseMembers(code);
    }

    @PostMapping("/{code}/finish-enroll")
    public ResponseEntity<?> finishEnroll(@PathVariable String code){
        courseService.finishEnroll(code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/images")
    public String uploadImage(@RequestParam String containerName, @RequestParam MultipartFile file)throws IOException{
        try(InputStream inputStream = file.getInputStream()) {
            return this.imageStorageClient.uploadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
        }
    }

    @GetMapping("/savedCourses")
    public List<Course> savedCourses(@RequestParam List<String> savedList, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return courseService.getCoursesByCodes(savedList, token);
    }
}
