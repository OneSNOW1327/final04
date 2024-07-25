package com.ex.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.ProducttypeEntity;

public interface ProducttypeRepository extends JpaRepository<ProducttypeEntity, Integer> {
	
}