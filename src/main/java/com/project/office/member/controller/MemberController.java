package com.project.office.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.office.common.ResponseDTO;
import com.project.office.common.paging.PagingButton;
import com.project.office.common.paging.ResponseDTOWithPaging;
import com.project.office.common.paging.pagenation;
import com.project.office.member.dto.MemberDTO;
import com.project.office.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/my")
public class MemberController {
	
	private final MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	// 내 정보 조회
	@GetMapping("/member")
	public ResponseEntity<ResponseDTO> selectMyInfo(@AuthenticationPrincipal MemberDTO member) {
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 정보 조회 성공", memberService.selectMyInfo(member)));
	}
	
	// 내 정보 수정
	@PatchMapping("/member")
	public ResponseEntity<ResponseDTO> updateMyInfo(@ModelAttribute MemberDTO memberDto, @AuthenticationPrincipal MemberDTO member) {
		
		log.info("[updateMyInfo] MemberDTO : {} ", memberDto);
		log.info("[updateMyInfo] MemberDTO : {} ", member);
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 정보 수정 성공", memberService.updateMyInfo(memberDto, member)));
	}
	
	// 비밀번호 변경
	@PutMapping("/member/updatePwd")
	public ResponseEntity<ResponseDTO> updateMyPwd(@RequestBody MemberDTO memberDto) {
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "비밀번호 변경 완료", memberService.updateMyPwd(memberDto)));
	}
	
	/* 내 명함 조회 */
	@GetMapping("/card")
	public ResponseEntity<ResponseDTO> selectMyCard(@AuthenticationPrincipal MemberDTO member) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 명함 조회 성공", memberService.selectMyCard(member)));
		
	}
	
	/* 내 명함 수정 */
	@PatchMapping("/card")
	public ResponseEntity<ResponseDTO> updateMyCard(@RequestBody MemberDTO memberDTO, @AuthenticationPrincipal MemberDTO member) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "내 명함 수정 성공", memberService.updateMyCard(memberDTO, member)));
		
	}
	
	/* 사내 명함 조회(본인 제외) */
	@GetMapping("/members")
	public ResponseEntity<ResponseDTO> getCardList(@AuthenticationPrincipal MemberDTO memberDTO, @RequestParam(name="page", defaultValue="1") int page) {
	      
		log.info("[MemberController] selectCards Start =====================");
	    log.info("[MemberController] memberDTO : {}", memberDTO);
	    log.info("[MemberController] page : {}", page);
	      
	    Page<MemberDTO> memberDTOList = memberService.getCardList(memberDTO, page);
	      
	    PagingButton pageBtn = pagenation.getPagingButton(memberDTOList);
	    log.info("[MemberController] pageBtn : {}", pageBtn);
	      
	    ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
	    responseDTOWithPaging.setPageBtn(pageBtn);
	    responseDTOWithPaging.setData(memberDTOList.getContent());
	      
	    log.info("[MemberController] selectCards End =====================");
	      
	    return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "사내 명함 조회 성공", responseDTOWithPaging));
	      
	}
	
}
