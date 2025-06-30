package com.example.attendance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.attendance.entity.Employee;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.EmployeeService;

@Controller
@RequestMapping("/dashboard")  //i changed
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) {
        LocalDate today = LocalDate.now();
        
     
        List<Employee> allEmployees = employeeService.getAllEmployees();
        int totalEmployees = allEmployees.size();
        long presentToday = attendanceService.countTodayAttendanceByStatus("PRESENT");
        long absentToday = totalEmployees - presentToday;
        
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("todayPresent", presentToday);
        model.addAttribute("todayAbsent", absentToday);

        // Calculate monthly attendance rate
        int presentThisMonth = attendanceService.countMonthlyAttendanceByStatus(
            today.getYear(), 
            today.getMonthValue(), 
            "PRESENT"
        );
        int workingDaysThisMonth = today.lengthOfMonth(); // Adjust for business days if needed
        double monthlyAttendanceRate = (double) presentThisMonth / (totalEmployees * workingDaysThisMonth) * 100;
        
        model.addAttribute("monthlyAttendanceRate", String.format("%.1f%%", monthlyAttendanceRate));

        // Get recent records and all employees
        model.addAttribute("recentAttendance", attendanceService.getRecentAttendanceRecords(10));
        model.addAttribute("employees", allEmployees);

        return "admin-dashboard";
    }
}