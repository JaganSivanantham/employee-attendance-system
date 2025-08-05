package com.example.attendancesystem.controller;

import com.example.attendancesystem.model.AttendanceRecord;
import com.example.attendancesystem.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/powerbi")
@RequiredArgsConstructor
public class PowerBiController {

    private final AttendanceRepository attendanceRepository;

    // This endpoint will be protected by our JWT filter
    @GetMapping("/attendance-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceDataForPowerBi() {
        return ResponseEntity.ok(attendanceRepository.findAll());
    }
}