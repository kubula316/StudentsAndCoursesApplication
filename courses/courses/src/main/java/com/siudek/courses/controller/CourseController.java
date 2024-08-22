package com.siudek.courses.controller;


import com.siudek.courses.model.Course;
import com.siudek.courses.model.dto.StudentDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.siudek.courses.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {


    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) Course.Status status) {
        return courseService.getCourses(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course addCourse(@Valid @RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @GetMapping("/{code}")
    public Course getCourse(@PathVariable String code) {
        return courseService.getCourse(code);
    }

    @PostMapping("/{code}/student/{id}")
    public Course addCourseMember(@PathVariable String code, @PathVariable Long id){
        return courseService.addStudentToCourse(code, id);
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

}
