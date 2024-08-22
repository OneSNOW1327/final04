package com.ex.repository;

import com.ex.entity.FaqEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntity, Long> {

	List<FaqEntity> findAll();
	
}
