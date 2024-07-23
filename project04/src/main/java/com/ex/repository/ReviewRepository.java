package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
}
