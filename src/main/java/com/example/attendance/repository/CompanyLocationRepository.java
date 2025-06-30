package com.example.attendance.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.attendance.entity.CompanyLocation;

public interface CompanyLocationRepository extends JpaRepository<CompanyLocation, Long> {
	
	
	
}