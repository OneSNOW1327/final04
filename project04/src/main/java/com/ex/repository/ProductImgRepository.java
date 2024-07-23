package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ProductImgEntity;

public interface ProductImgRepository extends JpaRepository<ProductImgEntity, Integer> {
}
