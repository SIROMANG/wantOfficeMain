package com.project.office.approval.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.office.approval.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long>{

	@Query("select d "
			+ "from Document d "
			+ "where "
			+ "d.member.memberNo =:memberNo")
	Page<Document> findAll(Pageable page, @Param("memberNo")Long memberNo);

	@Query("select d "
			+ "from Document "
			+ "d where d.docNo =:docNo" )
	Optional<Document> findBydocNo(@Param("docNo") Long docNo);
	
	

	
	
}
