package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
	
}
