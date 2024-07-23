package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.SalesVolumeEntity;


public interface SalesVolumeRepository extends JpaRepository<SalesVolumeEntity, Integer> {
}
