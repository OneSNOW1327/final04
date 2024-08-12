package com.ex.repository;

import com.ex.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
	
    List<ReviewEntity> findByProduct_Id(Integer productId); // 상품 ID로 리뷰 검색

    Optional<ReviewEntity> findByProduct_IdAndAdopted(Integer productId,int adopted);
}
