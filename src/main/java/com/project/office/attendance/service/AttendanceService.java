package com.project.office.attendance.service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.office.attendance.dto.AttendanceDTO;
import com.project.office.attendance.entity.Attendance;
import com.project.office.attendance.repository.AttendanceRepository;
import com.project.office.member.dto.MemberDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	private final ModelMapper modelMapper;
	
	public AttendanceService(AttendanceRepository attendanceRepository, ModelMapper modelMapper) {
		this.attendanceRepository = attendanceRepository;
		this.modelMapper = modelMapper;
	}
	
	/* 출근 등록 */
	@Transactional
	public AttendanceDTO insertAttIn(AttendanceDTO attendanceDTO) throws ParseException {
		
		log.info("[AttendanceService] insertAttIn Start ====================");
		log.info("[AttendanceService] attendanceDTO : {}", attendanceDTO);
		
		attendanceRepository.save(modelMapper.map(attendanceDTO, Attendance.class));
		
		log.info("[AttendanceService] insertAttIn End ====================");
	
		return attendanceDTO;
		
	}
	
	/* 출퇴근 조회 */
	public AttendanceDTO selectAtt(MemberDTO member) {
		
		log.info("[AttendanceService] selectAtt Start ====================");
		log.info("[AttendanceService] member : {}", member);
		
		Long memberNo = member.getMemberNo();
		
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
		
		Attendance attendance = attendanceRepository.findByMemberAndAttInAndAttOut(start, end, memberNo)
				.orElseThrow(() -> new IllegalArgumentException("오늘 출근 시간이 존재하지 않습니다."));
		
		log.info("[AttendanceService] selectAtt End ====================");
		
		return modelMapper.map(attendance, AttendanceDTO.class);
		
	}

	/* 퇴근 등록 */
	@Transactional
	public AttendanceDTO insertAttOut(AttendanceDTO attendanceDTO,LocalDateTime now) throws ParseException {
		
		log.info("[AttendanceService] insertAttOut Start ====================");
		log.info("[AttendanceService] attendanceDTO : {}", attendanceDTO);
		
		// LocalDateTime 타입으로 비교
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime start = LocalDate.now().atStartOfDay();
		
		// Member와 오늘 날짜로 출근 찍힌 행 기준으로 조회
		Attendance oriAttendance = attendanceRepository.findByMemberAndAttIn(today, start, attendanceDTO.getMember().getMemberNo())
				.orElseThrow(() -> new IllegalArgumentException("오늘 출근 시간이 존재하지 않습니다."));
		
		oriAttendance.setAttOut(now);
		
		// 출근 시간과 퇴근 시간
		LocalDateTime In = oriAttendance.getAttIn();
		LocalDateTime Out = oriAttendance.getAttOut();
		
		int InHour = In.getHour();
		int InMinute = In.getMinute();
		
		int OutHour = Out.getHour();
		int OutMinute = Out.getMinute();
		
		// 출근 시간과 퇴근 시간 차이로 근무시간 구하기
		Duration duration = Duration.between(In, Out);
		Long time = duration.getSeconds();
		Long hour = time / 3600;
		Long minute = time % 3600 / 60;
		
		String attTime = (hour + "시간 " + minute + "분");
		
		oriAttendance.setAttTime(attTime);
		
		// 출근 시간과 퇴근 시간으로 근무유형 구하기
		LocalTime inTime = LocalTime.of(InHour, InMinute);
		LocalTime outTime = LocalTime.of(OutHour, OutMinute);
		LocalTime late = LocalTime.of(9, 30);
		LocalTime earlyLeave = LocalTime.of(17, 00);
		
		if(inTime.isAfter(late)) {
			oriAttendance.setAttType("지각");
		} else if(outTime.isBefore(earlyLeave)) {
			oriAttendance.setAttType("조퇴");
		} else {
			oriAttendance.setAttType("정상출근");
		}
		
		log.info("[AttendanceService] oriAttendance : {}", oriAttendance);
		log.info("[AttendanceService] setAttOut : {}", oriAttendance.getAttOut());
		
		attendanceRepository.save(oriAttendance);
		
		log.info("[AttendanceService] insertAttOut End ====================");
		
		return modelMapper.map(oriAttendance, AttendanceDTO.class);
		
	}
	
	/* 내 근태 월별 목록 조회 */
	public List<AttendanceDTO> getMyAttList(MemberDTO member, int year, int month) {
		
		log.info("[AttendanceService] getMyAttList Start ====================");
		
		Long memberNo = member.getMemberNo();
		
		
		String str = year + "-" + (month < 10 ? "0"+month : month) + "-01";
		log.info("[AttendanceService] yearMonth : {}", str);
		
		LocalDate date = LocalDate.parse(str);
		YearMonth yearMonth = YearMonth.from(date);
		
		LocalDate firstDate = yearMonth.atDay(1);
		LocalDate lastDate = yearMonth.atEndOfMonth();
		
		String firstDateString = firstDate.toString();
		String lastDateString = lastDate.toString();
		
		List<Attendance> attendanceList = attendanceRepository.findByAttDateMonth(memberNo, firstDateString, lastDateString);
		List<AttendanceDTO> attendanceDTOList = attendanceList.stream().map(attendance -> modelMapper.map(attendance, AttendanceDTO.class)).collect(Collectors.toList());
		
		log.info("[AttendanceService] attendanceList : {}", attendanceList);
		log.info("[AttendanceService] attendanceDTOList : {}", attendanceDTOList);
		
		log.info("[AttendanceService] getMyAttList End ====================");
		
		return attendanceDTOList;
		
	}

	/* 날짜별 근태 목록 조회(관리자) */
	public Page<AttendanceDTO> getAttListForAdmin(int page, String attDate) {
		
		log.info("[AttendanceService] getAttListForAdmin Start ====================");
		
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("member").ascending());
		
		Page<Attendance> attendanceList = attendanceRepository.findByAttDate(pageable, attDate);
		Page<AttendanceDTO> attendanceDTOList = attendanceList.map(attendance -> modelMapper.map(attendance, AttendanceDTO.class));
		
		log.info("[AttendanceService] attendanceDTOList : {}, attendanceDTOList");
		
		log.info("[AttendanceService] getAttListForAdmin End ====================");
		
		return attendanceDTOList;
		
	}


}
