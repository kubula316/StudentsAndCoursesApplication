package com.siudek.courses.model;

import com.siudek.courses.exception.CourseError;
import com.siudek.courses.exception.CourseException;
import com.siudek.courses.model.dto.StudentDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class Course {
    @Id
    private String code;

    private Status status;
    @NotBlank
    private String name;

    private String description;
    @NotNull
    @Future
    private LocalDateTime startDate;

    @NotNull
    @Future
    private LocalDateTime endDate;

    @NotNull
    @Min(0)
    private Long participantsLimit;

    @Min(0)
    private Long participantsNumber;

    List<CourseMember> participants = new ArrayList<>();

    public enum Status{
        ACTIVE,
        INACTIVE,
        FULL;
    }
    void validateCourseDate(){
        if (startDate.isAfter(endDate)){
            throw new CourseException(CourseError.COURSE_START_DATE_IS_AFTER_END_DATE);
        }
    }

    void validateCourseParticipantsLimit(){
        if (participantsNumber>participantsLimit){
            throw new CourseException(CourseError.COURSE_PARTICIPANTS_LIMIT_IS_EXCEEDED);
        }
    }

    void validateCourseFullStatus(){
        if (Status.FULL.equals(status) && !participantsNumber.equals(participantsLimit)){
            throw new CourseException(CourseError.COURSE_CAN_NOT_SET_FULL_STATUS);
        }
        if (Status.ACTIVE.equals(status) && participantsNumber.equals(participantsLimit)){
            throw new CourseException(CourseError.COURSE_CAN_NOT_SET_ACTIVE_STATUS);
        }
    }

    public void validateCourse(){
        validateCourseFullStatus();
        validateCourseParticipantsLimit();
        validateCourseDate();
    }

    public void validateCourseIsActive(){
        if (!Status.ACTIVE.equals(status)){
            throw new CourseException(CourseError.COURSE_IS_NOT_ACTIVE);
        }
    }

    public void validateSignedParticipants(StudentDto student){
        if (participants.stream().anyMatch(x -> x.getEmail().equals(student.getEmail()))){
            throw new CourseException(CourseError.STUDENT_IS_ALREADY_SIGNED);
        }
    }

    public void validateParticipantStatus(StudentDto student){
        if (!student.getStatus().equals(StudentDto.Status.ACTIVE)){
            throw new CourseException(CourseError.STUDENT_IS_NOT_ACTIVE);
        }
    }

    public void incrementParticipants(){
        participantsNumber += 1;
        if (participantsNumber.equals(participantsLimit)){
            setStatus(Status.FULL);
        }
    }



}
