package com.example.attendance.service;


import com.example.attendance.entity.Employee;
import java.util.List;

public interface EmployeeService {
 List<Employee> getAllEmployees();
 Employee getEmployeeById(Long id);
 Employee saveEmployee(Employee employee);
 void deleteEmployee(Long id);
 Employee findByUsername(String username);
}