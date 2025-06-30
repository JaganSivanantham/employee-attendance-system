package com.example.attendance.service;

import org.springframework.stereotype.Service;

import com.example.attendance.dto.EmployeeDto;
@Service
public interface UserService {
    void registerNewEmployee(EmployeeDto employeeDto);
}