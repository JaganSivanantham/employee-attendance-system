package com.example.attendance.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.CompanyLocation;
import com.example.attendance.entity.Employee;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CompanyLocationRepository;
import com.example.attendance.repository.EmployeeRepository;

@Service
@Transactional
public class AttendanceService {

    private static final long DEFAULT_OFFICE_ID = 1L;
    private static final double HALF_DAY_HOURS_THRESHOLD = 4.0;

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyLocationRepository locationRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository,
                           EmployeeRepository employeeRepository,
                           CompanyLocationRepository locationRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.locationRepository = locationRepository;
    }

    public void checkIn(String username, double latitude, double longitude, String ipAddress) {
        Employee employee = employeeRepository.findByUserUsername(username)
                .orElseThrow();
        
        try {
			verifyLocation(latitude, longitude);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			verifyNotAlreadyCheckedIn(employee);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setStatus("PRESENT");
        attendance.setLocationVerified(true);
        attendance.setIpAddress(ipAddress);
        
        attendanceRepository.save(attendance);
    }

    public void checkOut(String username) throws Exception {
        Employee employee = employeeRepository.findByUserUsername(username)
                .orElseThrow();
        
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, LocalDate.now())
                .orElseThrow();
        
        if (attendance.getCheckOut() != null) {
            throw new Exception("You have already checked out today");
        }
        
        attendance.setCheckOut(LocalDateTime.now());
        calculateAndSetWorkHours(attendance);
        attendanceRepository.save(attendance);
    }

    private void verifyLocation(double latitude, double longitude) throws Exception {
        CompanyLocation office = locationRepository.findById(DEFAULT_OFFICE_ID)
                .orElseThrow();
        
        double distance = calculateDistance(latitude, longitude, 
                          office.getLatitude(), office.getLongitude());
        
        if (distance > office.getAllowedRadius()) {
            throw new Exception("You must be within office premises to check in");
        }
    }

    private void verifyNotAlreadyCheckedIn(Employee employee) throws Exception {
        if (attendanceRepository.existsByEmployeeAndDate(employee, LocalDate.now())) {
            throw new Exception("You have already checked in today");
        }
    }

    private void calculateAndSetWorkHours(Attendance attendance) {
        Duration duration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
        double hoursWorked = duration.toMinutes() / 60.0;
        attendance.setHoursWorked(hoursWorked);
        
        if (hoursWorked < HALF_DAY_HOURS_THRESHOLD) {
            attendance.setStatus("HALF_DAY");
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Earth radius in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000; // Convert to meters
    }

    public Attendance getTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now());
    }

    public List<Attendance> getRecentAttendance(Long employeeId) {
        return attendanceRepository.findTop5ByEmployeeIdOrderByDateDesc(employeeId);
    }

    public int getAttendanceCount(Long employeeId, int year, int month, String status) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return attendanceRepository.countByEmployeeIdAndDateBetweenAndStatus(
            employeeId, startDate, endDate, status);
    }

    public int countTodayAttendanceByStatus(String status) {
        return attendanceRepository.countByDateAndStatus(LocalDate.now(), status);
    }

    public int countMonthlyAttendanceByStatus(int year, int month, String status) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return attendanceRepository.countByDateBetweenAndStatus(startDate, endDate, status);
    }

    public List<Attendance> getRecentAttendanceRecords(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return attendanceRepository.findRecentAttendanceRecords(pageable);
    }
}