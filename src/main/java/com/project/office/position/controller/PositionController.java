package com.project.office.position.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.project.office.common.ResponseDTO;
import com.project.office.position.dto.PositionDTO;
import com.project.office.position.service.PositionService;

@RestController
@RequestMapping("/api/position")
public class PositionController {

	private final PositionService positionService;
	
	public PositionController(PositionService positionService) {
		this.positionService = positionService;
	}
	
	// 직위 등록
	@PostMapping("/position-management")
	public ResponseEntity<ResponseDTO> insertPosition(@RequestBody PositionDTO positionDto) {
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "직위 등록 완료", positionService.insertPosition(positionDto)));
	}
	
	// 전체 직위 목록 조회
	@GetMapping("/list")
	public ResponseEntity<ResponseDTO> positionList() {
		
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "직위 목록 조회 완료", positionService.positionList()));
	}																									  
	
	// 직위 삭제
	@DeleteMapping("/position-management/{positionNo}")
	public ResponseEntity<ResponseDTO> deletePosition(@PathVariable Long positionNo) {
		positionService.deletePosition(positionNo);
		return ResponseEntity.noContent().build();
	}
}
