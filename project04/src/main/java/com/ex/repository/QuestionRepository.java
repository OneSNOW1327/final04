package com.ex.repository;

import com.ex.entity.QuestionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
	List<QuestionEntity> findAllByUserUsername(String username);
}
