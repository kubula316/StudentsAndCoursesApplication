package com.siudek.courses.repository;

import com.siudek.courses.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findAllByStatus(Course.Status status);

    List<Course> findAllByCodeIn(List<String> codes);

    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'tags': { $in: [?0] } } ] }")
    List<Course> findCoursesByNameOrTags(String searchTerm);

    List<Course> findAllByCategory(String category);
}
