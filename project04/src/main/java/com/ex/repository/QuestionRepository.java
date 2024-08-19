package com.ex.repository;

import com.ex.entity.QuestionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
	// 특정 사용자가 작성한 문의 목록을 사용자 이름으로 조회하는 메서드
    List<QuestionEntity> findByUser_Username(String username);
}
