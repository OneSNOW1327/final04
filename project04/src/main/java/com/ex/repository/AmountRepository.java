package com.ex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.AmountEntity;

public interface AmountRepository extends JpaRepository<AmountEntity,Integer>{

}
