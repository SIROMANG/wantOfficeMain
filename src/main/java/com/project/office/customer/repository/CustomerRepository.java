package com.project.office.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.office.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	/* 거래처 명함 조회(부서 공유 거래처 포함) */
	@EntityGraph(attributePaths= {"member"})
	@Query("select c from Customer c where (c.member.memberNo = :memberNo) or (c.member.dept.deptNo = :deptNo and c.customerShare = 'Y')")
	Page<Customer> findByMemberAndDeptAndShare(@Param("memberNo") Long memberNo, @Param("deptNo") Long deptNo, Pageable pageable);
	
}
