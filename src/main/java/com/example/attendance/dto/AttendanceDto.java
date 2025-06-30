package com.example.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDto {
    private String employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;
    private Double hoursWorked;
    
    public AttendanceDto(String employeeId, String employeeName, LocalDate date, 
                       LocalDateTime checkIn, LocalDateTime checkOut, 
                       String status, Double hoursWorked) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.hoursWorked = hoursWorked;
    }

	public String getEmployeeId() {
		return employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalDateTime getCheckIn() {
		return checkIn;
	}

	public LocalDateTime getCheckOut() {
		return checkOut;
	}

	public String getStatus() {
		return status;
	}

	public Double getHoursWorked() {
		return hoursWorked;
	}
    
    // Getters
}