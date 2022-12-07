package com.project.office.reservation.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.project.office.member.dto.MemberDTO;
import com.project.office.member.entity.Member;
import com.project.office.reservation.dto.ReservationDTO;
import com.project.office.reservation.entity.Reservation;
import com.project.office.reservation.repository.ReservationRepository;
import com.project.office.room.entity.Room;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final ModelMapper modelMapper;
	
	public ReservationService(ReservationRepository reservationRepository, ModelMapper modelMapper) {
		this.reservationRepository = reservationRepository;
		this.modelMapper = modelMapper;
	}
	
	@Value("${image.image-dir}")
	private String IMAGE_DIR;
	@Value("${image.image-url}")
	private String IMAGE_URL;
	
	/* 1. 회의실 예약 전체 목록 조회(관리자) */
	public Page<ReservationDTO> selectReservationMList(int page) {
		log.info("[ReservationService] selectReservationList start============");
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("reservationNo").descending());
		
		Page<Reservation> reservationList = reservationRepository.findAll(pageable);
		Page<ReservationDTO> reservationDTOList = reservationList.map(reservation -> modelMapper.map(reservation, ReservationDTO.class));
		
		
		log.info("[ReservationService] reservationDTOList : {}", reservationDTOList.getContent());
		log.info("[ReservationService] selectReservationList End============");
		
		return reservationDTOList;
	}
	
//	/* 2. 예약 목록 조회 - 검색[예약중 / 예약 가능/ 예약 취소]*/
//	public Page<ReservationDTO> selectReservationListByReservationStatus(int page, String reservationStatus) {
//		log.info("[ReservationService] selectReservationListByReservationDate start============");
//		
//		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("reservationNo").descending());
//		
//		Page<Reservation> reservationList = reservationRepository.findByReservationStatus(pageable, reservationStatus );
//		Page<ReservationDTO> reservationDTOList = reservationList.map(reservation -> modelMapper.map(reservation, ReservationDTO.class));
//		
//		log.info("[ReservationService] ReservationDTOList : {}", reservationDTOList.getContent());
//		
//		log.info("[ReservationService] selectReservationListByReservationDate end ============");
//		
//		return reservationDTOList;
//	}
	
	/* 3. 회의실 예약 상세 조회(회원)*/
	public ReservationDTO selectReservationListForAdmin(Long reservationNo) {
		log.info("[ReservationService] selectReservation start============");
		log.info("[ReservationService] reservationNo : {}", reservationNo);
		
		Reservation reservation = reservationRepository.findByReservationNo(reservationNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 예약사항이 없습니다. reservationNo=" + reservationNo));
		
		ReservationDTO reservationDTO = modelMapper.map(reservation, ReservationDTO.class);
		
		log.info("[ReservationService] reservationDTO : {}", reservationDTO);
		
		log.info("[ReservationService] selectReservation End============");
		return reservationDTO;
	}
	
	/* 3-1. 회의실 예약 조회 */
	public List<ReservationDTO> selectReservationList(Long roomNo) {
		log.info("[ReservationService] selectRoomReservationList start============");
		log.info("[ReservationService] room : {}", roomNo);

		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
		
		log.info("[ReservationService] start : {},end: {}", start,end);
		
		List<Reservation> reservation = reservationRepository.findByRoomAndReservationDate(start, end, roomNo)
				.orElseThrow(() -> new RuntimeException("오늘 예약이 존재하지 않습니다."));
		
		log.info("[ReservationService] reservation : {}", reservation);
		
		log.info("[ReservationService] selectRoomReservation end============");
		
		return reservation.stream().map(r -> modelMapper.map(r, ReservationDTO.class)).collect(Collectors.toList());
	}
	

	/* 4. 회의실 예약 등록 (회원) */
	@Transactional
	public ReservationDTO insertReservation(ReservationDTO reservationDTO) {
		log.info("[ReservationService] insertReservation start============");
		log.info("[ReservationService] reservationDTO : {}", reservationDTO );
		
		reservationRepository.save(modelMapper.map(reservationDTO, Reservation.class));
		
		log.info("[ReservationService] insertReservation End============");
		return reservationDTO;
	}
	
	/* 5. 회의실 예약 수정 (회원) */
	@Transactional
	public ReservationDTO updateReservation(MemberDTO member, ReservationDTO reservationDTO) {
		log.info("[ReservationService] updateReservation start============");
		log.info("[ReservationService] reservationDTO : {}", reservationDTO);
		
		
		Reservation oriReservation = reservationRepository.findById(reservationDTO.getReservationNo()).orElseThrow(
					() -> new IllegalArgumentException("해당 예약 목록이 없습니다. reservationNo =" + reservationDTO.getReservationNo()));
			
		oriReservation.update(
				reservationDTO.getReservationUseTime(),
				reservationDTO.getReservationStatus(),
				reservationDTO.getReservationPurpose(),
				reservationDTO.getReservationTimeIn(),
				reservationDTO.getReservationTimeOut());

		reservationRepository.save(oriReservation);
		
		log.info("[ReservationService] updateReservation End============");
		
		return reservationDTO;
	}

	/* 6. 회의실 예약 전체 목록 조회(관리자) */
	public Page<ReservationDTO> selectReservationListForAdmin(int page) {
		log.info("[ReservationService] selectReservationList start============");
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("reservationNo").descending());
		
		Page<Reservation> reservationList = reservationRepository.findAll(pageable);
		Page<ReservationDTO> reservationDTOList = reservationList.map(reservation -> modelMapper.map(reservation, ReservationDTO.class));
		
		
		log.info("[ReservationService] reservationDTOList : {}", reservationDTOList.getContent());
		log.info("[ReservationService] selectReservationList End============");
		
		return reservationDTOList;
	}

	/* 7. 회의실 예약 상세 조회(관리자)*/
	public ReservationDTO selectReservationForAdmin(Long reservationNo) {
		log.info("[ReservationService] selectReservation start============");
		log.info("[ReservationService] reservationNo : {}", reservationNo);
		
		Reservation reservation = reservationRepository.findByReservationNo(reservationNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 예약사항이 없습니다. reservationNo=" + reservationNo));
		
		ReservationDTO reservationDTO = modelMapper.map(reservation, ReservationDTO.class);
		
		
		log.info("[ReservationService] reservationDTO : {}", reservationDTO);
		
		log.info("[ReservationService] selectReservation End============");
		return reservationDTO;
	}
	
	/* 8. 회의실 취소/삭제(관리자) */
	public void deleteReservationForAdmin(Long reservationNo) {
		Reservation deleteReservation = reservationRepository.findById(reservationNo).get();
		reservationRepository.delete(deleteReservation);
		
	}
	
	

	
	
	

}
