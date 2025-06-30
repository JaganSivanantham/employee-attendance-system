package com.example.attendance.service;



import com.example.attendance.entity.Employee;
import com.example.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

 @Autowired
 private EmployeeRepository employeeRepository;

 @Override
 public List<Employee> getAllEmployees() {
     return employeeRepository.findAll();
 }

 @Override
 public Employee getEmployeeById(Long id) {
     return employeeRepository.findById(id).orElse(null);
 }

 @Override
 public Employee saveEmployee(Employee employee) {
     return employeeRepository.save(employee);
 }

 @Override
 public void deleteEmployee(Long id) {
     employeeRepository.deleteById(id);
 }

@Override
public Employee findByUsername(String username) {
	// TODO Auto-generated method stub
	return null;
}
}