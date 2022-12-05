package com.project.office.off.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

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
	@Transactional
	public OffDTO insertOff(OffDTO offDTO) {
		
		log.info("[OffService] insertOff Start ====================");
		
		offRepository.save(modelMapper.map(offDTO, Off.class));
		
		log.info("[OffService] insertOff End ====================");
		
		return offDTO;
		
	}

	/* 결과별 연차 신청 목록 조회(결재권자) */
	public Page<OffDTO> getOffListForApp(MemberDTO member, String offResult, int page) {
		
		log.info("[OffService] getOffList Start ====================");
		
		Long deptNo = member.getDept().getDeptNo();
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("offNo").descending());
		
		Page<Off> offList = offRepository.findByOffResult(deptNo, offResult, pageable);
		Page<OffDTO> offDTOList = offList.map(off -> modelMapper.map(off, OffDTO.class));
		
		log.info("[OffService] getOffList End ====================");
		
		return offDTOList;
		
	}
	
	/* 연차 상세 조회 */
	public OffDTO selectOff(Long offNo) {

		log.info("[OffService] selectOff Start ====================");
		log.info("[OffService] offNo : {}", offNo);
		
		Off off = offRepository.findById(offNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 연차가 존재하지 않습니다."));
		OffDTO offDTO = modelMapper.map(off, OffDTO.class);
		
		log.info("[OffService] selectOff End ====================");
		
		return offDTO;
		
	}

	/* 연차 승인 처리 */
	@Transactional
	public OffDTO appOff(OffDTO offDTO) {
		
		log.info("[OffService] appOff Start ====================");
		log.info("[OffService] offDTO : ", offDTO);
		
		Off foundOff = offRepository.findById(offDTO.getOffNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 연차가 존재하지 않습니다."));
		
		foundOff.setOffResult("승인");
		
		offRepository.save(foundOff);
		
		log.info("[OffService] appOff End ====================");
		
		return offDTO;
		
	}

	/* 연차 반려 처리 */
	@Transactional
	public OffDTO returnOff(OffDTO offDTO) {

		log.info("[OffService] returnOff Start ====================");
		log.info("[OffService] offDTO : ", offDTO);
		
		Off foundOff = offRepository.findById(offDTO.getOffNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 연차가 존재하지 않습니다."));
		
		foundOff.setOffResult("반려");
		
		offRepository.save(foundOff);
		
		log.info("[OffService] returnOff End ====================");
		
		return offDTO;
		
	}

	/* 연차 수정 */
	public OffDTO updateOff(MemberDTO member, Long offNo, OffDTO offDTO) {
		
		log.info("[OffService] updateOff Start ====================");
		log.info("[OffService] offNo : ", offNo);
		
		Off foundOff = offRepository.findByMemberAndOffNo(member.getMemberNo(), offNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 연차가 존재하지 않습니다."));
		
		foundOff.update(offDTO.getOffStart(), offDTO.getOffEnd(), offDTO.getOffTitle(), offDTO.getOffReason());
		
		LocalDateTime today = LocalDateTime.now();
		foundOff.setOffUpdate(today);
		
		offRepository.save(foundOff);
		
		log.info("[OffService] updateOff End ====================");
		
		return offDTO;
		
	}

}
