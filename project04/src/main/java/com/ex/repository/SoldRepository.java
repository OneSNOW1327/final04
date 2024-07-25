package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ex.entity.SoldEntity;

public interface SoldRepository extends JpaRepository<SoldEntity, Integer> {
}
