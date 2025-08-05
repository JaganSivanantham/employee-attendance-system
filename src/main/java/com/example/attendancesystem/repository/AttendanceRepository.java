package com.example.attendancesystem.repository;

import com.example.attendancesystem.model.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<AttendanceRecord, String> {
    Optional<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);
    List<AttendanceRecord> findByEmployeeIdOrderByDateDesc(String employeeId);
}