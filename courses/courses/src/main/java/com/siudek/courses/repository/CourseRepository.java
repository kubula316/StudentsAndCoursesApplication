package com.siudek.courses.repository;

import com.siudek.courses.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findAllByStatus(Course.Status status);
}
