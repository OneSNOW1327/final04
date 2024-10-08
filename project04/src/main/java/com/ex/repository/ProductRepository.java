package com.ex.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ProductEntity;
 
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
	  
	Page<ProductEntity> findAll(Specification<ProductEntity>spec, Pageable pageable);
	
	Page<ProductEntity> findByTypeId(Integer tpyeId, Pageable pageable);
	
	List<ProductEntity> findAllByStock(int stock);
	
	List<ProductEntity> findAllByStockLessThanEqual(int stock);
}
