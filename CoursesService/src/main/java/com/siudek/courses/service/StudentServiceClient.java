package com.siudek.courses.service;

import com.siudek.courses.model.dto.StudentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "STUDENT-SERVICE")
public interface StudentServiceClient {
    @GetMapping("/students/{id}")
    StudentDto getStudent(@PathVariable Long id);

    @PostMapping("/students/members")
    List<StudentDto> getStudentsByEmail(@RequestBody List<String> mailList);
}
