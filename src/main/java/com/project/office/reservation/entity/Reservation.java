package com.project.office.reservation.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.project.office.member.entity.Member;
import com.project.office.room.entity.Room;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBL_RESERVATION")
@SequenceGenerator(name = "RESERVATION_SEQ_GENERATOR",
					sequenceName = "SEQ_RESERVATION_NO",
					initialValue = 9,
					allocationSize = 1)
@DynamicInsert
public class Reservation {

	@Id
	@Column(name = "RESERVATION_NO")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESERVATION_SEQ_GENERATOR")
	private Long reservationNo;
	
	@Column(name = "RESERVATION_USE_TIME")
	private Integer reservationUseTime;
	
	@Column(name = "RESERVATION_DATE")
	private LocalDateTime reservationDate;
	
	@Column(name = "RESERVATION_STATUS")
	private String reservationStatus;
	
	@Column(name = "RESERVATION_PURPOSE")
	private String reservationPurpose;
	
	@ManyToOne
	@JoinColumn(name = "ROOM_NO")
	private Room room;
	
	@ManyToOne
	@JoinColumn(name = "MEMBER_NO")
	private Member member;
	
	@Column(name = "RESERVATION_TIME_IN")
	private String reservationTimeIn;
	
	@Column(name = "RESERVATION_TIME_OUT")
	private String reservationTimeOut;

	public void update(Integer reservationUseTime , String reservationStatus,
			String reservationPurpose, String reservationTimeIn, String reservationTimeOut) {
		
		this.reservationUseTime = reservationUseTime;
		this.reservationStatus = reservationStatus;
		this.reservationPurpose = reservationPurpose;
		this.reservationTimeIn = reservationTimeIn;
		this.reservationTimeOut = reservationTimeOut;
		
	}

		
	
	
}
