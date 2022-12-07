package com.project.office.member.service;

import java.io.IOException;
import java.util.UUID;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.office.exception.FindIdFailedException;
import com.project.office.exception.LoginFailedException;
import com.project.office.exception.UserNotFoundException;
import com.project.office.jwt.TokenProvider;
import com.project.office.member.dto.MemberDTO;
import com.project.office.member.dto.TokenDTO;
import com.project.office.member.entity.Member;
import com.project.office.member.repository.MemberRepository;
import com.project.office.util.FileUploadUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	
	@Value("${image.image-dir}")
	private String IMAGE_DIR;
	@Value("${image.image-url}")
	private String IMAGE_URL;
	
	public MemberService(MemberRepository memberRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
		this.memberRepository = memberRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}
	
	// 로그인
	public TokenDTO login(MemberDTO memberDto) {
		log.info("[AuthService] login Start ===========================");
		log.info("[AuthService] memberDto : {}", memberDto);
			
		// 아이디 조회
		Member member = memberRepository.findByMemberId(memberDto.getMemberId())
				.orElseThrow(() -> new LoginFailedException("아이디 또는 비밀번호가 잘못되었습니다."));
			
		// 비밀번호 매칭
		if(!passwordEncoder.matches(memberDto.getMemberPassword(), member.getMemberPassword())) {
			log.info("[AuthService] Password Match Failed");
			throw new LoginFailedException("아이디 또는 비밀번호가 잘못되었습니다.");
		}
			
		// 토큰 발급
		TokenDTO tokenDto = tokenProvider.generateTokenDTO(modelMapper.map(member, MemberDTO.class));
		log.info("[AuthService] tokenDto : {}", tokenDto);
			
		log.info("[AuthService] login End ===========================");
			
		return tokenDto;
	}

		
		// 아이디 찾기
		public MemberDTO findId(MemberDTO memberDto) {
			log.info("[AuthService] findId Start ===========================");
			log.info("[AuthService] memberDto : {}", memberDto);
			
			Member member = memberRepository.findByMemberNameAndMemberEmail(memberDto.getMemberName(), memberDto.getMemberEmail())
					.orElseThrow(() -> new FindIdFailedException("입력하신 정보에 해당하는 아이디를 조회할 수 없습니다."));
			
			return modelMapper.map(member, MemberDTO.class);
		}
	
	// 내 정보 조회
	public MemberDTO selectMyInfo(MemberDTO member) {
		log.info("[MemberService] selectMyInfo Start ===========================");
		log.info("[MemberService] member : {}", member);
		
		Member findmember = memberRepository.findById(member.getMemberNo())
	            .orElseThrow(() -> new UserNotFoundException(member + "를 찾을 수 없습니다."));

		log.info("[MemberService] member : {}", member);
		
		log.info("[MemberService] selectMyInfo End ===========================");
		return modelMapper.map(findmember, MemberDTO.class);
	}

	// 내 정보 수정
	@Transactional
	public MemberDTO updateMyInfo(MemberDTO memberDto, MemberDTO member) {
		log.info("[MemberService] updateMyInfo Start ===========================");
		log.info("[MemberService] memberDto : {}", memberDto);
		log.info("[MemberService] member : {}", member);
		
		String replaceFileName = null;
		
		try {
			Member oriMember = memberRepository.findById(member.getMemberNo()).orElseThrow(
					() ->  new IllegalArgumentException("해당 사원이 존재하지 않습니다. memberNo=" + member.getMemberNo()));
			String oriFile = oriMember.getMemberFileUrl();
			
			if(memberDto.getMemberImage() != null) {
				String fileName = UUID.randomUUID().toString().replace("-", "");
				replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, fileName, memberDto.getMemberImage());
				memberDto.setMemberFileUrl(replaceFileName);
				
				FileUploadUtils.deleteFile(IMAGE_DIR, oriFile);
			} else {
				memberDto.setMemberFileUrl(oriFile);
			}
			
			oriMember.update(
					memberDto.getMemberPhone(),
					memberDto.getMemberEmail(),
					memberDto.getMemberFileUrl()
					);
			memberRepository.save(oriMember);
			
			log.info("[MemberService] update : {}", modelMapper.map(oriMember, MemberDTO.class));
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
			}catch (IOException e1) {
				e.printStackTrace();
			}
		}
		
		log.info("[MemberService] updateMyInfo End ===========");
		return memberDto;
	}
	
	// 비밀번호 변경
	@Transactional
	public Object updateMyPwd(MemberDTO memberDto) {
		
		if(passwordEncoder.matches(memberDto.getMemberPassword(), memberDto.getMemberPassword())) { 
			memberDto.setMemberPassword(passwordEncoder.encode(memberDto.getMemberPassword()));
			memberRepository.save(modelMapper.map(memberDto, Member.class));
		}
		
		return memberDto;
	}
	
	/* 내 명함 조회 */
	public MemberDTO selectMyCard(MemberDTO member) {
		
		log.info("[MemberService] selectMyCard Start ===========");
		log.info("[MemberService] member : {}", member);
		
		Member foundMember = memberRepository.findById(member.getMemberNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 사원이 존재하지 않습니다."));
		MemberDTO memberDTO = modelMapper.map(foundMember, MemberDTO.class);
		
		log.info("[MemberService] selectMyCard End ===========");
		
		return memberDTO;
		
		
	}

	/* 내 명함 수정 */
	@Transactional
	public MemberDTO updateMyCard(MemberDTO memberDTO, MemberDTO member) {
		
		log.info("[MemberService] updateMyCard Start ===========");
		log.info("[MemberService] memberDTO : {}", memberDTO);
		log.info("[MemberService] member : {}", member);
		
		Long memberNo = member.getMemberNo();
		log.info("[MemberService] memberNo : {}", memberNo);
		
		Member oriMember = memberRepository.findById(memberNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 사원이 존재하지 않습니다."));
		
		oriMember.updateForCard(
				memberDTO.getMemberName(),
				memberDTO.getMemberPhone(),
				memberDTO.getMemberEmail());
		
		memberRepository.save(oriMember);
		
		log.info("[MemberService] updateMyCard End ===========");
		
		return memberDTO;
		
	}
	
	/* 사내 명함 조회(본인 제외) */
	public Page<MemberDTO> getCardList(MemberDTO memberDTO, int page) {
	      
		log.info("[MemberService] getCardList Start ===========");
	      
	    Pageable pageable = PageRequest.of(page - 1, 4, Sort.by("memberNo").descending());
	      
	    Long memberNo = memberDTO.getMemberNo();
	      
	    Page<Member> memberList = memberRepository.findAllWithoutMember(memberNo, pageable);
	    Page<MemberDTO> memberDTOList = memberList.map(member -> modelMapper.map(member, MemberDTO.class));
	      
	    log.info("[MemberService] memberDTOList : {}", memberDTOList);
	      
	    log.info("[MemberService] getCardList End ===========");
	      
	    return memberDTOList;
	      
	}
	
}
