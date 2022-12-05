package com.project.office.approval.service;


import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.office.approval.dto.DocumentDTO;
import com.project.office.approval.entity.Document;
import com.project.office.approval.repository.DocumentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentService {
	
	private DocumentRepository documentRepository;
	private final ModelMapper modelMapper;

	
	public DocumentService(DocumentRepository documentRepository, ModelMapper modelMapper) {
		this.documentRepository = documentRepository;
		this.modelMapper = modelMapper;
	}
	
	
	/* 목록조회*/
	public Page<DocumentDTO> selectDocumentList(int page, Long memberNo) {
		
		Pageable pageable = PageRequest.of(page -1, 10, Sort.by("docNo").descending());
		
		
		Page<Document> documentList = documentRepository.findAll(pageable, memberNo);
		
		Page<DocumentDTO> documentDTOList = documentList.map(document -> modelMapper.map(document, DocumentDTO.class));
		
	
		return documentDTOList;
	
}
	
	
	/* 상세조회 */
	public DocumentDTO selectDocumentDetail(Long docNo) {

		log.info("[selectDocumentDetail] docNo : {}", docNo);
		
		Document document = documentRepository.findBydocNo(docNo)
				.orElseThrow(() -> new IllegalArgumentException("결재가 없습니다. docNo=" + docNo));
		DocumentDTO documentDTO = modelMapper.map(document, DocumentDTO.class);

		return documentDTO;
	}
	
	/* 결재등록 */
	@Transactional
	public DocumentDTO insertDocument(DocumentDTO documentDTO) {
		
		documentRepository.save(modelMapper.map(documentDTO, Document.class));
		
		return documentDTO;
		
		
	}

	
	
	

}
