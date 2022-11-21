package com.project.office.attendance.controller;

import java.text.ParseException;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.office.attendance.dto.AttendanceDTO;
import com.project.office.attendance.service.AttendanceService;
import com.project.office.common.ResponseDTO;
import com.project.office.common.paging.PagingButton;
import com.project.office.common.paging.ResponseDTOWithPaging;
import com.project.office.common.paging.pagenation;
import com.project.office.member.dto.MemberDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	
	public AttendanceController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
	
	//출근 등록
	@PostMapping("/attendance/in")
	public ResponseEntity<ResponseDTO> insertAttIn(@RequestBody MemberDTO member/*@AuthenticationPrincipal MemberDTO member*/) throws ParseException {
		
		log.info("[AttendanceController] insertAttIn Start ====================");
		//log.info("[AttendanceController] member : {}", member);
		
		AttendanceDTO attendanceDTO = new AttendanceDTO();
		
		LocalDateTime now = LocalDateTime.now();
		
		attendanceDTO.setMember(member);
		attendanceDTO.setAttIn(now);
		
		log.info("[AttendanceController] insertAttIn End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "출근 완료", attendanceService.insertAttIn(attendanceDTO)));
		
	}
	
	/* 퇴근 등록 */
	@PatchMapping("/attendance/out")
	public ResponseEntity<ResponseDTO> insertAttOut(MemberDTO member /*@AuthenticationPrincipal MemberDTO member*/) throws ParseException {
		
		log.info("[AttendanceController] insertAttOut Start ====================");
		
		// 나중에 로그인 한 상태로 처리해서 삭제할 코드
		member.setMemberNo(2L);
		
		AttendanceDTO attendanceDTO = new AttendanceDTO();
		
		LocalDateTime now = LocalDateTime.now();
		
		attendanceDTO.setMember(member);
		attendanceDTO.setAttOut(now);
		
		log.info("[AttendanceController] insertAttOut End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "퇴근 완료", attendanceService.insertAttOut(attendanceDTO, now)));
		
	}
	
	/* 내 근태 월별 목록 조회 */
	@GetMapping("/attendance/{memberNo}")
	public ResponseEntity<ResponseDTO> getMyAttList
		(@RequestParam(name="page", defaultValue="1") int page, @PathVariable Long memberNo, @RequestParam(name="search") String attDate) {
		
		log.info("[AttendanceController] getMyAttList Start =====================");
		log.info("[AttendanceController] page : {}", page);
		log.info("[AttendanceController] memberNo : {}", memberNo);
		
		Page<AttendanceDTO> attendanceDTOList = attendanceService.getMyAttList(page, memberNo, attDate);
		
		PagingButton pageInfo = pagenation.getPagingButton(attendanceDTOList);
		
		log.info("[AttendanceController] pageInfo : {}", pageInfo);
		
		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageBtn(pageInfo);
		responseDTOWithPaging.setData(attendanceDTOList.getContent());
		
		log.info("[AttendanceController] getMyAttList End =====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "내 근태 조회 완료", responseDTOWithPaging));
		
	}
	
	/* 날짜별 근태 조회(관리자) */
	@GetMapping("/attendance-manage/list")
	public ResponseEntity<ResponseDTO> getAttListForAdmin
		(@RequestParam(name="page", defaultValue="1") int page, @RequestParam(name="search") String attDate) {
		
		log.info("[AttendanceController] getAttListForAdmin Start =====================");
		log.info("[AttendanceController] page : {}", page);
		log.info("[AttendanceController] attDate : {}", attDate);
		
		Page<AttendanceDTO> attendanceDTOList = attendanceService.getAttListForAdmin(page, attDate);
		
		PagingButton pageInfo = pagenation.getPagingButton(attendanceDTOList);
		
		log.info("[AttendanceController] pageInfo : {}", pageInfo);
		
		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageBtn(pageInfo);
		responseDTOWithPaging.setData(attendanceDTOList.getContent());
		
		log.info("[AttendanceController] getAttListForAdmin End =====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "날짜별 근태 조회 완료", responseDTOWithPaging));
		
	}

}
