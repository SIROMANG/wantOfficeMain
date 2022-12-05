package com.project.office.approval.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.project.office.approval.dto.FormDTO;
import com.project.office.approval.entity.Form;
import com.project.office.approval.repository.FormRepository;



@Service
public class FormService {
	
	
	private FormRepository formRepository;
	private final ModelMapper modelMapper;

	
	public FormService(FormRepository formRepositor, ModelMapper modelMapper) {
		this.formRepository = formRepositor;
		this.modelMapper = modelMapper;
	}
	
	
	
	
	public List<FormDTO> formList() {		
		
		List<Form> form = formRepository.findAll();
	
		return form.stream().map(f -> modelMapper.map(f, FormDTO.class)).collect(Collectors.toList());
}
	

}
