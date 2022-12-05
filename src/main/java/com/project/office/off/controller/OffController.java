package com.project.office.off.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.office.common.ResponseDTO;
import com.project.office.common.paging.PagingButton;
import com.project.office.common.paging.ResponseDTOWithPaging;
import com.project.office.common.paging.pagenation;
import com.project.office.member.dto.MemberDTO;
import com.project.office.off.dto.OffDTO;
import com.project.office.off.service.OffService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class OffController {
	
	private final OffService offService;
	
	public OffController(OffService offService) {
		this.offService = offService;
	}
	
	/* 연차 조회 */
	@GetMapping("/off")
	public ResponseEntity<ResponseDTO> selectOff(@AuthenticationPrincipal MemberDTO member, @RequestParam(name="page", defaultValue="1") int page) {
		
		log.info("[OffController] selectOff Start ====================");
		log.info("[OffController] selectOff member : {}", member);
		log.info("[OffController] selectOff page : {}", page);
		
		Page<OffDTO> offDTOList = offService.getOffList(member, page);
		
		PagingButton pageBtn = pagenation.getPagingButton(offDTOList);
		log.info("[OffController] selectOff pageBtn : {}", pageBtn);
		
		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageBtn(pageBtn);
		responseDTOWithPaging.setData(offDTOList.getContent());
		
		log.info("[OffController] selectOff End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "연차 신청 목록 조회 완료", responseDTOWithPaging));
		
	}
	
	/* 연차 신청 양식에 나타낼 결재권자와 남은 연차일수 조회 */
	@GetMapping("/searchApp")
	public ResponseEntity<ResponseDTO> selectApp(@AuthenticationPrincipal MemberDTO member) {
		
		log.info("[OffController] selectApp Start ====================");
		log.info("[OffController] selectApp member : {}", member);
		
		Map<String, Object> newMap = new HashMap<String, Object>();
		newMap.put("app", offService.selectApp(member));
		newMap.put("offDays", member.getMemberRest());
		
		log.info("[OffController] selectApp newMap : {}", newMap);
		
		log.info("[OffController] selectApp End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "결재권자 이름과 남은 연차일수 조회 완료", newMap));
		
	}
	
	/* 연차 신청 */
	@PostMapping("/off")
	public ResponseEntity<ResponseDTO> insertOff(@AuthenticationPrincipal MemberDTO member, @RequestBody OffDTO offDTO) {
		
		log.info("[OffController] insertOff Start ====================");
		log.info("[OffController] insertOff member : {}", member);
		
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		offDTO.setMember(member);
		offDTO.setApproval(offService.selectApp(member));
		offDTO.setOffDate(date);
		offDTO.setOffResult("대기");
		
		log.info("[OffController] insertOff End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "연차 신청 완료", offService.insertOff(offDTO)));
		
	}
	
	/* 결과별 연차 신청 목록 조회(결재권자) */
	@GetMapping("/off/result")
	public ResponseEntity<ResponseDTO> selectOffForApp(@AuthenticationPrincipal MemberDTO member, @RequestParam(name="offResult") String offResult, @RequestParam(name="page", defaultValue="1") int page) {
		
		log.info("[OffController] selectOffForApp Start ====================");
		log.info("[OffController] selectOffForApp member : {}", member);
		log.info("[OffController] selectOffForApp offResult : {}", offResult);
		log.info("[OffController] selectOffForApp page : {}", page);
		
		Page<OffDTO> offDTOList = offService.getOffListForApp(member, offResult, page);
		
		PagingButton pageBtn = pagenation.getPagingButton(offDTOList);
		log.info("[OffController] selectOff pageBtn : {}", pageBtn);
		
		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageBtn(pageBtn);
		responseDTOWithPaging.setData(offDTOList.getContent());
		
		log.info("[OffController] selectOffForApp End ====================");
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "대기 연차 신청 목록 조회 완료", responseDTOWithPaging));
		
	}
	
	/* 연차 상세 조회 */
	@GetMapping("/off/{offNo}")
	public ResponseEntity<ResponseDTO> selectOffDetail(@PathVariable Long offNo) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "연차 상세 조회 완료", offService.selectOff(offNo)));
		
	}
	
	/* 연차 승인 처리 */
	@PatchMapping("/off/app")
	public ResponseEntity<ResponseDTO> appOff(@RequestBody OffDTO offDTO) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "연차 승인 처리 완료", offService.appOff(offDTO)));
		
	}
	
	/* 연차 반려 처리 */
	@PatchMapping("/off/return")
	public ResponseEntity<ResponseDTO> returnOff(@RequestBody OffDTO offDTO) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "연차 반려 처리 완료", offService.returnOff(offDTO)));
		
	}
	
	/* 연차 수정 */
	@PatchMapping("/off/modify/{offNo}")
	public ResponseEntity<ResponseDTO> updateOff(@AuthenticationPrincipal MemberDTO member, @PathVariable Long offNo, @RequestBody OffDTO offDTO) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "연차 수정 완료", offService.updateOff(member, offNo, offDTO)));
		
	}
	
	/* Calendar에서의 부서 연차조회*/
	@GetMapping("/calendar/offs")
	public ResponseEntity<ResponseDTO> calendarOff(@AuthenticationPrincipal MemberDTO member) {
		
		log.info("[OffController] member : {}", member);
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "캘린더 부서원 연차 조회 완료", offService.calendarOff(member.getDept().getDeptNo())));
	}
	

}
