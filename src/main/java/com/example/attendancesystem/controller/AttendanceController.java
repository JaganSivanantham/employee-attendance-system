package com.example.attendancesystem.controller;

// ... imports ...
import com.example.attendancesystem.model.AttendanceRecord;
import com.example.attendancesystem.repository.AttendanceRepository;
import com.example.attendancesystem.repository.EmployeeRepository;
import com.example.attendancesystem.service.LocationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LocationService locationService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> checkIn(@RequestBody LocationData locationData) {
        if (!locationService.isWithinCompanyRadius(locationData.getLatitude(), locationData.getLongitude())) {
            return ResponseEntity.status(403).body("You are not within the company premises.");
        }

        String username = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findByUsername(username).map(employee -> {
            Optional<AttendanceRecord> existingRecord = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), LocalDate.now());
            if (existingRecord.isPresent() && existingRecord.get().getCheckInTime() != null) {
                return ResponseEntity.status(400).body("You have already checked in today.");
            }

            AttendanceRecord record = existingRecord.orElse(new AttendanceRecord());
            record.setEmployeeId(employee.getId());
            record.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            record.setDate(LocalDate.now());
            record.setCheckInTime(LocalDateTime.now());
            record.setStatus("PRESENT");
            attendanceRepository.save(record);
            return ResponseEntity.ok("Checked in successfully at " + record.getCheckInTime());
        }).orElse(ResponseEntity.status(404).body("Employee not found."));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> checkOut(@RequestBody LocationData locationData) {
        if (!locationService.isWithinCompanyRadius(locationData.getLatitude(), locationData.getLongitude())) {
            return ResponseEntity.status(403).body("You are not within the company premises.");
        }

        String username = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findByUsername(username).flatMap(employee ->
                attendanceRepository.findByEmployeeIdAndDate(employee.getId(), LocalDate.now())
        ).map(record -> {
            if (record.getCheckOutTime() != null) {
                return ResponseEntity.status(400).body("You have already checked out today.");
            }
            if (record.getCheckInTime() == null) {
                return ResponseEntity.status(400).body("You have not checked in yet today.");
            }
            record.setCheckOutTime(LocalDateTime.now());
            attendanceRepository.save(record);
            return ResponseEntity.ok("Checked out successfully at " + record.getCheckOutTime());
        }).orElse(ResponseEntity.status(404).body("No check-in record found for today."));
    }

    @GetMapping("/history/{employeeId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceHistory(@PathVariable String employeeId) {
        return ResponseEntity.ok(attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecord>> getAllAttendance() {
        return ResponseEntity.ok(attendanceRepository.findAll());
    }
}

@Data
class LocationData {
    private double latitude;
    private double longitude;
}