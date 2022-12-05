package com.project.office.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.office.approval.entity.Form;

public interface FormRepository extends JpaRepository<Form, Long> {
	
	
//	List<Form> findByAll();
	
	
}
