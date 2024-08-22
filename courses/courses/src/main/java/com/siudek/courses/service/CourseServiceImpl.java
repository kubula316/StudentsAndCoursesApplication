package com.siudek.courses.service;

import com.siudek.courses.exception.CourseError;
import com.siudek.courses.exception.CourseException;
import com.siudek.courses.model.Course;
import com.siudek.courses.model.CourseMember;
import com.siudek.courses.model.dto.NotificationInfoDto;
import com.siudek.courses.model.dto.StudentDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.siudek.courses.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;
    public CourseServiceImpl(CourseRepository courseRepository, StudentServiceClient studentServiceClient, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public List<Course> getCourses(Course.Status status) {
        if (status == null){
            return courseRepository.findAll();
        }
        return courseRepository.findAllByStatus(status);
    }

    @Override
    public Course getCourse(String code) {
        return courseRepository.findById(code).orElseThrow(()-> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course addCourse(Course course) {
        course.validateCourse();
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String code) {
        if (courseRepository.existsById(code)){
            courseRepository.deleteById(code);
        }else {
            throw new CourseException(CourseError.COURSE_NOT_FOUND);
        }

    }

    @Override
    public Course patchCourse(String code, Course course) {
        course.validateCourse();
        return courseRepository.findById(code).map(courseFromDB ->{
            if (!StringUtils.isEmpty(course.getDescription())){
                courseFromDB.setDescription(course.getDescription());
            }
            if (!StringUtils.isEmpty(course.getName())){
                courseFromDB.setName(course.getName());
            }
            if (!StringUtils .isEmpty(course.getStartDate())){
                courseFromDB.setStartDate(course.getStartDate());
            }
            if (!StringUtils.isEmpty(course.getStartDate())){
                courseFromDB.setStartDate(course.getStartDate());
            }
            if (!StringUtils.isEmpty(course.getEndDate())){
                courseFromDB.setEndDate(course.getEndDate());
            }
            if (!StringUtils.isEmpty(course.getParticipantsLimit())){
                courseFromDB.setParticipantsLimit(course.getParticipantsLimit());
            }
            if (!StringUtils.isEmpty(course.getParticipantsNumber())){
                courseFromDB.setParticipantsNumber(course.getParticipantsNumber());
            }
            return courseRepository.save(courseFromDB);
        }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course putCourse(String code, Course course) {
        course.validateCourse();
        return courseRepository.findById(code).map(CourseFromDB ->{
            if (!StringUtils.isEmpty(course.getCode())) {
                CourseFromDB.setCode(course.getCode());
            }
            if (!StringUtils.isEmpty(course.getStatus())) {
                CourseFromDB.setStatus(course.getStatus());
            }if (!StringUtils.isEmpty(course.getDescription())) {
                CourseFromDB.setDescription(course.getDescription());
            }
            if (!StringUtils.isEmpty(course.getName())) {
                CourseFromDB.setName(course.getName());
            }
            if (!StringUtils.isEmpty(course.getStartDate())) {
                CourseFromDB.setStartDate(course.getStartDate());
            }
            if (!StringUtils.isEmpty(course.getEndDate())) {
                CourseFromDB.setEndDate(course.getEndDate());
            }
            if (!StringUtils.isEmpty(course.getParticipantsLimit())) {
                CourseFromDB.setParticipantsLimit(course.getParticipantsLimit());
            }
            if (!StringUtils.isEmpty(course.getParticipantsNumber())) {
                CourseFromDB.setParticipantsNumber(course.getParticipantsNumber());
            }
            return courseRepository.save(CourseFromDB);
        }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course addStudentToCourse(String code, Long id) {
        StudentDto student = studentServiceClient.getStudent(id);

        return courseRepository.findById(code).map(CourseFromDb ->{
            CourseFromDb.validateCourseIsActive();
            CourseFromDb.validateParticipantStatus(student);
            CourseFromDb.validateSignedParticipants(student);
            CourseFromDb.getParticipants().add(new CourseMember(student.getEmail()));
            CourseFromDb.incrementParticipants();
            return courseRepository.save(CourseFromDb);
        }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public List<StudentDto> getCourseMembers(String code) {
        Course course = courseRepository.findById(code).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
        List <@NotNull String> emailMemebers = getCourseMembersEmails(course);
        return studentServiceClient.getStudentsByEmail(emailMemebers);

    }

    @Override
    public void finishEnroll(String code) {
        Course course = courseRepository.findById(code).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
        if (course.getStatus().equals(Course.Status.INACTIVE)){
            throw new CourseException(CourseError.COURSE_IS_ALREADY_INACTIVE);
        }
        course.setStatus(Course.Status.INACTIVE);
        courseRepository.save(course);
        SendNotificationToRabbitMq(course);
    }

    private void SendNotificationToRabbitMq(Course course) {
        List <@NotNull String> emailMemebers = getCourseMembersEmails(course);
        NotificationInfoDto notificationInfo = NotificationInfoDto.builder()
                .courseCode(course.getCode())
                .courseDescription(course.getDescription())
                .courseNAme(course.getName())
                .courseEndDate(course.getEndDate())
                .courseStartDate(course.getStartDate())
                .emails(emailMemebers)
                .build();
        rabbitTemplate.convertAndSend("enroll_finish", notificationInfo);
    }

    private static List<String> getCourseMembersEmails(Course course) {
        return course.getParticipants().stream().map(CourseMember::getEmail).toList();
    }


}
