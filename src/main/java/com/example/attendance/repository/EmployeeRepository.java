package com.example.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.attendance.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserUsername(String username);
    Optional<Employee> findByEmpId(String empId);
    List<Employee> findAllByActiveTrue();
    long countByActiveTrue();
}