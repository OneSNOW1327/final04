package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.DeliveryEntity;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Integer> {
	
	
}
