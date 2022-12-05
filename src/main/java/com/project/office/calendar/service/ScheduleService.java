package com.project.office.calendar.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.project.office.calendar.dto.ScheduleDTO;
import com.project.office.calendar.entity.Schedule;
import com.project.office.calendar.repository.ScheduleRepository;
import com.project.office.member.dto.MemberDTO;
import com.project.office.member.entity.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleService {
	
	private ScheduleRepository scheduleRepository;
	private ModelMapper modelMapper;

	public ScheduleService (ScheduleRepository scheduleRepository, ModelMapper modelMapper) {
		this.scheduleRepository = scheduleRepository;
		this.modelMapper = modelMapper;
	}
	
	public List<ScheduleDTO> scheduleList(MemberDTO member) {
		
//		log.info("[ScheduleService] scheduleSort : {} ", scheduleDTO );
		
		List<Schedule> scheduleList = scheduleRepository.findByMember(modelMapper.map(member, Member.class));

//		log.info("[ScheduleService] scheduleList : {} ", scheduleList );
		
//		List<ScheduleDTO> test = scheduleList.stream().map(schedule -> modelMapper.map(schedule, ScheduleDTO.class)).collect(Collectors.toList());
		
//		log.info("[ScheduleService] test : {}", test);
		
		return scheduleList.stream().map(schedule -> modelMapper.map(schedule, ScheduleDTO.class)).collect(Collectors.toList());
	}

	public ScheduleDTO selectschedule(Long scheduleNo) {

//		log.info("[ScheduleService] scheduleSort : {} ", scheduleNo );
		
		Schedule schedule = scheduleRepository.findByScheduleNo(scheduleNo);
		
//		log.info("[ScheduleService] schedule : {} ", schedule );
		
		schedule.setScheduleStart((schedule.getScheduleStart()).substring(0, 10));
		schedule.setScheduleEnd((schedule.getScheduleEnd()).substring(0, 10));
		
		return modelMapper.map(schedule, ScheduleDTO.class);
	}
	
	@Transactional
	public ScheduleDTO insertSchedule(ScheduleDTO scheduleDTO) {

		log.info("[ScheduleService] schedule : {} ", scheduleDTO );
		
		scheduleRepository.save(modelMapper.map(scheduleDTO, Schedule.class));
		
		return scheduleDTO;
	}

	@Transactional
	public ScheduleDTO updateSchedule(ScheduleDTO scheduleDTO) {

		log.info("[ScheduleService] scheduleDTO : {}", scheduleDTO);
		
		Schedule newSchedule = scheduleRepository.findByScheduleNo(scheduleDTO.getScheduleNo());
		
//		ScheduleDTO updateSchedule = modelMapper.map(newSchedule, ScheduleDTO.class);
		
		newSchedule.update(scheduleDTO.getScheduleTitle(),
				scheduleDTO.getScheduleStart(),
				scheduleDTO.getScheduleEnd(),
				scheduleDTO.getScheduleSort(),
				scheduleDTO.getScheduleColor(),
				scheduleDTO.getSchedulePlace(),
				scheduleDTO.getScheduleContent());
		
		log.info("[ScheduleService] newSchedule : {}", newSchedule);
		
		scheduleRepository.save(newSchedule);
		
		return scheduleDTO;
	}

	public Object deleteSchedule(Long scheduleNo) {
		
		scheduleRepository.delete(scheduleRepository.findByScheduleNo(scheduleNo));

		return null;
	}

}
