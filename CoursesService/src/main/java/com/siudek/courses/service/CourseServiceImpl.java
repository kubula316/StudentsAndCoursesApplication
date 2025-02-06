package com.siudek.courses.service;

import com.siudek.courses.exception.CourseError;
import com.siudek.courses.exception.CourseException;
import com.siudek.courses.model.Course;
import com.siudek.courses.model.CourseMember;
import com.siudek.courses.model.Lecture;
import com.siudek.courses.model.Section;
import com.siudek.courses.model.dto.CourseDto;
import com.siudek.courses.model.dto.NotificationInfoDto;
import com.siudek.courses.model.dto.StudentDto;
import com.siudek.courses.repository.CourseRepository;
import com.siudek.courses.security.AuthService;
import com.siudek.courses.storage.ImageStorageClient;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final ImageStorageClient imageStorageClient;
    private final AuthService authService;

    public CourseServiceImpl(CourseRepository courseRepository, StudentServiceClient studentServiceClient, RabbitTemplate rabbitTemplate, ImageStorageClient imageStorageClient, AuthService authService) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.imageStorageClient = imageStorageClient;
        this.authService = authService;
    }

    private void validateAcces(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CourseException(CourseError.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        try {
            boolean tokenIsValidated = !authService.validateToken();
        }catch (Exception e){
            throw new CourseException(CourseError.UNAUTHORIZED);
        }
        if (!authService.validateToken()){
            throw new CourseException(CourseError.UNAUTHORIZED);
        }
    }
    @Override
    public List<Course> getCourses(Course.Status status, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validateAcces(authHeader);
        //
        if (status == null){
            return courseRepository.findAll();
        }
        return courseRepository.findAllByStatus(status);
    }



    @Override
    public List<CourseDto> getCoursesProjections(Course.Status status, String authHeader) {
        validateAcces(authHeader);

        if (status == null){
            return courseRepository.findAll().stream().map(course ->
                new CourseDto(course.getCode(),
                        course.getName(),
                        course.getAuthor(),
                        course.getParticipantsLimit(),
                        course.getParticipantsNumber(),
                        course.getImageUrl()
                        ))
                    .collect(Collectors.toList());
        }
        return courseRepository.findAllByStatus(status).stream().map(course ->
                        new CourseDto(course.getCode(),
                                course.getName(),
                                course.getAuthor(),
                                course.getParticipantsLimit(),
                                course.getParticipantsNumber(),
                                course.getImageUrl()
                               ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getCoursesByCodes(List<String> savedList, String token) {
        validateAcces(token);
        if (savedList == null || savedList.isEmpty()){
            return List.of();
        }
        return courseRepository.findAllByCodeIn(savedList);
    }

    @Override
    public List<CourseDto> findCoursesByNameOrTags(String searchTerm, String token) {
        validateAcces(token);
        return courseRepository.findCoursesByNameOrTags(searchTerm).stream().map(course ->
                        new CourseDto(course.getCode(),
                                course.getName(),
                                course.getAuthor(),
                                course.getParticipantsLimit(),
                                course.getParticipantsNumber(),
                                course.getImageUrl()
                        ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> findCoursesByCategory(String category, String token) {
        validateAcces(token);
        return courseRepository.findAllByCategory(category).stream().map(course ->
                        new CourseDto(course.getCode(),
                                course.getName(),
                                course.getAuthor(),
                                course.getParticipantsLimit(),
                                course.getParticipantsNumber(),
                                course.getImageUrl()
                        ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> addSectionToCourse(String courseId, String sectionName, int position ,String token) {
        validateAcces(token);
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException(CourseError.COURSE_NOT_FOUND));
        course.getSections().add(new Section(sectionName, position));
        course.getSections().sort(null);
        return Collections.singletonList(courseRepository.save(course));
    }
    //dopoprawy^^^^^^^
    @Override
    public List<Course> addLectureToCourse(String courseId, String sectionName, Lecture lecture, MultipartFile video, String containerName, String token) {
        validateAcces(token);
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException(CourseError.COURSE_NOT_FOUND));
        try {
            lecture.setVideoUrl(uploadImage(containerName, video));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        course.getSections().stream().filter(section -> sectionName.equals(section.getTitle())).findAny().map(section -> section.getLessons().add(lecture));
        course.getSections().forEach(section -> section.getLessons().sort(null));
        return Collections.singletonList(courseRepository.save(course));
    }



    //dopoprawy^^^^^^^
    @Override
    public Course getCourse(String code, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validateAcces(authHeader);
        return courseRepository.findById(code).orElseThrow(()-> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course addCourse(Course course, String containerName, MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validateAcces(authHeader);
        try {
            course.setImageUrl(uploadImage(containerName, file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        course.validateCourse();
        String[] baseTags = course.getName().split(" ");
        List<String> tags = course.getTags() != null ? course.getTags() : new ArrayList<>();
        for (String tag : baseTags) {
            tags.add(tag.toLowerCase());
        }
        course.setTags(tags);
        course.validateCourse();
        return courseRepository.save(course);
    }

    public String uploadImage(String containerName, MultipartFile file)throws IOException{
        try(InputStream inputStream = file.getInputStream()) {
            return this.imageStorageClient.uploadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
        }
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
    public Course addStudentToCourse(String code, Long id, String token) {
        validateAcces(token);
        StudentDto student = studentServiceClient.getStudent(id);
        return courseRepository.findById(code).map(CourseFromDb ->{
            CourseFromDb.validateCourseIsActive();
            CourseFromDb.validateParticipantStatus(student);
            CourseFromDb.validateSignedParticipants(student);
            CourseFromDb.getParticipants().add(new CourseMember(student.getEmail()));
            CourseFromDb.incrementParticipants();
            studentServiceClient.addCourse(id, code);
            return courseRepository.save(CourseFromDb);
        }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public void removeStudentFromCourse(String code, String email, String authHeader) {
        validateAcces(authHeader);
        Course course = courseRepository.findById(code).orElseThrow(()-> new CourseException(CourseError.COURSE_NOT_FOUND));
        course.decrementParticipants();
        course.getParticipants().removeIf(courseMember -> courseMember.getEmail().equals(email));
        studentServiceClient.removeCourse(email,code);
        courseRepository.save(course);
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
                .emails(emailMemebers)
                .build();
        rabbitTemplate.convertAndSend("enroll_finish", notificationInfo);
    }

    private static List<String> getCourseMembersEmails(Course course) {
        return course.getParticipants().stream().map(CourseMember::getEmail).toList();
    }


}
