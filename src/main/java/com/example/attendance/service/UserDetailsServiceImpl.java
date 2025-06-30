package com.example.attendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.attendance.dto.EmployeeDto;
import com.example.attendance.entity.User;
import com.example.attendance.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserService {
 
 @Autowired
 private UserRepository userRepository;

 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     User user = userRepository.findByUsername(username)
         .orElseThrow(() -> new UsernameNotFoundException("User not found"));
     
     return org.springframework.security.core.userdetails.User
         .withUsername(user.getUsername())
         .password(user.getPassword())
         .roles(user.getRole())
         .build();
 }

@Override
public void registerNewEmployee(EmployeeDto employeeDto) {
	// TODO Auto-generated method stub
	
}
}