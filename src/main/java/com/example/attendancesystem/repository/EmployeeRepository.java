package com.example.attendancesystem.repository;

import com.example.attendancesystem.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByUsername(String username);
}