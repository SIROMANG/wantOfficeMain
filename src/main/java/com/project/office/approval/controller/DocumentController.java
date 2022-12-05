package com.project.office.approval.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.office.approval.dto.DocumentDTO;
import com.project.office.approval.dto.FormDTO;
import com.project.office.approval.service.DocumentService;
import com.project.office.approval.service.FormService;
import com.project.office.common.ResponseDTO;
import com.project.office.common.paging.PagingButton;
import com.project.office.common.paging.ResponseDTOWithPaging;
import com.project.office.common.paging.pagenation;
import com.project.office.member.dto.MemberDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/aproval")
public class DocumentController {
	
	
	private DocumentService documentService;
//	private FormService formService;

	public  DocumentController(DocumentService documentService) {
		this.documentService=documentService;
//		this.formService = formService;
		
	}

	
	
	@GetMapping("/list")
	public ResponseEntity<ResponseDTO> selectDocumentList(@RequestParam(name = "page", defaultValue = "1") int page,
			@AuthenticationPrincipal MemberDTO member) {
		
		Long memberNo = member.getMemberNo();
		Page<DocumentDTO> documentDTOList = documentService.selectDocumentList(page, memberNo);
		
		PagingButton pageBtn = pagenation.getPagingButton(documentDTOList);
		
		ResponseDTOWithPaging responseDtoWithPaging = new ResponseDTOWithPaging();
		responseDtoWithPaging.setPageBtn(pageBtn);
		responseDtoWithPaging.setData(documentDTOList.getContent());
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", responseDtoWithPaging));
	}
	
//	
//	@GetMapping("/formlist")
//	public ResponseEntity<ResponseDTO> selectFormList(FormDTO Form){
//		
//		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "문서서식 조 성공", formService.selectFormList(Form)));
//		
//	}
	
	
	/* 결재 문서 상세 조회 */
	@GetMapping("/list/{docNo}")
	public ResponseEntity<ResponseDTO> selectDocumentDetail(@PathVariable Long docNo) {
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상세 조회 성공", documentService.selectDocumentDetail(docNo)));
	}
	
	
	/* 결재 등록 */
	@PostMapping("/aprovals")
	public ResponseEntity<ResponseDTO> insertDocument(@ModelAttribute DocumentDTO documentDTO,
			@AuthenticationPrincipal MemberDTO member){
		
		documentDTO.setMember(member);
		
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "결재 등록 성공", documentService.insertDocument(documentDTO)));
		
	}
	
	
	
	
	
	
	
	
	

}
