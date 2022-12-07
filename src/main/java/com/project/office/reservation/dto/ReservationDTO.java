package com.project.office.reservation.dto;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.office.member.dto.MemberDTO;
import com.project.office.room.dto.RoomDTO;

import lombok.Data;

@Data
public class ReservationDTO {

	private Long reservationNo;
	private Integer reservationUseTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime reservationDate;
	private String reservationStatus;
	private String reservationPurpose;
	private RoomDTO room;
	private MemberDTO member;
	private String reservationTimeIn;
	private String reservationTimeOut;

	

	
}
