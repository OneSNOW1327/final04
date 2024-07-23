package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.OrderlistEntity;

public interface OrderlistRepository extends JpaRepository<OrderlistEntity, Integer> {
}
