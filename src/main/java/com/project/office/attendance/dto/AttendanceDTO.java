package com.project.office.attendance.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.office.member.dto.MemberDTO;

import lombok.Data;

@Data
public class AttendanceDTO {
	
	private Long attNo;
	@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime attIn;
	@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime attOut;
	private String attDate;
	private String attType;
	private String attTime;
	private MemberDTO member;
}
