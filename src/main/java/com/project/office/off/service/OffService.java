package com.project.office.off.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.office.member.dto.MemberDTO;
import com.project.office.member.entity.Member;
import com.project.office.off.dto.OffDTO;
import com.project.office.off.entity.Off;
import com.project.office.off.repository.OffMemberRepository;
import com.project.office.off.repository.OffRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OffService {
	
	private final OffRepository offRepository;
	private final OffMemberRepository offMemberRepository;
	private final ModelMapper modelMapper;
	
	public OffService(OffRepository offRepository, OffMemberRepository offMemberRepository, ModelMapper modelMapper) {
		this.offRepository = offRepository;
		this.offMemberRepository = offMemberRepository;
		this.modelMapper = modelMapper;
	}
	
	/* 연차 조회 */
	public Page<OffDTO> getOffList(MemberDTO member, int page) {
		
		log.info("[OffService] getOffList Start ====================");
		
		Long memberNo = member.getMemberNo();
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("offNo").descending());
		
		Page<Off> offList = offRepository.findByMember(memberNo, pageable);
		Page<OffDTO> offDTOList = offList.map(off -> modelMapper.map(off, OffDTO.class));
		
		log.info("[OffService] getOffList End ====================");
		
		return offDTOList;
		
	}

	/* 연차 신청 양식에 나타낼 결재권자 조회 */
	public MemberDTO selectApp(MemberDTO member) {
		
		log.info("[OffService] selectApp Start ====================");
		
		Long deptNo = member.getDept().getDeptNo();
		
		Member memberName = offMemberRepository.findByDeptNoAndAuthNo(deptNo);
		MemberDTO memberDTO = modelMapper.map(memberName, MemberDTO.class);
		
		log.info("[OffService] selectApp End ====================");
		
		return memberDTO;
		
	}

	/* 연차 신청 */
	public OffDTO insertOff(OffDTO offDTO) {
		
		log.info("[OffService] insertOff Start ====================");
		
		offRepository.save(modelMapper.map(offDTO, Off.class));
		
		log.info("[OffService] insertOff End ====================");
		
		return offDTO;
		
	}

	/* 결과별 연차 신청 목록 조회(결재권자) */
	public Page<OffDTO> getOffListForApp(String offResult, int page) {
		
		log.info("[OffService] getOffList Start ====================");
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("offNo").descending());
		
		Page<Off> offList = offRepository.findByOffResult(offResult, pageable);
		Page<OffDTO> offDTOList = offList.map(off -> modelMapper.map(off, OffDTO.class));
		
		log.info("[OffService] getOffList End ====================");
		
		return offDTOList;
		
	}
	
	/* 연차 상세 조회 */
	public OffDTO selectOff(Long offNo) {

		log.info("[OffService] selectOff Start ====================");
		log.info("[OffService] offNo : {}", offNo);
		
		Off off = offRepository.findById(offNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 연차가 존재하지 않습니다. offNo=" + offNo));
		OffDTO offDTO = modelMapper.map(off, OffDTO.class);
		
		log.info("[OffService] selectOff End ====================");
		
		return offDTO;
		
	}

}
