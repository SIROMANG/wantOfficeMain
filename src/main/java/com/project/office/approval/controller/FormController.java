package com.project.office.approval.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.office.approval.service.FormService;
import com.project.office.common.ResponseDTO;

@RestController
@RequestMapping("/api/form")
public class FormController {
	
	
	private final FormService formService;
	
	public FormController(FormService formService) {
		
		this.formService = formService;
	}
	
	
	@GetMapping("/list")
	public ResponseEntity<ResponseDTO> formList() {
		
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "서식 목록 조회 성공", formService.formList()));
	}

}
