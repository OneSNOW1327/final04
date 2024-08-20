package com.ex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.OrderlistEntity;

public interface OrderlistRepository extends JpaRepository<OrderlistEntity, Integer> {
	
	List<OrderlistEntity> findByTid(String tid);
	
}
