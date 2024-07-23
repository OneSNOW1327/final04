package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.BasketEntity;

public interface BasketRepository extends JpaRepository<BasketEntity, Integer> {
}
