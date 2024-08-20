package com.ex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.BasketEntity;

public interface BasketRepository extends JpaRepository<BasketEntity, Integer> {
	
	Optional<BasketEntity> findByUserIdAndProductId(Integer userId, Integer productId);
	List<BasketEntity> findAllByUserIdOrderByIdDesc(Integer userId);
	
}
