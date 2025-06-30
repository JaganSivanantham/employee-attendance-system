package com.example.attendance.controller;

import java.security.Principal;
import com.example.attendance.entity.Employee;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        String username = principal.getName();
        Employee employee = employeeService.findByUsername(username);
        
        model.addAttribute("employee", employee);
        model.addAttribute("todayAttendance", attendanceService.getTodayAttendance(employee.getId()));
        model.addAttribute("attendanceRecords", attendanceService.getRecentAttendance(employee.getId()));
        
        // Calculate monthly stats
        LocalDate now = LocalDate.now();
        model.addAttribute("currentMonthPresentDays", 
            attendanceService.getAttendanceCount(employee.getId(), now.getYear(), now.getMonthValue(), "PRESENT"));
        model.addAttribute("currentMonthAbsentDays", 
            attendanceService.getAttendanceCount(employee.getId(), now.getYear(), now.getMonthValue(), "ABSENT"));
        model.addAttribute("currentMonthLateDays", 
            attendanceService.getAttendanceCount(employee.getId(), now.getYear(), now.getMonthValue(), "LATE"));
        
        return "employee-dashboard";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam double latitude, 
                         @RequestParam double longitude,
                         Principal principal,
                         HttpServletRequest request) {
        
        String username = principal.getName();
        String ipAddress = request.getRemoteAddr();
        
        try {
            attendanceService.checkIn(username, latitude, longitude, ipAddress);
            return "redirect:/employee/dashboard?success=Checked+in+successfully";
        } catch (Exception e) {
            return "redirect:/employee/dashboard?error=" + e.getMessage().replace(" ", "+");
        }
    }

    @PostMapping("/check-out")
    public String checkOut(Principal principal) {
        String username = principal.getName();
        
        try {
            attendanceService.checkOut(username);
            return "redirect:/employee/dashboard?success=Checked+out+successfully";
        } catch (Exception e) {
            return "redirect:/employee/dashboard?error=" + e.getMessage().replace(" ", "+");
        }
    }
}