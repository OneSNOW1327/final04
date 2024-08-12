package com.ex.repository;

import com.ex.entity.FaqEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntity, Long> {
    
    // 'writer' 필드를 기준으로 FAQ를 조회하는 메서드
    Optional<FaqEntity> findByWriter(String writer);
}
