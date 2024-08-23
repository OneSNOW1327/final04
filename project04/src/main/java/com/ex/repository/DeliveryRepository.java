package com.ex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.DeliveryEntity;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Integer> {
	
	List<DeliveryEntity> findAllByUserIdOrderByIdDesc(Integer userId);
	
}
