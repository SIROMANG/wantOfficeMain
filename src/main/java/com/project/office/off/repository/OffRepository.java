package com.project.office.off.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.office.off.entity.Off;

public interface OffRepository extends JpaRepository<Off, Long> {

	/* 연차 조회 */
	@EntityGraph(attributePaths= {"member"})
	@Query("select o from Off o where o.member.memberNo = :memberNo")
	Page<Off> findByMember(@Param("memberNo") Long memberNo, Pageable pageable);

	/* 결과별 연차 신청 목록 조회(결재권자) */
	@Query("select o from Off o where o.offResult = :offResult and o.member.dept.deptNo = :deptNo")
	Page<Off> findByOffResult(@Param("deptNo") Long deptNo, @Param("offResult") String offResult, Pageable pageable);

	/* 연차 수정을 위한 조회 */
	@EntityGraph(attributePaths= {"member"})
	@Query("select o from Off o where o.member.memberNo = :memberNo and o.offNo = :offNo")
	Optional<Off> findByMemberAndOffNo(@Param("memberNo") Long memberNo, @Param("offNo") Long offNo);

	/* Calendar 에서 부서원 연차 조회 */
	@Query(value = "SELECT o FROM Off o WHERE o.member.dept.deptNo = :deptNo")
	List<Off> findByDept(@Param("deptNo") Long deptNo);
}
