package com.example.attendancesystem.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "attendanceRecords")
public class AttendanceRecord {
    @Id
    private String id;
    private String employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // e.g., "PRESENT", "ABSENT"
}