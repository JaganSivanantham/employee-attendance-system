package com.example.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findByEmployeeIdAndDate(long employeeId, LocalDate date);
    
    List<Attendance> findTop5ByEmployeeIdOrderByDateDesc(long employeeId);
    
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);
    
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    
    List<Attendance> findByEmployeeIdOrderByDateDesc(Long employeeId, Pageable pageable);
    
    int countByEmployeeIdAndDateBetweenAndStatus(
        Long employeeId, LocalDate start, LocalDate end, String status);
    
    int countByDateAndStatus(LocalDate date, String status);
    
    int countByDateBetweenAndStatus(LocalDate start, LocalDate end, String status);
    
    // Corrected version using Pageable
    @Query("SELECT a FROM Attendance a ORDER BY a.date DESC, a.checkIn DESC")
    List<Attendance> findRecentAttendanceRecords(Pageable pageable);
    
    // Alternative native query version
    @Query(value = "SELECT * FROM attendance ORDER BY date DESC, check_in DESC LIMIT : count", 
           nativeQuery = true)
    List<Attendance> findRecentAttendanceRecordsNative(@Param("count") int count);
}