package com.example.attendancesystem.controller;

import com.example.attendancesystem.model.Employee;
import com.example.attendancesystem.repository.EmployeeRepository;
import com.example.attendancesystem.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody AuthRequest request) {
        if (adminUsername.equals(request.getUsername()) && adminPassword.equals(request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername(), "ADMIN");
            return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
        }
        return ResponseEntity.status(401).body("Invalid Admin Credentials");
    }

    @PostMapping("/employee/login")
    public ResponseEntity<?> employeeLogin(@RequestBody AuthRequest request) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(request.getUsername());
        if (employeeOpt.isPresent() && employeeOpt.get().getPassword().equals(request.getPassword())) {
            Employee employee = employeeOpt.get();
            String token = jwtUtil.generateToken(employee.getUsername(), "EMPLOYEE");
            return ResponseEntity.ok(Map.of("token", token, "role", "EMPLOYEE", "employeeId", employee.getId(), "name", employee.getFirstName()));
        }
        return ResponseEntity.status(401).body("Invalid Employee Credentials");
    }

    // Admin can register new employees
    @PostMapping("/employee/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        if (employeeRepository.findByUsername(employee.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(savedEmployee);
    }
}

@Data
class AuthRequest {
    private String username;
    private String password;
}