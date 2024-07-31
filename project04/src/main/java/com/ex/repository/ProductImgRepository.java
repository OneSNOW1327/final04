package com.ex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ProductImgEntity;

public interface ProductImgRepository extends JpaRepository<ProductImgEntity, Integer> {
	  
	   List<ProductImgEntity> findByProductIdOrderByIdAsc(Integer productId);
}
