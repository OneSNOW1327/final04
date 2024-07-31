package com.ex.repository;

import com.ex.entity.ProductThumbnailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
  
@Repository
public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnailEntity, Integer> {
    List<ProductThumbnailEntity> findByProductIdOrderByIdAsc(Integer productId); 
}
