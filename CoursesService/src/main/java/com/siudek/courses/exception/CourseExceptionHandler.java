package com.siudek.courses.exception;

import feign.FeignException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseExceptionHandler {

    @ExceptionHandler(value = CourseException.class)
    public ResponseEntity<ErrorInfo> handleException(CourseException e){
        if (e.getCourseError().equals(CourseError.COURSE_NOT_FOUND)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorInfo(e.getCourseError().getMessage()));
        }
        else if (e.getCourseError().equals(CourseError.COURSE_CAN_NOT_SET_FULL_STATUS)
                || e.getCourseError().equals(CourseError.COURSE_CAN_NOT_SET_ACTIVE_STATUS)
                || e.getCourseError().equals(CourseError.COURSE_PARTICIPANTS_LIMIT_IS_EXCEEDED)
                || e.getCourseError().equals(CourseError.STUDENT_IS_ALREADY_SIGNED)
                || e.getCourseError().equals(CourseError.COURSE_IS_ALREADY_INACTIVE)
        ){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorInfo(e.getCourseError().getMessage()));
        }
        else if (e.getCourseError().equals(CourseError.COURSE_START_DATE_IS_AFTER_END_DATE)
                ||e.getCourseError().equals(CourseError.COURSE_IS_NOT_ACTIVE)
                ||e.getCourseError().equals(CourseError.STUDENT_IS_NOT_ACTIVE)
                ||e.getCourseError().equals(CourseError.STUDENT_CANNOT_BE_SIGNED)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo(e.getCourseError().getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo(e.getCourseError().getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException e){
        return ResponseEntity.status(e.status()).body(new JSONObject(e.contentUTF8()).toMap());
    }
}
