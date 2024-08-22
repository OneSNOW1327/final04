package com.ex.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.NoticeEntity;

public interface NoticeRepository  extends JpaRepository<NoticeEntity, Integer> {
	
    Page<NoticeEntity> findAll(Specification<NoticeEntity> spec, Pageable pageable);

    Page<NoticeEntity> findAll(Pageable pageable);
    
    NoticeEntity findByMain(int main); // 메인이 체크된 공지사항 가져옴
    
}
