package com.project.office.dept.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBL_DEPT")
@SequenceGenerator(name = "DETP_SEQ_GENERATOR", sequenceName = "SEQ_DEPT_NO", initialValue = 1, allocationSize = 1)
@DynamicInsert
public class Dept {

	@Id
	@Column(name = "DEPT_NO")
	private Long deptNo;
	
	@Column(name = "DEPT_NAME")
	private String deptName;
	
	@Column(name = "DEPT_STATUS")
	private String deptStatus;
	
	@Column(name = "DEPT_DATE")
	private java.util.Date deptDate;
}