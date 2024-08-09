package com.ex.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.AmountEntity;

public interface AmountRepository extends JpaRepository<AmountEntity,Integer>{
	
	List<AmountEntity> findAllByReasonOrderByRecordDateAsc(String reason);
	
	List<AmountEntity> findAllByOrderByRecordDateDesc();
	
	Optional<AmountEntity> findByReasonAndRecordDateBetweenOrderByRecordDateDesc(String reason, LocalDateTime startOfDay, LocalDateTime endOfDay);
	
	
}
