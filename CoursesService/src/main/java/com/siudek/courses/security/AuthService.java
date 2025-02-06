package com.siudek.courses.security;


import com.siudek.courses.service.StudentServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final StudentServiceClient studentServiceClient;

    @Autowired
    public AuthService(StudentServiceClient studentServiceClient) {
        this.studentServiceClient = studentServiceClient;
    }

    public boolean validateToken() {
        return studentServiceClient.validateToken();
    }
}