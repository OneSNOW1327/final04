package com.ex.repository;

import com.ex.entity.ReviewImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImgRepository extends JpaRepository<ReviewImgEntity, Integer> {
    List<ReviewImgEntity> findByReview_Id(Integer reviewId); // 리뷰 ID로 이미지 검색
}
