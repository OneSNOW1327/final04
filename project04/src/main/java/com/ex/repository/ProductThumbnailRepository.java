package com.ex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ProductThumbnailEntity;

public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnailEntity, Integer> {
	
	List<ProductThumbnailEntity> findByProductIdOrderByIdAsc(Integer productId);
}
