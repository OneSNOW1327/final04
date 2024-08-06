package com.ex.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.SalesVolumeEntity;


public interface SalesVolumeRepository extends JpaRepository<SalesVolumeEntity, Integer> {
	
	Optional<SalesVolumeEntity> findByProductIdAndRecordDate(Integer productId,LocalDate recordDate);
	
	List<SalesVolumeEntity> findByProductIdOrderByRecordDateDesc(Integer productId);
	
}
